package com.swagswap.dao;

import java.util.List;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.domain.BlackListedUser;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagSwapUser;

public class UserDaoImplTest extends LocalDatastoreTestCase  {
	
	private UserDaoImpl userDao;
	
	@Override
    public void setUp() throws Exception {
        super.setUp();
        if (userDao == null) {
    		UserDaoImpl userDao = new UserDaoImpl();
            userDao.setPersistenceManagerFactory(PMF);
    		this.userDao=userDao;
        }
	}

    public void testInsert() {
        
        SwagSwapUser swagSwapUser = Fixture.createUser();
        userDao.insert(swagSwapUser);

        assertNumberOfUsers(1);   
        assertNotNull(swagSwapUser.getJoined()); //joined should be set
    }
    
    //TODO fix me
    public void testUpdate() {
    	
    	SwagSwapUser orig = Fixture.createUser();
    	userDao.insert(orig);
    	
    	orig.setNickName("testie");
    	orig.getSwagItemRatings().add(new SwagItemRating(1L,2));
    	userDao.update(orig);
    	
    	SwagSwapUser retrievedUser = userDao.get(orig.getKey());
    	assertEquals(orig, retrievedUser);
    }

    public void testFindByEmail() {
    	
        SwagSwapUser orig = Fixture.createUser();
        userDao.insert(orig);
        
        SwagSwapUser retrievedItem = userDao.findByEmail(orig.getEmail());
        assertEquals(orig,retrievedItem);
    }
    
    //Make sure it's null (and doesn't thrown an exception)
    public void testFindByEmail_nonexistant_email() {
    	SwagSwapUser retrievedItem = userDao.findByEmail("bogus");
    	assertNull(retrievedItem);
    		
    }
    
    //Make sure it's null (and doesn't thrown an exception)
    public void testBlackListUser_and_IsBlackListed() {
    	String email = "bogus@gmail.com";
    	userDao.blackListUser(email);
    	Query query = new Query(BlackListedUser.class.getSimpleName());
    	assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
    	assertTrue(userDao.isBlackListed(email));
    	assertFalse(userDao.isBlackListed("not@blacklisted.com"));
    }
    
    public void testOptOut() {
    	SwagSwapUser orig = Fixture.createUser();
    	userDao.insert(orig);
    	SwagSwapUser user = userDao.findByGoogleID(orig.getGoogleID());
//    	userDao.optOut(orig.getGoogleID(), true);
        user.setOptOut(true);
        
    	SwagSwapUser retrievedUser = userDao.get(orig.getKey());
//    	assertEquals(retrievedUser.getOptOut(), true);
    }
    
    /**
     * Database count assertions
     * @param usersExpected
     */
	private void assertNumberOfUsers(int usersExpected) {
		Query query = new Query(SwagSwapUser.class.getSimpleName());
    	assertEquals(usersExpected, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
	}
}
