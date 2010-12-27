package com.swagswap.dao;

import java.util.List;

import javax.jdo.JDOObjectNotFoundException;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;

public interface ItemDao {

	/**
	 * Load swagItem, but not associated swagImage
	 */
	SwagItem get(Long id);

	/**
	 * 
	 * @param id
	 * @param loadSwagImage whether to load swagImage (it is lazy loaded by GAE)
	 * @return SwagItem if found
	 * @throws JDOObjectNotFoundException (RuntimeException) if item not found
	 */
	SwagItem get(Long id, boolean loadSwagImage);

	/**
	 * Search by tag and by name
	 * NOTE: only supports case sensitive queries
	 * This implementation searches for exact SwagItem.name or exact tag match
	 * @param searchString
	 */
	List<SwagItem> search(String searchString);

	List<SwagItem> getAll();

	List<SwagItem> findByTag(String searchString);
	/**
	 * Insert SwagItem and SwagImage (if applicable)
	 * Note: swagItem ref passed in is updated 
	 * @param swagItem
	 */
	void insert(SwagItem swagItem);

	/**
	 * @param updatedItem
	 * TODO take care of image here
	 */
	void update(SwagItem updatedItem);
	
	void updateRating(Long swagItemKey, int computedRatingDifference, boolean isNewRating);

	/**
	 * SwagImages (children) of SwagItems are automatically deleted
	 * So you have to do that yourself
	 * see http://code.google.com/appengine/docs/python/datastore/keysandentitygroups.html#Entity_Groups_Ancestors_and_Paths
	 */
	void delete(Long id);

	void addComment(SwagItemComment swagItemComment);
	

}