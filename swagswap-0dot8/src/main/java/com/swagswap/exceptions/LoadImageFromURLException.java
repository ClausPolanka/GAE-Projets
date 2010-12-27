package com.swagswap.exceptions;


public class LoadImageFromURLException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private String url;

	//Needed for GWT compilation
	public LoadImageFromURLException() {
		super();
	}
	
	public LoadImageFromURLException(String url, Throwable cause) {
		super("Cannot load image from " + url, cause);
		this.url=url;
	}

	public String getUrl() {
		return url;
	}
}
