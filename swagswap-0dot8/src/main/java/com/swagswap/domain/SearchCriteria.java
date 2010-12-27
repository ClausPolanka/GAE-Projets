package com.swagswap.domain;

/**
 * Wrapper object needed to bind to the spring form
 * could eventually hold paging and fetch size info
 * @author sam
 *
 */
public class SearchCriteria {
	private String searchString;

	public SearchCriteria() {
	}

	public SearchCriteria(String searchString) {
		this.searchString=searchString;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
}
