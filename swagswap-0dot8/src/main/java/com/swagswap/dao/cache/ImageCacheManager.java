package com.swagswap.dao.cache;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.swagswap.dao.ImageDao;
import com.swagswap.domain.SwagImage;

@SuppressWarnings("unchecked")
public class ImageCacheManager implements ImageDao {

	private static final Logger log = Logger.getLogger(ImageCacheManager.class);

	@Autowired
	private SwagCacheManager swagCacheManager;

	private ImageDao imageDao;

	/**
	 * Maintain list of cache keys for more efficient getAll()
	 */

	public ImageCacheManager() {
	}

	public void setImageDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	// for unit tests
	protected ImageCacheManager(ImageDao imageDao, SwagCacheManager cacheManager) {
		this();
		this.imageDao = imageDao;
		this.swagCacheManager = cacheManager;
	}

	public void setItemDao(ImageDao imageDao) {
		this.imageDao = imageDao;
	}

	public SwagImage get(String key) {

		if (swagCacheManager.getCache().containsKey(key)) {
			// returned cached swagItem
			return ((ImageAndThumbnail) swagCacheManager.getCache().get(key))
					.getImage();
		}
		// log.warn("Expected Swag Image not found in Cache.  Key " + key
		// + ".  Refresh cache");
		// Not in cache so get from DAO and add to cache
		// Getting all upfront causes timeout in GAE
		swagCacheManager.getCache().put(
				key,
				new ImageAndThumbnail(imageDao.get(key), imageDao
						.getThumbnailBytes(key)));

		return ((ImageAndThumbnail) swagCacheManager.getCache().get(key))
				.getImage();
	}

	public List<SwagImage> getAll() {
		// Just use Dao for this. Not going to be used so much
		return imageDao.getAll();
	}

	public byte[] getResizedImageBytes(byte[] originalImageBytes) {
		return imageDao.getResizedImageBytes(originalImageBytes);
	}

	public byte[] getResizedThumbnailImageBytes(byte[] originalImageBytes) {
		return imageDao.getResizedThumbnailImageBytes(originalImageBytes);
	}

	public byte[] getThumbnailBytes(String key) {

		if (swagCacheManager.getCache().containsKey(key)) {
			// returned cached thumbnail
			return ((ImageAndThumbnail) swagCacheManager.getCache().get(key))
					.getThumbnail();
		}
		// log.warn("Expected Swag Image not found in Cache.  Key " + key
		// + ".  Refresh cache");
		// Not in cache so get from DAO and add to cache
		// Getting all upfront causes timeout in GAE
		swagCacheManager.getCache().put(
				key,
				new ImageAndThumbnail(imageDao.get(key), imageDao
						.getThumbnailBytes(key)));

		return ((ImageAndThumbnail) swagCacheManager.getCache().get(key))
				.getThumbnail();
	}

	public void deleteImageFromCache(String imageKey) {
		swagCacheManager.getCache().remove(imageKey);
	}

}
