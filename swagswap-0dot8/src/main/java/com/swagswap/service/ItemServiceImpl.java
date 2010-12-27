package com.swagswap.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.appengine.api.users.User;
import com.swagswap.dao.ImageDao;
import com.swagswap.dao.ItemDao;
import com.swagswap.domain.SwagImage;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.exceptions.AccessDeniedException;
import com.swagswap.exceptions.ImageTooLargeException;
import com.swagswap.exceptions.InvalidSwagImageException;
import com.swagswap.exceptions.InvalidSwagItemException;
import com.swagswap.exceptions.LoadImageFromURLException;

/**
 * For transactionality and will be used for caching.
 * 
 * @author sam
 * 
 */
public class ItemServiceImpl implements ItemService {

	private static final Logger log = Logger.getLogger(ItemServiceImpl.class);

	private ItemDao itemDao;

	@Autowired
	private ImageService imageService;

	@Autowired
	private SwagSwapUserService swagSwapUserService; // for saving users to our

	@Autowired
	private OutgoingMailService outgoingMailService;
	// app
	// for checking mime type
	private Magic jmimeMagicParser = new Magic();

	public ItemServiceImpl() {
	}

	// for unit tests
	protected ItemServiceImpl(ItemDao itemDao, ImageDao imageDao) {
		this.itemDao = itemDao;
	}
	
	//for unit tests
	protected void setImageService(ImageService imageService) {
		this.imageService=imageService;
	}

	/**
	 * Load swagItem, but not associated swagImage
	 */
	public SwagItem get(Long id) {
		return get(id, false);
	}

	/**
	 * 
	 * @param id
	 * @param loadSwagImage
	 *            whether to load swagImage (it is lazy loaded by JDO)
	 * @return SwagItem if found
	 * @throws Exception
	 *             if item not found
	 */
	public SwagItem get(Long id, boolean loadSwagImage) {
		return itemDao.get(id, loadSwagImage);
	}

	// GAE doesn't support case-insensitive queries (yet)
	// Easiest way to do it is to code our way out of it
	public List<SwagItem> search(String queryString) {
		return search(itemDao.getAll(), queryString);
	}

	public List<SwagItem> search(List<SwagItem> swagList, String queryString) {
		if (queryString==null) {
			return swagList;
		}
		List<SwagItem> searchResults = new ArrayList<SwagItem>();
		//  Put all searchable fields into a StringBuffer and search it.
		for (SwagItem swagItem : swagList) {
			StringBuffer searchMe = new StringBuffer();
			searchMe.append(swagItem.getName()+"/");
			searchMe.append(swagItem.getDescription()+"/");
			searchMe.append(swagItem.getCompany()+"/");
			for (String tag : swagItem.getTags()) {
				searchMe.append(tag+"/");
			}
			if (searchMe.toString().toUpperCase().contains(queryString.toUpperCase())) {
				searchResults.add(swagItem);
			}
		}
		return searchResults;
	}

	public List<SwagItem> getAll() {
		return itemDao.getAll();
	}

	public List<SwagItem> findByTag(String searchString) {
		return itemDao.findByTag(searchString);
	}

	public List<SwagItem> filterByRated(List<SwagItem> swagList,
			SwagSwapUser user, boolean exclusive) {
		// Easier and faster to do this programatically than go to DAO
		List<SwagItem> filteredList = new ArrayList<SwagItem>();
		if (swagList == null || user == null) {
			return filteredList;
		}
		Set<SwagItemRating> userRatingList = user.getSwagItemRatings();
		if (userRatingList.size() == 0) {
			// Performance improvement. No ratings from this user so we return
			// complete swagList
			return swagList;
		}

		itemLoop: for (SwagItem swagItem : swagList) {
			for (SwagItemRating rating : userRatingList) {
				if (rating.getSwagItemKey().equals(swagItem.getKey())) {
					if (!exclusive) {
						filteredList.add(swagItem);
						continue itemLoop;
					} else {
						continue itemLoop;
					}

				}
			}
			// No user ratings for item so meets exclusive criteria
			if (exclusive) {
				filteredList.add(swagItem);
			}
		}

		return filteredList;
	}

	public List<SwagItem> filterByOwnerGoogleID(List<SwagItem> swagList,
			String googleID) {
		// Easier and faster to do this programatically than go to DAO
		List<SwagItem> filteredList = new ArrayList<SwagItem>();
		if (swagList == null || googleID == null) {
			return filteredList;
		}
		for (SwagItem swagItem : swagList) {
			if (swagItem.getOwnerGoogleID() != null
					&& swagItem.getOwnerGoogleID().equals(googleID)) {
				filteredList.add(swagItem);
			}
		}
	
		return filteredList;
	}

	/**
	 * @param exclusive
	 *            The reverse of what it normally does
	 */
	public List<SwagItem> filterByCommentedOn(List<SwagItem> swagList,
			String googleID, boolean exclusive) {

		// Easier and faster to do this programatically than go to DAO
		List<SwagItem> filteredList = new ArrayList<SwagItem>();
		if (swagList == null || googleID == null) {
			return filteredList;
		}
		itemLoop: for (SwagItem swagItem : swagList) {
			for (SwagItemComment swagItemComment : swagItem.getComments()) {
				// Put this check in to defend against DB changes (like deleting
				// a user)
				if (swagItemComment.getItemOwnerGoogleID() != null) {
					if (swagItemComment.getItemOwnerGoogleID().equals(googleID)
							&& !exclusive) {
						filteredList.add(swagItem);
						continue itemLoop;
					}
					if ((!swagItemComment.getItemOwnerGoogleID().equals(
							googleID))
							&& exclusive) {
						filteredList.add(swagItem);
						continue itemLoop;
					}
				}
			}
			// No comments for Item so meets criteria if exclusive
			if (exclusive && swagItem.getComments().size() == 0) {
				filteredList.add(swagItem);
			}
		}
		return filteredList;
	}

	/**
	 * saves swag item and image (image is saved in dao because it's a child
	 * object) (A user is not associated with a swagitem via a JDO relationship
	 * because I couldn't get a many-to-one relationship going in JDO),
	 * 
	 * @throws IOException
	 * @throws MalformedURLException
	 * @return updated SwagItem (needed for SwagSwapGWT impl)
	 */

	//TODO should this be REQUIRES_NEW?
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public SwagItem save(SwagItem swagItem) throws LoadImageFromURLException,
			ImageTooLargeException, InvalidSwagImageException {
		if (StringUtils.isEmpty(swagItem.getName())) { // only required field
			throw new InvalidSwagItemException("name is required");
		}
		if (swagItem.isNew()) {
			SwagSwapUser swagSwapUser = swagSwapUserService
					.findByEmailOrCreate();
			swagItem.setOwnerGoogleID(swagSwapUser.getGoogleID());
			String currentUserNickName = swagSwapUser.getNickName();
			swagItem.setOwnerNickName(currentUserNickName);

			populateSwagImage(swagItem);

			itemDao.insert(swagItem);
			/**
			 * No need to create swagSwapUser here. We only need a user in our
			 * DB if they rate something (to remember their rating) Anyway, this
			 * call fails since it tries to update two different entity groups
			 * in one transaction
			 */
			// swagSwapUserService.findByEmailOrCreate(currentUserEmail);
		} else { // update
			checkPermissions(swagItem.getKey());
			populateSwagImage(swagItem);
			itemDao.update(swagItem);
		}

		// to test transactions, uncomment the throw exception line below
		// and try this method with and without the annotation
		// throw new RuntimeException("see if it rolls back");
		return swagItem;
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void saveFromEmail(SwagItem swagItem) {
		if (StringUtils.isEmpty(swagItem.getName())) { // only required field
			throw new InvalidSwagItemException("name is required");
		}
		// don't check logged in identity
		populateSwagImage(swagItem);
		itemDao.insert(swagItem);
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void updateRating(Long swagItemKey,
			int computedRatingDifference, boolean isNewRating) {
		itemDao.updateRating(swagItemKey, computedRatingDifference,isNewRating);
	}
	
	/**
	 * Takes into account previous user rating and updates item average rating
	 * @param previousRating can be null
	 * @param newSwagItemRating
	 */
	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void recomputeAndRecordSwagItemAverageRating(Integer previousRatingValue, SwagItemRating newSwagItemRating) {
		//start with new rating which will be used if this is their first rating of this item
		int computedRatingDifference = newSwagItemRating.getUserRating().intValue(); 
		boolean isNew=true;
		//If they have a previous rating, calculate the difference and update the total item rating with that.
		if (previousRatingValue!=null) {
			if (previousRatingValue.equals(newSwagItemRating.getUserRating())) {
				return; //they submitted the same rating as before
			}
			isNew=false;
			computedRatingDifference=newSwagItemRating.getUserRating().intValue()-previousRatingValue.intValue();
		}
		updateRating(newSwagItemRating.getSwagItemKey(),computedRatingDifference,isNew);
	}

	public void delete(Long id) {
		checkPermissions(id);
		itemDao.delete(id);
	}

	@Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
	public void addComment(SwagItemComment newComment) {
		if (StringUtils.isEmpty(newComment.getCommentText())) {
			return;
		}
		newComment.setSwagSwapUserNickname(swagSwapUserService.getCurrentUser()
				.getNickname());
		newComment.setItemOwnerGoogleID(swagSwapUserService.getCurrentUser()
				.getUserId()); // GoogleID
		itemDao.addComment(newComment);
		// send the owner a mail
		String subject = "Someone just commented on your swag item";
		String msgBody = newComment.getSwagSwapUserNickname()
			+ " said: "
				+ newComment.getCommentText()
				+ "\n\n<br/><br/>See Your Item here: (Spring MVC impl) http://swagswap.appspot.com/springmvc/view/"
				+ newComment.getSwagItemKey()
				+ "\n<br/>or here (JSF 2.0 impl) http://swagswap.appspot.com/jsf/viewSwag.jsf?swagItemKey="
				+ newComment.getSwagItemKey();
		outgoingMailService.sendWithTaskManager(newComment.getSwagItemKey(), subject,
				msgBody);
	}

	public void setItemDao(ItemDao itemDao) {
		this.itemDao = itemDao;
	}

	// for tests
	public void setSwagSwapUserService(SwagSwapUserService swagSwapUserService) {
		this.swagSwapUserService = swagSwapUserService;
	}

	protected void populateSwagImage(SwagItem swagItem)
			throws LoadImageFromURLException, ImageTooLargeException,
			InvalidSwagImageException {
		if (!swagItem.hasNewImage()) {
			return;
		}
		byte[] newImageData = null;
		if (swagItem.hasNewImageBytes()) {
			// TODO fix this comment
			// The following line only works for a save,
			// not an upate cause there you have to operate on the stored
			// SwagImage
			// orig.setImage(updatedItem.getImage();
			newImageData = swagItem.getImageBytes();
		} else if (swagItem.hasNewImageURL()) {
			newImageData = getImageDataFromURL(swagItem.getImageURL());
		}
		checkImageMimeType(newImageData);
		// Resize the image before saving
		swagItem.setImage(new SwagImage(imageService
				.getResizedImageBytes(newImageData)));
	}

	/**
	 * 
	 * public so that it can be used by AdminService
	 * 
	 * @return image data from swagItem
	 */
	public byte[] getImageDataFromURL(String imageURL)
			throws LoadImageFromURLException, ImageTooLargeException {
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			// fetch URL as InputStream
			URL url = new URL(imageURL);
			bis = new BufferedInputStream(url.openStream());
			// write it to a byte[] using a buffer since we don't know the exact
			// image size
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			int i = 0;
			while (-1 != (i = bis.read(buffer))) {
				bos.write(buffer, 0, i);
			}
			byte[] imageData = bos.toByteArray();
			if (imageData.length > 150000) {
				throw new ImageTooLargeException(url.toString(), 150000);
			}
			return imageData;
		} catch (IOException e) {
			throw new LoadImageFromURLException(imageURL, e);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * 
	 * Make sure they're uploading a picture
	 * 
	 * @param imageData
	 * @throws InvalidSwagImageException
	 */
	public void checkImageMimeType(byte[] imageData)
			throws InvalidSwagImageException {
		String mimeType;
		try {
			MagicMatch match = jmimeMagicParser.getMagicMatch(imageData);
			mimeType = match.getMimeType();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			throw new InvalidSwagImageException(e);
		}
		if (!(("text/html".equals(mimeType)) || ("image/gif".equals(mimeType))
				|| ("image/png".equals(mimeType)) || ("image/jpeg"
				.equals(mimeType)))) {
			throw new InvalidSwagImageException(mimeType);
		}
	}

	/**
	 * @param swagItemIdToCheck
	 * @throws AccessDeniedException
	 *             if currentUser is not allowed to access an item
	 */
	private void checkPermissions(Long swagItemIdToCheck)
			throws AccessDeniedException {
		User user = swagSwapUserService.getCurrentUser();
		// admins can do everything!
		if (swagSwapUserService.isUserAdmin()) {
			return;
		}
		// get item fresh to prevent client side attacks
		SwagItem swagItemToCheck = get(swagItemIdToCheck);
		// Their email doesn't match the swagItem
		// (again, if this happened the webapp messed up or someone's trying to
		// hack us)
		if (!user.getUserId().equals(swagItemToCheck.getOwnerGoogleID())) {
			throw new AccessDeniedException(swagItemToCheck.getName(),
					swagItemToCheck.getOwnerGoogleID(), user.getUserId());
		}
	}

}