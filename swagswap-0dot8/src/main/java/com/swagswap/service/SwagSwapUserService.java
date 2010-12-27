package com.swagswap.service;

import java.util.List;

import com.google.appengine.api.users.User;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.exceptions.InvalidSwagItemRatingException;
import com.swagswap.exceptions.UserAlreadyExistsException;

public interface SwagSwapUserService {

	List<SwagSwapUser> getAll();
	
	SwagSwapUser get(Long id);

	void insert(SwagSwapUser swagSwapUser) throws UserAlreadyExistsException ;
	
	void update(SwagSwapUser swagSwapUser);

	/**
	 * Gets email from currentUser
	 */
	SwagSwapUser findCurrentUserByEmail();
	
	SwagSwapUser findByEmail(String email);
	
	SwagSwapUser findByGoogleID(String googleID);
	
	SwagSwapUser findByEmailOrCreate();

	void addOrUpdateRating(String userEmail, SwagItemRating swagItemRating) throws InvalidSwagItemRatingException ;
	
	boolean isItemOwner(SwagItem item);
	
	void blackListUser(String email);
	
	void optOut(String googleID, boolean optOut);
	
	/**
	 * Wrapped Google UserService methods
	 */
	
	String createLoginURL(String destinationURL);
	
	String createLogoutURL(String destinationURL);
	
	User getCurrentUser();
	
	boolean isUserAdmin();
	
	boolean isUserLoggedIn();


}