package com.swagswap.exceptions;


public class ImageTooLargeException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String url;

	//Needed for GWT compilation
	public ImageTooLargeException() {
		super();
	}
	
	public ImageTooLargeException(String url, int maxSize) {
		super("Image at url " + url+ " is too large (max size is " + maxSize + ")");
	}

	public String getUrl() {
		return url;
	}
}
