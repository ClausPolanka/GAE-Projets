package com.swagswap.web.gwt.server;

import junit.framework.TestCase;

import com.swagswap.common.Fixture;
import com.swagswap.domain.SwagItem;
import com.swagswap.service.ItemServiceImpl;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

public class ItemServiceGWTWrapperImplTest extends TestCase {

	ItemServiceGWTWrapperImpl service;
	
	@Override
    public void setUp() throws Exception {
        super.setUp();
        if (service==null) {
        	service = new ItemServiceGWTWrapperImpl();
        }
	}
	
	public void testToDTO() {
		SwagItem swagItem = Fixture.createSwagItem();
		SwagItemGWTDTO swagItemGWTDTO = service.toDTO(swagItem);
		assertEquals(swagItem.getKey(),swagItemGWTDTO.getKey());
		assertEquals(swagItem.getName(),swagItemGWTDTO.getName());
		assertEquals(swagItem.getCompany(),swagItemGWTDTO.getCompany());
		assertEquals(swagItem.getDescription(),swagItemGWTDTO.getDescription());
		assertEquals(swagItem.getOwnerGoogleID(),swagItemGWTDTO.getOwnerGoogleID());
		assertEquals(swagItem.getOwnerNickName(),swagItemGWTDTO.getOwnerNickName());
		assertEquals(swagItem.getAverageRating(),swagItemGWTDTO.getAverageRating());
		assertEquals(swagItem.getNumberOfRatings(),swagItemGWTDTO.getNumberOfRatings());
		assertEquals(swagItem.getCreated(),swagItemGWTDTO.getCreated());
		assertEquals(swagItem.getLastUpdated(),swagItemGWTDTO.getLastUpdated());
		assertEquals(swagItem.getTags(),swagItemGWTDTO.getTags());
		assertEquals(swagItem.getComments(),swagItemGWTDTO.getComments());
	}
	
	public void testToSwagItem() {
		SwagItemGWTDTO swagItemGWTDTO = Fixture.createSwagItemGWTDTO();
		
		SwagItem swagItem = service.toSwagItem(swagItemGWTDTO);
		
		assertEquals(swagItemGWTDTO.getKey(),swagItem.getKey());
		assertEquals(swagItemGWTDTO.getName(),swagItem.getName());
		assertEquals(swagItemGWTDTO.getCompany(),swagItem.getCompany());
		assertEquals(swagItemGWTDTO.getDescription(),swagItem.getDescription());
		assertEquals(swagItemGWTDTO.getOwnerGoogleID(),swagItem.getOwnerGoogleID());
		assertEquals(swagItemGWTDTO.getOwnerNickName(),swagItem.getOwnerNickName());
		assertEquals(swagItemGWTDTO.getAverageRating(),swagItem.getAverageRating());
		assertEquals(swagItemGWTDTO.getNumberOfRatings(),swagItem.getNumberOfRatings());
		assertEquals(swagItemGWTDTO.getCreated(),swagItem.getCreated());
		assertEquals(swagItemGWTDTO.getLastUpdated(),swagItem.getLastUpdated());
		assertEquals(swagItemGWTDTO.getTags(),swagItem.getTags());
		assertEquals(swagItemGWTDTO.getComments(),swagItem.getComments());
	}
}
