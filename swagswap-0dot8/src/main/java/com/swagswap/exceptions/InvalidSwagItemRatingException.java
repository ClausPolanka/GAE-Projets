package com.swagswap.exceptions;

import com.swagswap.domain.SwagItemRating;

public class InvalidSwagItemRatingException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public InvalidSwagItemRatingException() {
		super();
	}
	
	public InvalidSwagItemRatingException(String msg) {
		super(msg);
	}

	public InvalidSwagItemRatingException(SwagItemRating swagItemRating) {
		this("SwagItemRating " + swagItemRating + " cannot be saved with null attribute value");
	}

}
