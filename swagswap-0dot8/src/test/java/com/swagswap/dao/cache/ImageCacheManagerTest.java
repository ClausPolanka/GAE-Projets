package com.swagswap.dao.cache;

import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.dao.ImageDaoImpl;
import com.swagswap.dao.ItemDaoImpl;
import com.swagswap.domain.SwagImage;
import com.swagswap.domain.SwagItem;

public class ImageCacheManagerTest extends LocalDatastoreTestCase {
	
	private ImageCacheManager imageCacheManager;
	private ItemDaoImpl itemDao;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		if (imageCacheManager == null) {
			ImageDaoImpl imageDao = new ImageDaoImpl();
			imageDao.setPersistenceManagerFactory(PMF);
			itemDao = new ItemDaoImpl();
			itemDao.setPersistenceManagerFactory(PMF);
			imageCacheManager = new ImageCacheManager(imageDao, new SwagCacheManagerImpl());
		}
	}
	
    public void testGet() {
    	// TODO Test fails as Image is invalid and thumbnail cannot be retrieved
        SwagItem swagItem = Fixture.createSwagItem();
        itemDao.insert(swagItem);
        
        SwagImage image = imageCacheManager.get(swagItem.getImageKey());
        assertNotNull(image);
    }
	
	


}
