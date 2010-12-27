package com.swagswap.domain;

import java.io.Serializable;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * A SwagItemUser rating of a SwagItem
 *
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SwagItemRating implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    //This makes it so we can use a String key instead of a non-portable Google key
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;
	
    @Persistent
	private Long swagItemKey;
	
	@Persistent
	private Integer userRating;
	
	public SwagItemRating() {
	}
	
	public SwagItemRating(Long swagItemKey) {
		this(swagItemKey,null);
	}

	public SwagItemRating(Long swagItemKey, Integer userRating) {

		this.swagItemKey = swagItemKey;
		setUserRating(userRating); //set method has logic in it
	}

	public String getEncodedKey() {
		return encodedKey;
	}
	public void setEncodedKey(String encodedKey) {
		this.encodedKey = encodedKey;
	}
	public Long getSwagItemKey() {
		return swagItemKey;
	}
	public void setSwagItemKey(Long swagItemKey) {
		this.swagItemKey = swagItemKey;
	}
	public Integer getUserRating() {
		return userRating;
	}
	public void setUserRating(Integer userRating) {
		if (userRating==null) {
			return;
		}
		this.userRating=userRating;
	}
	
	//just use swagItemKey
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((swagItemKey == null) ? 0 : swagItemKey.hashCode());
		return result;
	}
	
	//just use swagItemKey
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwagItemRating other = (SwagItemRating) obj;
		if (swagItemKey == null) {
			if (other.swagItemKey != null)
				return false;
		} else if (!swagItemKey.equals(other.swagItemKey))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SwagItemRating [encodedKey=" + encodedKey + ", swagItemKey="
				+ swagItemKey + ", userRating=" + userRating + "]";
	}
	
	
}
