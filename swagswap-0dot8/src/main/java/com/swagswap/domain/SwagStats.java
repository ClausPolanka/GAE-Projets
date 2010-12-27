package com.swagswap.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SwagStats implements Serializable {

	private static final long serialVersionUID = 1L;

	List<SwagItem> topRated;
	List<SwagItem> mostRated;
	List<SwagItem> mostCommented;
	List<SwagSwapUserStats> userCreated;
	List<SwagSwapUserStats> userRated;
	List<SwagSwapUserStats> userCommented;
	Integer totalItems = 0;
	Integer totalUsers = 0;
	Integer totalComments = 0;
	Integer totalRatings = 0;
	
	public List<SwagItem> getAllTopRatedSwagItems() {
		List<SwagItem> allTopRatedItems = new ArrayList<SwagItem>();
		allTopRatedItems.addAll(topRated);
		allTopRatedItems.addAll(mostRated);
		allTopRatedItems.addAll(mostCommented);
		return allTopRatedItems;
	}
	
	/**
	 * 
	 * prepend the out put of this with something like:
	 * http://chart.apis.google.com/chart?chs=400x100&
	 * 
	 * @param chartType google chart type
	 * @return Google Chart URL (data section)
	 */
	public String getGoogleChartsUrlItemsCreated() {
		StringBuilder nicknames = new StringBuilder();
		StringBuilder itemsCreated = new StringBuilder();
		for (SwagSwapUserStats swagSwapUserStats : userCreated) {
			String userNickName = swagSwapUserStats.getSwagSwapUser().getNickName();
			nicknames.append(userNickName + "|");
			itemsCreated.append(swagSwapUserStats.getItemsCreated()+ ",");
		}
		String chartUrl = "http://chart.apis.google.com/chart?" + 
				"chs=400x100" + 
				"&chd=t:" + chopLastSeperator(itemsCreated.toString()) + 
				"&cht=p3" +
				"&chl=" + chopLastSeperator(nicknames.toString());
		return chartUrl;
	}
	/**
	 * 
	 * prepend the out put of this with something like:
	 * http://chart.apis.google.com/chart?chs=400x100&
	 * 
	 * @param chartType google chart type
	 * @return Google Chart (pie) URL (data section)
	 */
	public String getGoogleChartsUrlItemsRated() {
		StringBuilder nicknames = new StringBuilder();
		StringBuilder itemsRated = new StringBuilder();
		for (SwagSwapUserStats swagSwapUserStats : userRated) {
			String userNickName = swagSwapUserStats.getSwagSwapUser().getNickName();
			nicknames.append(userNickName + "|");
			itemsRated.append(swagSwapUserStats.getItemsRated()+ ",");
		}
		String chartUrl = "http://chart.apis.google.com/chart?" + 
				"chs=400x100" + 
				"&chd=t:" + chopLastSeperator(itemsRated.toString()) + 
				"&cht=p3" +
				"&chl=" + chopLastSeperator(nicknames.toString());
		return chartUrl;
	}
	/**
	 * 
	 * prepend the out put of this with something like:
	 * http://chart.apis.google.com/chart?chs=400x100&
	 * 
	 * @param chartType google chart type
	 * @return Google Chart URL (data section)
	 */
	public String getGoogleChartsUrlItemsCommented() {
		StringBuilder nicknames = new StringBuilder();
		StringBuilder itemsCommented = new StringBuilder();
		for (SwagSwapUserStats swagSwapUserStats : userCommented) {
			String userNickName = swagSwapUserStats.getSwagSwapUser().getNickName();
			nicknames.append(userNickName + "|");
			itemsCommented.append(swagSwapUserStats.getItemsCommented()+ ",");
		}
		String chartUrl = "http://chart.apis.google.com/chart?" + 
				"chs=500x100" + 
				"&chd=t:" + chopLastSeperator(itemsCommented.toString()) + 
				"&cht=p3" +
				"&chl=" + chopLastSeperator(nicknames.toString());
		return chartUrl;
	}
	
	private String chopLastSeperator(String string) {
		if (string==null) {
			return "";
		}
		return string.substring(0,string.length()-1);
	}
	
	public Integer getTotalComments() {
		return totalComments;
	}

	public void setTotalComments(Integer totalComments) {
		this.totalComments = totalComments;
	}

	public Integer getTotalRatings() {
		return totalRatings;
	}

	public void setTotalRatings(Integer totalRatings) {
		this.totalRatings = totalRatings;
	}
	
	public List<SwagSwapUserStats> getUserCommented() {
		return userCommented;
	}

	public void setUserCommented(List<SwagSwapUserStats> userCommented) {
		this.userCommented = userCommented;
	}
	public List<SwagSwapUserStats> getUserRated() {
		return userRated;
	}

	public void setUserRated(List<SwagSwapUserStats> userRated) {
		this.userRated = userRated;
	}


	public Integer getTotalUsers() {
		return totalUsers;
	}

	public void setTotalUsers(Integer totalUsers) {
		this.totalUsers = totalUsers;
	}

	public Integer getTotalItems() {
		return totalItems;
	}

	public void setTotalItems(Integer totalItems) {
		this.totalItems = totalItems;
	}

	public List<SwagItem> getTopRated() {
		return topRated;
	}

	public void setTopRated(List<SwagItem> topRated) {
		this.topRated = topRated;
	}

	public List<SwagItem> getMostRated() {
		return mostRated;
	}

	public void setMostRated(List<SwagItem> mostRated) {
		this.mostRated = mostRated;
	}

	public List<SwagItem> getMostCommented() {
		return mostCommented;
	}

	public void setMostCommented(List<SwagItem> mostCommented) {
		this.mostCommented = mostCommented;
	}

	public List<SwagSwapUserStats> getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(List<SwagSwapUserStats> userCreated) {
		this.userCreated = userCreated;
	}

}
