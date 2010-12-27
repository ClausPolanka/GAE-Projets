package com.swagswap.domain;

import java.io.Serializable;

public class SwagSwapUserStats implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private SwagSwapUser swagSwapUser;
	private Integer itemsCreated = 0;
	private Integer itemsRated = 0;
	private Integer itemsCommented = 0;
	
	public SwagSwapUserStats(SwagSwapUser swagSwapUser) {
		super();
		this.swagSwapUser = swagSwapUser;
	}

	public SwagSwapUser getSwagSwapUser() {
		return swagSwapUser;
	}
	public void setSwagSwapUser(SwagSwapUser swagSwapUser) {
		this.swagSwapUser = swagSwapUser;
	}
	public Integer getItemsCreated() {
		return itemsCreated;
	}
	public void setItemsCreated(Integer itemsCreated) {
		this.itemsCreated = itemsCreated;
	}
	public Integer getItemsRated() {
		return itemsRated;
	}
	public void setItemsRated(Integer itemsRated) {
		this.itemsRated = itemsRated;
	}
	public Integer getItemsCommented() {
		return itemsCommented;
	}
	public void setItemsCommented(Integer itemsCommented) {
		this.itemsCommented = itemsCommented;
	}

	
}
