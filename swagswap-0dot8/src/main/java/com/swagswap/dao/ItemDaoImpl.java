package com.swagswap.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.collections.list.SetUniqueList;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.orm.jdo.support.JdoDaoSupport;

import com.swagswap.domain.SwagImage;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;

/**
 * Persistence of SwagItems using Spring JDO
 * (from spring docs): JdoTemplate will ensure that PersistenceManagers are
 * properly opened and closed, and automatically participate in transactions.
 * 
 * No need for finally blocks that close the persistenceManager!
 * 
 */
@SuppressWarnings("unchecked")
public class ItemDaoImpl extends JdoDaoSupport implements ItemDao {

	private static final Logger log = Logger.getLogger(ItemDaoImpl.class);

	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#get(java.lang.Long)
	 */
	public SwagItem get(Long id) {
		return get(id, false);
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#get(java.lang.Long, boolean)
	 */
	public SwagItem get(Long id, boolean loadSwagImage) {
		SwagItem swagItem = getPersistenceManager().getObjectById(SwagItem.class, id);
		//  Had to add this or the comments weren't being loaded in Cache.  Scott.
		swagItem.getComments();
		if (loadSwagImage) {
			swagItem.getImage();
		}
		return swagItem;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#search(java.lang.String)
	 */
    public List<SwagItem> search(String searchString) {
    	if (StringUtils.isEmpty(searchString)) {
    		return getAll();
    	}
        List<SwagItem> tagResults = findByTag(searchString);
        List<SwagItem> nameResults = findByName(searchString);
        List<SwagItem> companyResults = findByCompany(searchString);
        List<SwagItem> descriptionResults = findByDescription(searchString);
        //commons-lang trickery to ensure uniqueness in a list
        List<SwagItem> allResults = SetUniqueList.decorate(new ArrayList<SwagItem>());
        allResults.addAll(tagResults);
        allResults.addAll(nameResults);
        allResults.addAll(companyResults);
        allResults.addAll(descriptionResults);
        return allResults;
    }


    // Figured these out from this page:
    // http://code.google.com/p/datanucleus-appengine/source/browse/trunk/tests/org/datanucleus/store/appengine/query/JDOQLQueryTest.java#2178
	public List<SwagItem> findByTag(String searchString) {
		Query query = getPersistenceManager().newQuery(
                "select from " + SwagItem.class.getName()
                + " where tags.contains(p1) parameters String p1");
        return (List<SwagItem>) query.execute(searchString); 
	}
	
	protected List<SwagItem> findByName(String searchString) {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagItem.class.getName()
				+ " where name==p1 parameters String p1");
		return (List<SwagItem>) query.execute(searchString); 
	}
	
	protected List<SwagItem> findByDescription(String searchString) {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagItem.class.getName()
				+ " where description==p1 parameters String p1");
		return (List<SwagItem>) query.execute(searchString); 
	}
	
	protected List<SwagItem> findByCompany(String searchString) {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagItem.class.getName()
				+ " where company==p1 parameters String p1");
		return (List<SwagItem>) query.execute(searchString); 
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#getAll()
	 */
	public List<SwagItem> getAll() {
		PersistenceManager pm = getPersistenceManager();
		String query = "select from " + SwagItem.class.getName() + " order by lastUpdated desc";
		List<SwagItem> swagItems = (List<SwagItem>) pm.newQuery(query).execute();
		logSwagItems(swagItems); //debugging output
		return swagItems;

	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#insert(com.swagswap.domain.SwagItem)
	 */
	public void insert(SwagItem swagItem) {
		Date now = new Date();
		swagItem.setCreated(now);
		swagItem.setLastUpdated(now);
		getPersistenceManager().makePersistent(swagItem);
		// Save image key here in the parent 
		// See comment in SwagItem above the field imageKey
		swagItem.setImageKey(swagItem.getImage().getEncodedKey());
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#update(com.swagswap.domain.SwagItem)
	 */
//	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void update(SwagItem updatedItem) {
		SwagItem orig = get(updatedItem.getKey(),true);
		orig.setName(updatedItem.getName());
		orig.setCompany(updatedItem.getCompany());
		orig.setDescription(updatedItem.getDescription());
		//These are done in exclusively in update rating
//		orig.setRating(updatedItem.getRating());
//		orig.setNumberOfRatings(updatedItem.getNumberOfRatings());
		orig.setLastUpdated(new Date());
		orig.setTags(updatedItem.getTags());
		//comments are managed through addComment()
//		orig.setComments(updatedItem.getComments());
		if (updatedItem.hasNewImage()) {
			orig.getImage().setImage(updatedItem.getImage().getImage());
		}
		//Tricky, this is for smartGWT impl that needs an updated average rating
		updatedItem.setAverageRating(orig.getAverageRating());
		updatedItem.setNumberOfRatings(orig.getNumberOfRatings());
		updatedItem.setLastUpdated(orig.getLastUpdated());
	}
	
	/**
	 * 
	 * @param calculatedNewRating the new rating for the swagItem. 
	 * @param newRating already computed for swagItem
	 * @param isNewRating if true then numberOfRatings is incremented
	 */
	//Yikes this is gonna be a bottleneck.  This is probably a job for the task manager
	public synchronized void updateRating(Long swagItemKey, int computedRatingDifference, boolean isNewRating) {
		SwagItem orig = get(swagItemKey);
		synchronized (orig) {
			float totalRatingPoints = orig.getAverageRating() * orig.getNumberOfRatings();
			if (isNewRating) {
				//this is persisted since swagItem is JDO connected
				orig.setNumberOfRatings(orig.getNumberOfRatings()+1); 
			}
			float computedTotalRating = 
				(totalRatingPoints + computedRatingDifference) / orig.getNumberOfRatings();
			orig.setAverageRating(computedTotalRating);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.ItemDao#delete(java.lang.Long)
	 */
	public void delete(Long id) {
		PersistenceManager pm = getPersistenceManager();
		SwagItem swagItem = pm.getObjectById(SwagItem.class, id);
		SwagImage swagImage = swagItem.getImage();
		pm.deletePersistent(swagImage);
		pm.deletePersistent(swagItem);
	}
	
	public void addComment(SwagItemComment newComment) {
		SwagItem orig = get(newComment.getSwagItemKey());
		Date now = new Date();
		newComment.setCreated(now);
		orig.setLastUpdated(now); //put recently commented item first
		synchronized (orig) {
			orig.getComments().add(newComment);
		}
	}

	private void logSwagItems(List<SwagItem> swagItems) {
		if (log.isDebugEnabled()) {
			log.debug("returning " + swagItems.size() + " swag items");
			for (SwagItem swagItem : swagItems) {
				log.debug(swagItem.getName() + " key: " + swagItem.getKey() + " Time: " + swagItem.getLastUpdated());
			}
		}
	}

}