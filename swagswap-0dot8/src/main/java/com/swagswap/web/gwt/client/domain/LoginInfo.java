package com.swagswap.web.gwt.client.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.swagswap.domain.SwagItemRating;

// inspired by http://code.google.com/webtoolkit/tutorials/1.6/appengine.html

/**
 * Wraps SwagSwapUser since that contains JDO backed Sets that can't be serialzed by GWT
 */
public class LoginInfo implements Serializable {

	private boolean loggedIn = false;
	private boolean isUserAdmin = false;
	private String loginUrl;
	private String logoutUrl;
	private String email;
	private String googleID;
	private String nickName;
	private Set<SwagItemRating> swagItemRatings = new HashSet<SwagItemRating>();
	private SwagItemGWTDTO currentSwagItem; //for remembering which one they were editing

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public boolean isUserAdmin() {
		return isUserAdmin;
	}
	
	public void setIsUserAdmin(boolean isUserAdmin) {
		this.isUserAdmin = isUserAdmin;
	}
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID=googleID;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Set<SwagItemRating> getSwagItemRatings() {
		return swagItemRatings;
	}

	public void setSwagItemRatings(Set<SwagItemRating> swagItemRatings) {
		this.swagItemRatings = swagItemRatings;
	}
	
	/**
	 * 
	 * @param swagItemKey
	 * @return SwagItem by key or null if not found
	 */
	public SwagItemRating getSwagItemRating(Long swagItemKey) {
		for (SwagItemRating rating:swagItemRatings) {
			if (swagItemKey.equals(rating.getSwagItemKey())) {
				return rating;
			}
		}
		return null;
	}

	public SwagItemGWTDTO getCurrentSwagItem() {
		return currentSwagItem;
	}

	public void setCurrentSwagItem(SwagItemGWTDTO currentSwagItem) {
		this.currentSwagItem=currentSwagItem;
		
	}



}
