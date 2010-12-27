package com.swagswap.dao.cache;

import java.io.Serializable;

import com.swagswap.domain.SwagImage;

public class ImageAndThumbnail implements Serializable {

	private static final long serialVersionUID = 1L;
	private SwagImage Image;
	private byte[] Thumbnail;

	public ImageAndThumbnail(SwagImage image, byte[] thumbnail) {
		super();
		Image = image;
		Thumbnail = thumbnail;
	}

	public SwagImage getImage() {
		return Image;
	}

	public void setImage(SwagImage image) {
		Image = image;
	}

	public byte[] getThumbnail() {
		return Thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		Thumbnail = thumbnail;
	}
}

