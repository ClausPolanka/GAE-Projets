package com.swagswap.web.jsf.bean;

import java.io.IOException;
import java.util.Date;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.service.SwagSwapUserService;

@ManagedBean(name = "userBean")
@RequestScoped
public class UserBean {

	// Inject the swagSwapUserService Spring Bean
	@ManagedProperty(value = "#{swagSwapUserService}")
	private SwagSwapUserService swagSwapUserService;

	public SwagSwapUserService getSwagSwapUserService() {
		return swagSwapUserService;
	}

	public void setSwagSwapUserService(SwagSwapUserService swagSwapUserService) {
		this.swagSwapUserService = swagSwapUserService;
	}

	public SwagSwapUser getLoggedInUser() {
		if (!swagSwapUserService.isUserLoggedIn()) {
			return null;
		}
		return swagSwapUserService.findCurrentUserByEmail();
	}

	private String getCurrentURL() {
		HttpServletRequest request = (HttpServletRequest) FacesContext
				.getCurrentInstance().getExternalContext().getRequest();
		// Hack to get round losing request parameters when redirecting to
		// Google login page
		String currentURL = request.getRequestURL().toString().replace(
				"viewSwag", "allSwag");
		return currentURL;
	}

	/**
	 * Hack to redirect to Google generated login page. <h:outputLink> was
	 * encoding part of the URL and Google wouldn't accept that
	 * 
	 * @throws IOException
	 */
	public void showLogin() throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.sendRedirect(swagSwapUserService
				.createLoginURL(getCurrentURL()));
	}

	/**
	 * Hack to redirect to Google generated logout page. <h:outputLink> was
	 * encoding part of the URL and Google wouldn't accept that
	 * 
	 * @throws IOException
	 */
	public void showLogout() throws IOException {
		HttpServletResponse response = (HttpServletResponse) FacesContext
				.getCurrentInstance().getExternalContext().getResponse();
		response.sendRedirect(swagSwapUserService
				.createLogoutURL("/jsf/home.jsf"));
	}

	public Integer getUserRatingForItem(Long key, SwagSwapUser user) {
		// TODO Maybe service should default user rating to zero if it
		// doesn't exist

		if (user == null || (!swagSwapUserService.isUserLoggedIn())) {
			return 0;
		}
		Integer userItemRating;
		if (user.getSwagItemRating(key) == null) {
			userItemRating = 0;
		} else {
			userItemRating = user.getSwagItemRating(key).getUserRating();
		}
		// Temporary hack
		if (userItemRating == null) {
			userItemRating = 0;
		}

		return userItemRating;
	}

	public Boolean isItemOwner(SwagItem item) {
		return swagSwapUserService.isUserAdmin()
				|| swagSwapUserService.isItemOwner(item);
	}

}
