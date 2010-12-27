package com.swagswap.dao;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.domain.SwagImage;
import com.swagswap.domain.SwagItem;


public class ImageDaoImplTest extends LocalDatastoreTestCase  {
	
	private ItemDao itemDao;
	private ImageDaoImpl swagImageDao;
	
	@Override
    public void setUp() throws Exception {
        super.setUp();
        if (itemDao == null) {
    		ItemDaoImpl swagItemDao = new ItemDaoImpl();
            swagItemDao.setPersistenceManagerFactory(PMF);
    		this.itemDao=swagItemDao;
        }
        if (swagImageDao == null) {
        	ImageDaoImpl swagImageDao = new ImageDaoImpl();
        	swagImageDao.setPersistenceManagerFactory(PMF);
        	this.swagImageDao=swagImageDao;
        }
	}

    public void testGet() {
    	
        SwagItem swagItem = Fixture.createSwagItem();
        itemDao.insert(swagItem);
        
        SwagImage image = swagImageDao.get(swagItem.getImageKey());
        assertNotNull(image);
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
