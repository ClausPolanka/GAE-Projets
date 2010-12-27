package com.swagswap.domain;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Text;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class SwagItemComment implements Serializable, Comparable<SwagItemComment> {
	private static final long serialVersionUID = 1L;

	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    //This makes it so we can use a String key instead of a non-portable Google key
    @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
    private String encodedKey;
	
    @Persistent
	private Long swagItemKey;
	
	@Persistent
	private String itemOwnerGoogleID;
	
	@Persistent
	private String swagSwapUserNickname;
	
	@Persistent
	private Text commentText;
	
	@Persistent
	private Date created;
	
	public SwagItemComment() {
	}
	
	public SwagItemComment(Long swagItemKey) {
		this.swagItemKey=swagItemKey;
	}

	public SwagItemComment(Long swagItemKey, String itemOwnerGoogleID, String nickName, String commentText) {
		this(swagItemKey);
		this.itemOwnerGoogleID=itemOwnerGoogleID;
		this.swagSwapUserNickname = nickName;
		this.commentText=new Text(commentText);
		
	}

	public Long getSwagItemKey() {
		return swagItemKey;
	}

	public void setSwagItemKey(Long swagItemKey) {
		this.swagItemKey = swagItemKey;
	}

	public String getSwagSwapUserNickname() {
		return swagSwapUserNickname;
	}
	
	public String getItemOwnerGoogleID() {
		return itemOwnerGoogleID;
	}

	public void setItemOwnerGoogleID(String itemOwnerGoogle) {
		this.itemOwnerGoogleID = itemOwnerGoogle;
	}

	public void setSwagSwapUserNickname(String swagSwapUserNickname) {
		this.swagSwapUserNickname = swagSwapUserNickname;
	}
	
	public String getCommentText() {
		return (commentText == null) ? "" : commentText.getValue();
	}
	
	public String getCommentTextNoHTML() {
		return getCommentText().replaceAll("\\<.*?\\>", "");
	}

	//creating a new Text(null) causes a datanucleus Exception
	public void setCommentText(String commentText) {
		this.commentText = new Text((commentText == null) ? "" : commentText);
	}
	
	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "SwagItemComment [encodedKey=" + encodedKey + ", swagItemKey="
				+ swagItemKey + ", swagSwapUserNickname=" + swagSwapUserNickname + "]";
	}

	public int compareTo(SwagItemComment comment) {
		if (comment == null) {
			throw new NullPointerException("Cannot compareTo null");
		}
		if (SwagItemComment.class != comment.getClass()) {
			throw new IllegalArgumentException("Cannot compare to a " + comment.getClass());
		}
		return -1 * this.created.compareTo(comment.getCreated()); //desc order
	}
	
	
}
