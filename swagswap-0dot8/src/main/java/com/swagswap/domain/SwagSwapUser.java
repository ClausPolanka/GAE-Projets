package com.swagswap.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SwagSwapUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;
	
	// store ID from google account so that it's available anywhere in the services
	@Persistent
	private String googleID; 
	
	//Never displayed. Used as identifier and to communicate with the user
	@Persistent
	private String email;
	
	//This is the google accounts nickName now.  Maybe make this changeable for swagswap
	//It is the name displayed as swagitem owner
	@Persistent
	private String nickName; 
	
	@Persistent
	private Set<SwagItemRating> swagItemRatings = new HashSet<SwagItemRating>();
	
	@Persistent
	private Date joined;
	
	@Persistent
	private boolean optOut; //if they've opted out of maliings
	
	public SwagSwapUser() {
	}
	
	public SwagSwapUser(String email, String googleID, String nickName) {
		this.email=email;
		this.googleID=googleID;
		this.nickName=nickName;
	}
	
	public SwagSwapUser(String email) {
		this.email=email;
	}

	public boolean isNew() {
		return joined==null;
	}
	
	/**
	 * 
	 * @param swagItemKey
	 * @return SwagItem by key or null if not found
	 */
	public SwagItemRating getSwagItemRating(Long swagItemKey) {
		for (SwagItemRating rating:swagItemRatings) {
			//yikes, this worked on the dev server but not on the real thing!
			//if (swagItemKey==rating.getSwagItemKey())) {
			if (swagItemKey.equals(rating.getSwagItemKey())) {
				return rating;
			}
		}
		return null;
	}
	
	public Long getKey() {
		return key;
	}
	public void setKey(Long key) {
		this.key = key;
	}
	
	public String getGoogleID() {
		return googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public Set<SwagItemRating> getSwagItemRatings() {
		return swagItemRatings;
	}

	public void setSwagItemRatings(Set<SwagItemRating> ratedSwagItems) {
		this.swagItemRatings = ratedSwagItems;
	}

	public Date getJoined() {
		return joined;
	}
	public void setJoined(Date joined) {
		this.joined = joined;
	}
	
	public boolean getOptOut() {
		return optOut;
	}

	public void setOptOut(boolean optOut) {
		this.optOut = optOut;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((googleID == null) ? 0 : googleID.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((joined == null) ? 0 : joined.hashCode());
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((nickName == null) ? 0 : nickName.hashCode());
		result = prime * result + ((swagItemRatings == null) ? 0 : swagItemRatings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwagSwapUser other = (SwagSwapUser) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (googleID == null) {
			if (other.googleID != null)
				return false;
		} else if (!googleID.equals(other.googleID))
			return false;
		if (joined == null) {
			if (other.joined != null)
				return false;
		} else if (!joined.equals(other.joined))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (nickName == null) {
			if (other.nickName != null)
				return false;
		} else if (!nickName.equals(other.nickName))
			return false;
		if (swagItemRatings == null) {
			if (other.swagItemRatings != null)
				return false;
		} else if (!swagItemRatings.equals(other.swagItemRatings))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SwagSwapUser [googleID=" + googleID + ", email=" + email + ", joined=" + joined + ", key="
				+ key + ", nickName=" + nickName + ", swagItemRatings="
				+ swagItemRatings + "]";
	}
	
}
