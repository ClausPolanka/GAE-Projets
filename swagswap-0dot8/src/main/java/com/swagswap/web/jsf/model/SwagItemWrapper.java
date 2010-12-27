package com.swagswap.web.jsf.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.service.SwagSwapUserService;
import com.swagswap.web.jsf.bean.UserBean;

/**
 * @author scott
 * 
 *         Wrapper class for SwagItem to make list processing simpler in JSF
 */
public class SwagItemWrapper implements Serializable {

	private static final long serialVersionUID = 1L;

	public SwagItemWrapper(SwagItem swagItem, Integer userRating,
			Boolean itemOwner) {
		super();
		this.swagItem = swagItem;
		this.userRating = userRating;
		this.itemOwner = itemOwner;
	}

	private SwagItem swagItem;
	private Integer userRating;
	// Problems serializing primitives with GAE.
	private Boolean itemOwner;

	public Boolean getItemOwner() {
		return itemOwner;
	}

	public void setItemOwner(Boolean itemOwner) {
		this.itemOwner = itemOwner;
	}

	public SwagItem getSwagItem() {
		return swagItem;
	}

	public void setSwagItem(SwagItem swagItem) {
		this.swagItem = swagItem;
	}

	public Integer getUserRating() {
		return userRating;
	}

	public void setUserRating(Integer userRating) {
		this.userRating = userRating;
	}

	public static List<SwagItemWrapper> convertSwagListToWrapperList(
			List<SwagItem> itemList, UserBean userBean) {
		// Get user to determine user rating
		SwagSwapUser user = null;
		SwagSwapUserService userService = userBean.getSwagSwapUserService();
		if (userService.isUserLoggedIn()) {
			user = userService.findCurrentUserByEmail();
		}
		List<SwagItemWrapper> wrapperList = new ArrayList<SwagItemWrapper>(
				itemList.size());

		for (SwagItem swagItem : itemList) {
			// Set image and image bytes to null. These are not required in Swag
			// Table and affects
			// performance. ImageBytes may be set due to previously adding a
			// swagItem. If we don't set this to null the request will blow the
			// form size limit
			swagItem.setImage(null);
			swagItem.setImageBytes(null);
			wrapperList.add(new SwagItemWrapper(swagItem, userBean
					.getUserRatingForItem(swagItem.getKey(), user), userBean
					.isItemOwner(swagItem)));
		}
		return wrapperList;
	}

}
