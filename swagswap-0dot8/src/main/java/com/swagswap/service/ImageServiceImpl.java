package com.swagswap.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.log4j.Logger;

import com.swagswap.dao.ImageDao;
import com.swagswap.domain.SwagImage;

/**
 * Image Service
 * 
 * @author scott
 * 
 */
public class ImageServiceImpl implements ImageService {

	private static final Logger log = Logger.getLogger(ImageServiceImpl.class);
	public static final String PATH_TO_DEFAULT_IMAGE = "images/no_photo.jpg";

	private static byte[] defaultImage;
	private static byte[] defaultThumbnailImage;

	private ImageDao imageDao;

	public ImageServiceImpl() {
	}

	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	public List<SwagImage> getAll() {
		return imageDao.getAll();
	}

	public SwagImage get(String key) {
		return imageDao.get(key);
	}

	public byte[] getResizedImageBytes(byte[] originalImageBytes) {
		return imageDao.getResizedImageBytes(originalImageBytes);
	}
	
	public byte[] getResizedThumbnailImageBytes(byte[] originalImageBytes) {
		if (defaultThumbnailImage == null || defaultThumbnailImage.length != 0) {
			defaultThumbnailImage = imageDao.getResizedThumbnailImageBytes(originalImageBytes);
		}
		return defaultThumbnailImage;
	}

	public byte[] getThumbnailBytes(String key) {
		return imageDao.getThumbnailBytes(key);
	}

	/**
	 * This is the only way I can get the base URL to this app
	 * 
	 * @param req
	 * @return
	 */
	private String constructDefaultImageURL(String requestURL) {
		String baseURL = requestURL.substring(0, requestURL
				.lastIndexOf("/springmvc"));
		return baseURL + "/" + PATH_TO_DEFAULT_IMAGE;
	}

	/**
	 * Lazy load default image for use if SwagItem isn't created with an image
	 * ImageIO had a nice way to do it but it is blacklisted on appengine :(
	 * 
	 * @param requestURL
	 *            to construct the full image URL
	 */
	public byte[] getDefaultImageBytes(String requestURL) {
		if (defaultThumbnailImage == null || defaultThumbnailImage.length != 0) {
			String defaultImageURLString = constructDefaultImageURL(requestURL);
			ByteArrayOutputStream bas = null;
	
			// create defaultImage byte[] from URL
			// ouch this would have been easier with ImageIO!
			try {
				URL defaultImageURL = new URL(defaultImageURLString);
				BufferedInputStream bis = new BufferedInputStream(defaultImageURL
						.openStream());
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int i;
				while ((i = bis.read()) != -1) {
					baos.write(i);
				}
				defaultImage = baos.toByteArray();
				return defaultImage;
			} catch (IOException e) {
				log.error("couldn't load defaultImage at " + defaultImageURLString,
						e);
				return null;
			} finally {
				try {
					if (bas != null)
						bas.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return defaultImage;
	}

}