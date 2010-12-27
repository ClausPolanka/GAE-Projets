package com.swagswap.service;

import java.util.List;

import com.swagswap.domain.SwagImage;

public interface ImageService {

	List<SwagImage> getAll();

	SwagImage get(String key);

	byte[] getResizedImageBytes(byte[] originalImageBytes);

	byte[] getResizedThumbnailImageBytes(byte[] originalImageBytes);
	
	byte[] getThumbnailBytes(String key);
	
	byte[] getDefaultImageBytes(String requestURL);


}
