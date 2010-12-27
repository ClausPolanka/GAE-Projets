package com.swagswap.dao;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserServiceFactory;
import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.domain.SwagImage;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;


public class ItemDaoImplTest extends LocalDatastoreTestCase  {
	
	private ItemDaoImpl itemDao;
	
	@Override
    public void setUp() throws Exception {
        super.setUp();
        if (itemDao == null) {
    		ItemDaoImpl itemDao = new ItemDaoImpl();
            itemDao.setPersistenceManagerFactory(PMF);
    		this.itemDao=itemDao;
        }
	}

    public void testInsert() {
        
        SwagItem swagItem = Fixture.createSwagItem();
        itemDao.insert(swagItem);

        assertNumberOfItemsAndImages(1,1);    
    }
    
    //TODO why doesn't this work?
    public void testUpdate() {
        SwagItem orig = Fixture.createSwagItem();
        itemDao.insert(orig);
        
        orig.setName("new name");
        itemDao.update(orig);
        
        SwagItem retrieved = itemDao.get(orig.getKey());
        assertEquals(orig.getName(), retrieved.getName());
    }
    
    /**
     * Make sure if there's no image, that an empty one is created
     * Otherwise with JDO 1-to-1 owned relationships you can't
     * add one later.
     */
    public void testSaveNoImage() {
    	
    	SwagItem swagItem = Fixture.createSwagItemNoImage();
    	itemDao.insert(swagItem);
    	
    	assertNumberOfItemsAndImages(1,1);
    }
    
    //TODO Why is this failing?
    public void testSaveReplaceImage() {
    	
    	SwagItem swagItem1 = Fixture.createSwagItem();
    	itemDao.insert(swagItem1);
    	SwagImage oldImage=swagItem1.getImage();
    	
    	//Make a new SwagItem so the images reference aren't pointing to the same thing
    	SwagItem swagItem2 = itemDao.get(swagItem1.getKey());
    	//This is how we indicate a new image is coming in
    	swagItem2.setImageBytes(new byte[]{8,7,6,5,4,3,2,1});
    	itemDao.insert(swagItem2);
    	swagItem2 = itemDao.get(swagItem1.getKey());
    	SwagImage newImage = swagItem2.getImage();
    	
    	assertNotEquals(oldImage, newImage);
    	
    }
    
    // Strings over 500 chars have to be stored in a 
    // com.google.appengine.api.datastore.Text
    public void testLargeDescription() {
    	
    	SwagItem originalItem = Fixture.createSwagItem();
    	String randomDescription = Fixture.get510Chars();
    	originalItem.setDescription(randomDescription);
    	itemDao.insert(originalItem);
    	assertNumberOfItemsAndImages(1,1);  
    	
        SwagItem retrievedItem = itemDao.get(originalItem.getKey());
        
    	assertEquals(randomDescription, retrievedItem.getDescription());
    }
    
    /**
     * Make sure all fields are saved and that (owned one-to-many relationship)
     * SwagItem is lazy loaded
     */
    public void testGet() {
    	
        SwagItem originalItem = Fixture.createSwagItem();
        itemDao.insert(originalItem);
        
        SwagItem retrievedItem = itemDao.get(originalItem.getKey());
        assertEquals(originalItem,retrievedItem);
        
        //Image is lazy loaded but make sure it's available when we access it
        SwagImage image = retrievedItem.getImage();
        assertNotNull(image);
    }
    
    /**
     * Should be ordered by lastUpdated timestamp
     * @throws Exception 
     */
    public void testGetAllOrdering() throws Exception {
    	
        SwagItem item0 = Fixture.createSwagItem();
        item0.setName("b");
        itemDao.insert(item0);
        
        Thread.currentThread().sleep(1000); //make it wait so they have different timestamps
        SwagItem item1 = Fixture.createSwagItem();
        item1.setName("a");
        itemDao.insert(item1);
        
        Thread.currentThread().sleep(1000); //make it wait so they have different timestamps
        SwagItem item2 = Fixture.createSwagItem();
        item2.setName("c");
        itemDao.insert(item2);
        
        List<SwagItem> items = itemDao.getAll();
        
        assertEquals(item2, items.get(0));
        assertEquals(item1, items.get(1));
        assertEquals(item0, items.get(2));
        
        //TODO why is this not working?
//        //now save the middle one and hope that it comes out first
//        itemDao.save(item1);
//        items = itemDao.getAll();
//        
//        assertEquals(item1, items.get(0));
        
    }  
    
    // This is testing the JDO framework for fun
    public void testGetNonExistantItem() {
    	try {
    		itemDao.get(1000L); //assume non-existant key
    		fail("Should have thrown a JDOObjectNotFoundException");
    	} catch (JDOObjectNotFoundException e) {
    		//good
    	}
    }

    /**
     * Make sure SwagImages are deleted too!
     */
    public void testDelete() {
        
        SwagItem swagItem = Fixture.createSwagItem();
        
        itemDao.insert(swagItem);
        itemDao.delete(swagItem.getKey());
        
        assertNumberOfItemsAndImages(0,0);
     }
    
    public void testAddComment() {
    	
    	SwagItem swagItem = Fixture.createSwagItem();
    	itemDao.insert(swagItem);
    	SwagItem retrievedItem = itemDao.get(swagItem.getKey());
    	String nickName = UserServiceFactory.getUserService().getCurrentUser().getNickname();
    	itemDao.addComment(new SwagItemComment(swagItem.getKey(),"1", nickName, "comment"));
    	//get it again
    	retrievedItem = itemDao.get(swagItem.getKey());
    	assertEquals(1, retrievedItem.getComments().size());
    }
    
    public void testFindByTag() {
        SwagItem swagItem = Fixture.createSwagItem();
        itemDao.insert(swagItem);
        List<SwagItem> swagItems = itemDao.findByTag((swagItem.getTags().get(0)));
        assertEquals(swagItem.getName(), swagItems.get(0).getName());
    }
    
    public void testFindByName() {
    	SwagItem swagItem = Fixture.createSwagItem();
    	itemDao.insert(swagItem);
    	List<SwagItem> swagItems = itemDao.findByName(swagItem.getName());
    	assertEquals(swagItem.getName(), swagItems.get(0).getName());
    }
    
    public void testSearch() {
    	SwagItem swagItem1 = Fixture.createSwagItem();
    	itemDao.insert(swagItem1);
    	
    	SwagItem swagItem2 = Fixture.createSwagItem();
    	swagItem2.setName("name2"); //different name than swagItem1
    	//make tag with the same name as item 1
    	swagItem2.getTags().add(swagItem1.getName());
    	itemDao.insert(swagItem2);
    	
    	List<SwagItem> swagItems = itemDao.search(swagItem1.getName());
    	//expect a hit on swagItem1.name and swagItem2.tag
    	assertEquals(2, swagItems.size());
    }
    
    public void testSearch_ensure_no_duplicates() {
    	//make item with a tag that matches name
    	SwagItem swagItem = Fixture.createSwagItem();
    	swagItem.getTags().add(swagItem.getName());
    	itemDao.insert(swagItem);
    	
    	List<SwagItem> swagItems = itemDao.search(swagItem.getName());
    	assertEquals(1, swagItems.size());
    }
    
    // If all tags were filled then empty search String 
    // didn't include item
    public void testEmptySearchString() {
    	SwagItem swagItem = Fixture.createSwagItem();
    	swagItem.getTags().add("another tag");
    	itemDao.insert(swagItem);
    	
    	assertEquals(1, itemDao.search("").size());
    }
    
    //TODO why isn't this working?
    public void testUpdateRating() {
    	SwagItem originalSwagItem = Fixture.createSwagItem();
    	itemDao.insert(originalSwagItem);
    	int firstRating = 1;
    	itemDao.updateRating(originalSwagItem.getKey(), firstRating, true); //new rating
    	SwagItem retrievedSwagItem = itemDao.get(originalSwagItem.getKey());
    	
    	//verify
    	assertEquals(firstRating, retrievedSwagItem.getAverageRating().floatValue());
    	assertEquals(1,retrievedSwagItem.getNumberOfRatings().intValue());
    	
    	//update existing rating
    	int secondRating = 2;
    	itemDao.updateRating(originalSwagItem.getKey(), secondRating, false); //new rating
    	retrievedSwagItem = itemDao.get(originalSwagItem.getKey());
    	assertEquals(secondRating, retrievedSwagItem.getAverageRating().floatValue());
    	//make sure there's still just one rating
    	assertEquals(1,retrievedSwagItem.getNumberOfRatings().intValue()); 
    }


    /**
     * Database count assertions
     * 
     * @param swagItemsExpected
     * @param swagImagesExpected
     */
	private void assertNumberOfItemsAndImages(int swagItemsExpected, int swagImagesExpected) {
		Query query = new Query(SwagItem.class.getSimpleName());
    	assertEquals(swagItemsExpected, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
    	
    	query = new Query(SwagImage.class.getSimpleName());
    	assertEquals(swagImagesExpected, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
	}
}
