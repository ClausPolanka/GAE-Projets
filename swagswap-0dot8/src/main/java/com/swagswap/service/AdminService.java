package com.swagswap.service;

public interface AdminService {

	/**
	 * Load swagItem, but not associated swagImage
	 */
	public abstract void populateTestSwagItems(int numberOfSwagItems);

	public abstract int deleteTestSwagItems();

}