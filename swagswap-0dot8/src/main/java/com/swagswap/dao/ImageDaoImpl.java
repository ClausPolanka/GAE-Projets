package com.swagswap.dao;

import java.util.List;

import javax.jdo.PersistenceManager;

import org.apache.log4j.Logger;
import org.springframework.orm.jdo.support.JdoDaoSupport;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.swagswap.domain.SwagImage;

/**
 * Operates on SwagImages. Inspired by Spring Image Database sample application
 */
public class ImageDaoImpl extends JdoDaoSupport implements ImageDao {

	private static final Logger log = Logger.getLogger(ImageDaoImpl.class);

	private static final int THUMBNAIL_WIDTH = 66;
	private static final int THUMBNAIL_HEIGHT = 50;

	private static final int IMAGE_WIDTH = 300;
	private static final int IMAGE_HEIGHT = 225;

	@SuppressWarnings("unchecked")
	public List<SwagImage> getAll() {
		PersistenceManager pm = getPersistenceManager();
		String query = "select from " + SwagImage.class.getName();
		List<SwagImage> swagImages = (List<SwagImage>) pm.newQuery(query)
				.execute();

		if (log.isInfoEnabled()) {
			log.info("returning " + swagImages.size() + " swag images");
		}
		return swagImages;

	}

	public SwagImage get(String key) {
		SwagImage swagImage = getPersistenceManager().getObjectById(
				SwagImage.class, key);
		return swagImage;
	}

	public byte[] getResizedImageBytes(byte[] originalImageBytes) {
		return resizeImage(originalImageBytes, IMAGE_WIDTH,
				IMAGE_HEIGHT);
	}
	
	public byte[] getResizedThumbnailImageBytes(byte[] originalImageBytes) {
		return resizeImage(originalImageBytes, THUMBNAIL_WIDTH,
				THUMBNAIL_HEIGHT);
	}

	public byte[] getThumbnailBytes(String key) {
		SwagImage image = get(key);
		if (image == null || image.getImage() == null) {
			return null;
		}
		return getResizedThumbnailImageBytes(image.getImage().getBytes());
	}

	private byte[] resizeImage(byte[] imageBytes, int resizedWidth,
			int resizedHeight) {

		ImagesService imagesService = ImagesServiceFactory.getImagesService();
		Image oldImage = ImagesServiceFactory.makeImage(imageBytes);
		Transform resize = ImagesServiceFactory.makeResize(resizedWidth,
				resizedHeight);
		Image newImage = imagesService.applyTransform(resize, oldImage,
				ImagesService.OutputEncoding.valueOf("JPEG"));
		return newImage.getImageData();
	}

}
