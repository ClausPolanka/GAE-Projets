package com.swagswap.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.dao.ImageDao;
import com.swagswap.dao.ImageDaoImpl;
import com.swagswap.dao.ItemDao;
import com.swagswap.dao.ItemDaoImpl;
import com.swagswap.dao.UserDaoImpl;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.exceptions.InvalidSwagItemRatingException;

public class SwagSwapUserServiceImplIntegrationTest extends LocalDatastoreTestCase  {
	
	private UserDaoImpl userDao;
	private SwagSwapUserService swagSwapUserService;
	private ItemDao itemDao;
	private ImageDao imageDao;
	private ItemService itemService;
	private OutgoingMailService outgoingMailService;
	
	@Override
    public void setUp() throws Exception {
        super.setUp();
        if (userDao == null) {
    		UserDaoImpl userDao = new UserDaoImpl();
            userDao.setPersistenceManagerFactory(PMF);
    		this.userDao=userDao;
        }
        if (itemDao == null) {
        	ItemDaoImpl itemDao = new ItemDaoImpl();
        	itemDao.setPersistenceManagerFactory(PMF);
        	this.itemDao=itemDao;
        }
        if (imageDao == null) {
        	ImageDaoImpl imageDao = new ImageDaoImpl();
        	imageDao.setPersistenceManagerFactory(PMF);
        	this.imageDao=imageDao;
        }
        if (itemService==null) {
        	itemService = new ItemServiceImpl(itemDao, imageDao);
        }
        if (outgoingMailService==null) {
        	outgoingMailService = new OutgoingMailServiceImpl();
        }
        if (swagSwapUserService==null) {
        	//This will give us a UserService based on the TestEnvironment class
        	UserService googleUserService = UserServiceFactory.getUserService();
        	swagSwapUserService = new SwagSwapUserServiceImpl(userDao, itemService, 
        			googleUserService, outgoingMailService);
        }
	}

    public void testFindByEmail() {
        
        SwagSwapUser swagSwapUser = Fixture.createUser();
        userDao.insert(swagSwapUser);

        SwagSwapUser retrievedUser = swagSwapUserService.findByEmail(swagSwapUser.getEmail());
        assertNotNull(retrievedUser);
    }
    
    
    public void testFindByEmailOrCreate_non_existent_user() {
    	SwagSwapUser retrievedUser = swagSwapUserService.findByEmailOrCreate();
    	assertNotNull(retrievedUser);
    }
    
    public void testFindByEmailOrCreate_existing_user() {
    	
    	//Try it with existing user
    	//create user
    	SwagSwapUser swagSwapUser = Fixture.createUser();
    	userDao.insert(swagSwapUser);
    	
    	//verify
    	SwagSwapUser retrievedUser = swagSwapUserService.findByEmailOrCreate();
    	assertNotNull(retrievedUser);
    }

    
    public void testAddUserRating() {
        //create user
    	SwagSwapUser swagSwapUser = Fixture.createUser();
        userDao.insert(swagSwapUser);
        
        //create item 1
        SwagItem swagItem1 = Fixture.createSwagItem();
        itemDao.insert(swagItem1);
        
        //create item 2
        SwagItem swagItem2 = Fixture.createSwagItem();
        itemDao.insert(swagItem2);
        
        //create rating 1
        SwagItemRating swagItemRating1 = new SwagItemRating(swagItem1.getKey(), 1);
    	swagSwapUserService.addOrUpdateRating(swagSwapUser.getEmail(), swagItemRating1);
    	
    	//create rating 2
    	SwagItemRating swagItemRating2 = new SwagItemRating(swagItem2.getKey(), 2);
    	swagSwapUserService.addOrUpdateRating(swagSwapUser.getEmail(), swagItemRating2);
    	
    	//verify
    	SwagSwapUser user = swagSwapUserService.findCurrentUserByEmail();
    	assertEquals(user.getSwagItemRatings().size(),2);
    }
    
    public void testUpdateUserRating() {
    	//create user
    	SwagSwapUser swagSwapUser = Fixture.createUser();
    	userDao.insert(swagSwapUser);
    	
    	//create item
    	SwagItem swagItem = Fixture.createSwagItem();
    	itemDao.insert(swagItem);
    	
    	//create rating
    	SwagItemRating originalRating = new SwagItemRating(swagItem.getKey(), 1);
    	swagSwapUserService.addOrUpdateRating(swagSwapUser.getEmail(), originalRating);
    	
    	//update rating
    	SwagItemRating newRating = new SwagItemRating(swagItem.getKey(), 2);
    	swagSwapUserService.addOrUpdateRating(swagSwapUser.getEmail(), newRating);
    	
    	SwagSwapUser user = swagSwapUserService.findByGoogleID(swagSwapUser.getGoogleID());
    	assertEquals(user.getSwagItemRatings().size(),1); //should still only be one rating
    	//Gosh this is the only way to get the first Item of a Set in Java
    	assertEquals(newRating.getUserRating(), user.getSwagItemRatings().iterator().next().getUserRating());
    }
    
    public void testAddOrUpdateRating_invalid_rating() {
    	//create user
    	SwagSwapUser swagSwapUser = Fixture.createUser();
    	userDao.insert(swagSwapUser);
    	
    	//create item
    	SwagItem swagItem = Fixture.createSwagItem();
    	itemDao.insert(swagItem);
    	
    	//create INVALID rating
    	SwagItemRating originalRating = new SwagItemRating(swagItem.getKey(), null);
    	try {
    		swagSwapUserService.addOrUpdateRating(swagSwapUser.getEmail(), originalRating);
    		fail ("should have thrown InvalidSwagItemRatingException");
    	}
    	catch (InvalidSwagItemRatingException e) {
    		//good
    	}
    }
    
}
