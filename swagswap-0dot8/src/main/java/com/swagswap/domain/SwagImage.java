package com.swagswap.domain;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

/**
 * Used as a vessel to hold the actual image data so that appengine
 * can save it to a Blob (just saving the byte[] is not allowed due 
 * to size constraints 
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SwagImage implements Serializable {

	private static final long serialVersionUID = 1L;

	// Have to use type Key (in this case a String key, see next comment)
	// (not Long) or else SwagImage can't be a child of SwagItem.
	// The key here has to be able to include the parent's key
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    //This makes it so we can use a String key instead of a non-portable Google key
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;
	
    @Persistent
	private Blob image;
    
    public SwagImage() {
    }
    
	public SwagImage(byte[] image) {
		this.image = new Blob(image);
	}
  
	public String getEncodedKey() {
		return encodedKey;
	}

	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}

	public Blob getImage() {
		return image;
	}
	public void setImage(Blob image) {
		this.image = image;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((encodedKey == null) ? 0 : encodedKey.hashCode());
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		return result;
	}

	// Here we had to use the getImage() method to force image loading
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwagImage other = (SwagImage) obj;
		if (encodedKey == null) {
			if (other.encodedKey != null)
				return false;
		} else if (!encodedKey.equals(other.encodedKey))
			return false;
		if (getImage() == null) {
			if (other.getImage() != null)
				return false;
		} else if (!getImage().equals(other.getImage()))
			return false;
		return true;
	}


    
    

}
