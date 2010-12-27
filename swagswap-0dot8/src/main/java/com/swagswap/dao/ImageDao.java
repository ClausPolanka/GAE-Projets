package com.swagswap.dao;

import java.util.List;

import com.swagswap.domain.SwagImage;

public interface ImageDao {

	List<SwagImage> getAll();

	SwagImage get(String key);

	byte[] getResizedImageBytes(byte[] originalImageBytes);
	
	byte[] getResizedThumbnailImageBytes(byte[] originalImageBytes);

	byte[] getThumbnailBytes(String key);

}
