package com.swagswap.service;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.swagswap.common.Fixture;
import com.swagswap.common.LocalDatastoreTestCase;
import com.swagswap.dao.ImageDaoImpl;
import com.swagswap.dao.ItemDaoImpl;
import com.swagswap.dao.UserDaoImpl;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagStats;

public class SwagStatsServiceImplTest extends LocalDatastoreTestCase {

	private ItemServiceImpl itemService;
	private ItemDaoImpl itemDao;
	private OutgoingMailService outgoingMailService;
	private SwagStatsServiceImpl swagStatsService;

	@Override
	public void setUp() throws Exception {
		super.setUp();
		ImageDaoImpl imageDao = new ImageDaoImpl();
		imageDao.setPersistenceManagerFactory(PMF);

		UserDaoImpl userDao = new UserDaoImpl();
		userDao.setPersistenceManagerFactory(PMF);

		UserService googleUserService = UserServiceFactory.getUserService();

		if (itemDao == null) {
			itemDao = new ItemDaoImpl();
			itemDao.setPersistenceManagerFactory(PMF);

		}
		if (itemService == null) {
			itemService = new ItemServiceImpl(itemDao, imageDao);

		}
        if (outgoingMailService==null) {
        	outgoingMailService = new OutgoingMailServiceImpl();
        }
		SwagSwapUserServiceImpl swagSwapUserService = new SwagSwapUserServiceImpl(
				userDao, itemService, googleUserService,outgoingMailService);
		if (swagStatsService == null) {
			swagStatsService = new SwagStatsServiceImpl(itemService,
					swagSwapUserService);
		}

	}

	public void testGetSwagStats() {

		SwagItem item1 = Fixture.createSwagItem();
		itemDao.insert(item1);
		SwagItem item2 = Fixture.createSwagItem();
		itemDao.insert(item2);
		SwagItem item3 = Fixture.createSwagItem();
		itemDao.insert(item3);
		SwagItem item4 = Fixture.createSwagItem();
		itemDao.insert(item4);
		SwagItem item5 = Fixture.createSwagItem();
		itemDao.insert(item5);
		SwagItem item6 = Fixture.createSwagItem();
		itemDao.insert(item6);

		SwagStats swagStats = swagStatsService.getSwagStats();

		assertEquals(0, swagStats.getTopRated().size());
		assertEquals(5, swagStats.getMostCommented().size());
		assertEquals(5, swagStats.getMostRated().size());

	}

}
