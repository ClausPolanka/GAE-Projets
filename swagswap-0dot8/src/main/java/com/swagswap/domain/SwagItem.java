package com.swagswap.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

/**
 * Represents one piece of swag
 */
@SuppressWarnings("unchecked")
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SwagItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long key;

	@Persistent
	private String name;

	@Persistent
	private String company; // vendor giving out swag

	// Text and Blob are not part of the default fetch group
	@Persistent
	private Text description;

	/**
	 * Owned One-to-One Relationship (lazy loaded) see
	 * http://code.google.com/appengine/docs/java/datastore/relationships.html
	 * 
	 * Add empty image object, otherwise JDO won't allow you to add one as a
	 * child later plus we never want it to be null because the the only way to
	 * update the image data is to get the exiting SwagImage and set the image
	 * Blob. See ItemService populateSwagImage()
	 */
	@Persistent
	private SwagImage image = new SwagImage();

	/**
	 * Store this so we can get the images separately with an image servlet to
	 * show them with an image tag but not have to load them twice (once when
	 * retrieving it from SwagItem, and once when looking it up with the
	 * servlet)
	 */
	@Persistent
	private String imageKey;

	// used to store imageBytes from the HTML form
	@NotPersistent
	private byte[] imageBytes;

	// used to store imageURL from the HTML form
	@NotPersistent
	private String imageURL;

	// GoogleID: String because that's how it is in google user object
	@Persistent
	private String ownerGoogleID; 

	@Persistent
	private String ownerNickName;

	@Persistent
	private Float averageRating = 0F;

	@Persistent
	private Integer numberOfRatings = 0;

	@Persistent
	private Date created;

	@Persistent
	private Date lastUpdated;

	@Persistent(defaultFetchGroup = "true")
	private List<String> tags = new ArrayList<String>();

	@Persistent
	private List<SwagItemComment> comments = new ArrayList<SwagItemComment>();


	public SwagItem() {
		//Add empty strings for the backing form
		tags.add("");
		tags.add("");
		tags.add("");
		tags.add("");
	}
	
	public Integer getNumberOfComments() {
		// TODO.  Temporary hack.  Persist this at some point.  Scott.
		return comments.size();
	}
	
	public boolean isNew() {
		return getKey() == null;
	}

	/**
	 * @return whether the swagItem has an image to update
	 */
	public boolean hasNewImage() {
		return (hasNewImageBytes() || hasNewImageURL());
	}

	// SwagItem has a new image if imageBytes is filled from file upload
	public boolean hasNewImageBytes() {
		return (imageBytes != null && imageBytes.length != 0);
	}

	public boolean hasNewImageURL() {
		return (imageURL !=null && !"".equals(imageURL));
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
		return (description == null) ? "" : description.getValue();
	}

	//creating a new Text(null) causes a datanucleus Exception
	public void setDescription(String description) {
		this.description = new Text((description == null) ? "" : description);
	}

	public SwagImage getImage() {
		return image;
	}

	public void setImage(SwagImage image) {
		this.image = image;
	}

	public byte[] getImageBytes() {
		return imageBytes;
	}

	public void setImageBytes(byte[] imageBytes) {
		this.imageBytes = imageBytes;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	// Lazily populate this
	public String getImageKey() {
		return imageKey;
	}

	public void setImageKey(String imageKey) {
		this.imageKey = imageKey;
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

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<SwagItemComment> getComments() {
		Collections.sort(comments);
		return comments;
	}

	public void setComments(List<SwagItemComment> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((averageRating == null) ? 0 : averageRating.hashCode());
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + ((company == null) ? 0 : company.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + Arrays.hashCode(imageBytes);
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((numberOfRatings == null) ? 0 : numberOfRatings.hashCode());
		result = prime * result
				+ ((ownerGoogleID == null) ? 0 : ownerGoogleID.hashCode());
		result = prime * result
				+ ((ownerNickName == null) ? 0 : ownerNickName.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		return result;
	}

	/**
	 * Note, Blobs and children are lazy loaded. You can't just compare
	 * description fields for example, you have to actually call the
	 * getDescription() method to have it loaded. Same with image (since it's a
	 * child object).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SwagItem other = (SwagItem) obj;
		if (averageRating == null) {
			if (other.averageRating != null)
				return false;
		} else if (!averageRating.equals(other.averageRating))
			return false;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (company == null) {
			if (other.company != null)
				return false;
		} else if (!company.equals(other.company))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (getDescription() == null) {
			if (other.getDescription() != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (!Arrays.equals(imageBytes, other.imageBytes))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (numberOfRatings == null) {
			if (other.numberOfRatings != null)
				return false;
		} else if (!numberOfRatings.equals(other.numberOfRatings))
			return false;
		if (ownerGoogleID == null) {
			if (other.ownerGoogleID != null)
				return false;
		} else if (!ownerGoogleID.equals(other.ownerGoogleID))
			return false;
		if (ownerNickName == null) {
			if (other.ownerNickName != null)
				return false;
		} else if (!ownerNickName.equals(other.ownerNickName))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		return true;
	}

}