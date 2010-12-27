package com.swagswap.web.gwt.client.domain;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Unfortunate violation of the DRY principle :(
 * DTO to get around problem with GWT RPC-ing appengine Blob and Text appengine types
 *
 */
public class SwagItemCommentGWTDTO implements IsSerializable {
	private Long swagItemKey;
	private String googleID;
	private String swagSwapUserNickname;
	private String commentText;
	private Date created;

	public SwagItemCommentGWTDTO() {
	}
	
	public SwagItemCommentGWTDTO(Long swagItemKey, String googleID, String swagSwapUserNickname, String commentText, Date created) {
		this.swagItemKey=swagItemKey;
		this.googleID=googleID;
		this.swagSwapUserNickname=swagSwapUserNickname;
		this.commentText=commentText;
		this.created=created;
	}

	public Long getSwagItemKey() {
		return swagItemKey;
	}

	public void setSwagItemKey(Long swagItemKey) {
		this.swagItemKey = swagItemKey;
	}

	public String getGoogleID() {
		return googleID;
	}

	public void setGoogleID(String googleID) {
		this.googleID = googleID;
	}

	public String getSwagSwapUserNickname() {
		return swagSwapUserNickname;
	}

	public void setSwagSwapUserNickname(String swagSwapUserNickname) {
		this.swagSwapUserNickname = swagSwapUserNickname;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
