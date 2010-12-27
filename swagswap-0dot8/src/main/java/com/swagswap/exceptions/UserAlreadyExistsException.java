package com.swagswap.exceptions;

import com.swagswap.domain.SwagSwapUser;

public class UserAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private String email;
	
	public UserAlreadyExistsException(String msg) {
		super(msg);
	}

	public UserAlreadyExistsException(SwagSwapUser swagSwapUser) {
		this("User already exists with email: " + swagSwapUser.getEmail());
	}

}
