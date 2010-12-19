package com.metadot.book.connectr.server.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("serial")
public class FriendDetails implements Serializable {

	private String emailAddress;

	private Set<String> urls;

	public FriendDetails() {
		urls = new HashSet<String>();
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public Set<String> getUrls() {
		return urls;
	}

	public void setUrls(Set<String> urls) {
		this.urls = urls;
	}

	public void addUrl(String url) {
		urls.add(url);
	}

	public void addUrls(Set<String> urls) {
		this.urls.addAll(urls);
	}

	public void removeUrls(Set<String> urls) {
		this.urls.removeAll(urls);
	}

}
