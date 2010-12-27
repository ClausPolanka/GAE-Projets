package com.swagswap.exceptions;


public class InvalidSwagImageException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String mimeType;

	public InvalidSwagImageException(String mimeType) {
		super("Image with mime type " + mimeType + " not allowed");
		this.mimeType=mimeType;
	}
	
	public InvalidSwagImageException(Exception nested) {
		super(nested);
		this.mimeType="invalid";
	}

	public String getMimeType() {
		return mimeType;
	}
}
