package com.swagswap.exceptions;


public class InvalidSwagItemException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public InvalidSwagItemException(String msg) {
		super(msg);
	}

}
