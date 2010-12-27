package com.swagswap.web.gwt.client.domain;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Unfortunate violation of the DRY principle :(
 * DTO to get around problem with GWT RPC-ing appengine Blob and Text appengine types
 *
 */
public class SwagItemGWTDTO implements IsSerializable {
	private Long key;
	private String name;
	private String company;
	// is type Text in SwagItem
	private String description;
	//just store byte[] since contained Blob can't be GWT RPC-ed
	//private SwagImage image = new SwagImage();

	//not needed here
	//private String imageKey;
	private String imageKey; //existing image key
	private String newImageURL;
	private byte[] newImageBytes;
	private String ownerGoogleID;
	private String ownerNickName;
	private Float averageRating = 0F;
	private Integer numberOfRatings = 0;
	private Date created;
	private Date lastUpdated;
	private ArrayList<String> tags = new ArrayList<String>();
	private ArrayList<SwagItemCommentGWTDTO> comments = new ArrayList<SwagItemCommentGWTDTO>();
	
	private boolean isFetchOnly;


	public SwagItemGWTDTO() {
		this(false);
	}
	
	//For SwagSwapGWT fetch(item) adaptation
	public SwagItemGWTDTO(boolean isFetchOnly) {
		this.isFetchOnly=isFetchOnly;
		//Add empty strings for the backing form
		tags.add("");
		tags.add("");
		tags.add("");
		tags.add("");
	}
	
	public SwagItemGWTDTO(Long key, String name, String company,
			String description, String imageKey,
			String ownerGoogleID, String ownerNickName, Float averageRating,
			Integer numberOfRatings, Date created, Date lastUpdated,
			ArrayList<String> tags, ArrayList<SwagItemCommentGWTDTO> comments) {
		this();
		this.key = key;
		this.name = name;
		this.company = company;
		this.description = description;
		this.imageKey = imageKey;
		this.ownerGoogleID = ownerGoogleID;
		this.ownerNickName = ownerNickName;
		this.averageRating = averageRating;
		this.numberOfRatings = numberOfRatings;
		this.created = created;
		this.lastUpdated = lastUpdated;
		this.tags = tags;
		this.comments = comments;
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
	}

	public byte[] getNewImageBytes() {
		return newImageBytes;
	}

	public void setNewImageBytes(byte[] newImageBytes) {
		this.newImageBytes = newImageBytes;
	}

	public void setNewImageURL(String newImageURL) {
		this.newImageURL = newImageURL;
	}

	public String getNewImageURL() {
		return newImageURL;
	}

	public void setNewImagURL(String newImageURL) {
		this.newImageURL = newImageURL;
	}

	public String getOwnerGoogleID() {
		return ownerGoogleID;
	}

	public void setOwnerGoogleID(String ownerGoogleID) {
		this.ownerGoogleID = ownerGoogleID;
	}

	public String getOwnerNickName() {
		return ownerNickName;
	}

	public void setOwnerNickName(String ownerNickName) {
		this.ownerNickName = ownerNickName;
	}

	public Float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(Float averageRating) {
		this.averageRating = averageRating;
	}

	public Integer getNumberOfRatings() {
		return numberOfRatings;
	}

	public void setNumberOfRatings(Integer numberOfRatings) {
		this.numberOfRatings = numberOfRatings;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public ArrayList<String> getTags() {
		return tags;
	}

	public void setTags(ArrayList<String> tags) {
		this.tags = tags;
	}

	public ArrayList<SwagItemCommentGWTDTO> getComments() {
		return comments;
	}

	public void setComments(ArrayList<SwagItemCommentGWTDTO> comments) {
		this.comments = comments;
	}

	public boolean isFetchOnly() {
		return this.isFetchOnly;
	}
	
	public void setIsFetchOnly(boolean isFetchOnly) {
		this.isFetchOnly = isFetchOnly;
	}
	
}
