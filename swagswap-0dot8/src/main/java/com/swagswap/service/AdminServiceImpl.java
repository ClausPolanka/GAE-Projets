package com.swagswap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.swagswap.domain.SwagItem;

/**
 * For Admin functionality
 * 
 * @author sam
 * 
 */
public class AdminServiceImpl implements AdminService {

	private static final String TEST_ITEM_TAG = "admin-sample";

	private static final Logger log = Logger.getLogger(AdminServiceImpl.class);

	@Autowired
	private ItemService itemService;
	
	public AdminServiceImpl() {
	}

	// for unit tests
	protected AdminServiceImpl(ItemService itemService) {
		this.itemService = itemService;
	}

	/* (non-Javadoc)
	 * @see com.swagswap.service.AdminService#populateTestSwagItems(int)
	 */
	public void populateTestSwagItems (int numberOfSwagItems) {
		List<byte[]> sampleSwagImages = new ArrayList<byte[]>();
		
		sampleSwagImages.add(itemService.getImageDataFromURL("http://t3.gstatic.com/images?q=tbn:bqBPVyJAXv1cSM:http://www.comparestoreprices.co.uk/images/ju/jumbo-cigar.jpg"));
		sampleSwagImages.add(itemService.getImageDataFromURL("http://t1.gstatic.com/images?q=tbn:r3JpRJAkL5h4iM:http://www.easypointer.com/catalog/images/green-laser.gif"));
		sampleSwagImages.add(itemService.getImageDataFromURL("http://t3.gstatic.com/images?q=tbn:yrcfyyW5VHOltM:http://www.practical-jokes-and-pranks.com/images/noise_smell/8in_whoopie_cushion_250.jpg"));
		
		Random generator = new Random();
		int batchId = generator.nextInt(1000); //in case we run this several times
		List<String> tags = new ArrayList<String>();
		tags.add(TEST_ITEM_TAG);
		tags.add("");
		tags.add("");
		tags.add("");
		for (int i = 0; i < numberOfSwagItems; i++) {
			SwagItem swagItem = new SwagItem();
			swagItem.setName("item " + batchId + "-" +i);
			swagItem.setCompany("testCompany");
			swagItem.setDescription("test description " + i);
			swagItem.setImageBytes(sampleSwagImages.get(generator.nextInt(sampleSwagImages.size())));
			swagItem.setTags(tags);
			itemService.save(swagItem);
		}
	}
	
	public int deleteTestSwagItems() {
		List<SwagItem> itemsToDelete = itemService.findByTag(TEST_ITEM_TAG);
		for (SwagItem swagItem : itemsToDelete) {
			itemService.delete(swagItem.getKey());
		}
		return itemsToDelete.size();	
	}



}