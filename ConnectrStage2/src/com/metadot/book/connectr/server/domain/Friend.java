/** 
 * Copyright 2010 Daniel Guermeur and Amy Unruh
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   See http://connectrapp.appspot.com/ for a demo, and links to more information 
 *   about this app and the book that it accompanies.
 */
package com.metadot.book.connectr.server.domain;

import java.io.Serializable;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.listener.StoreCallback;

import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.server.utils.cache.Cacheable;
import com.metadot.book.connectr.shared.FriendDTO;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

/**
 * The Friend persistence-capable class stores information about a user's (UserAccount's) friends.
 * The UserAccount:Friend relationship is one-to-many, managed as a bi-directional
 * JDO owned relationship.
 * Each Friend has a FriendDetails child, also managed as a JDO owned relationship.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Friend implements StoreCallback, Serializable, Cacheable {
  
  private static final int CACHE_EXPIR = 600;  // in seconds
  

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
  private String id;

  @Persistent(dependent = "true")
  private FriendDetails details;

  @Persistent
  private String firstName;
  @Persistent
  private String lastName;
  @Persistent
  private String lcFirstName;
  @Persistent
  private String lcLastName;

  // pointer back to userinfo object with which this friend is associated
  @SuppressWarnings("unused")
  @Persistent
  private UserAccount userAccount;

  public Friend() {
    details = new FriendDetails();
  }

  public Friend(FriendDTO friendDTO) {
    this();
    this.setBasicInfo(friendDTO.getFirstName(), friendDTO.getLastName(),
        friendDTO.getEmailAddress());
    this.setUrls(friendDTO.getUrls());
  }
  
  
  public void setBasicInfo(String firstName, String lastName,
      String emailAddress) {

    this.firstName = firstName;
    this.lastName = lastName;
    this.setEmailAddress(emailAddress);
  }

  public FriendSummaryDTO toLightWeightDTO() {
    return new FriendSummaryDTO(id, getFullName());
  }

  public FriendDTO toDTO() {
    FriendDTO friendDTO = new FriendDTO(this.getFirstName(), 
      this.getLastName(), this.getEmailAddress());
    friendDTO.setId(this.getId());
    friendDTO.setUrls(this.getUrls());

    return friendDTO;
  }
    
  public static void removeBadURLs(Set<String> badurls, String id, PersistenceManager pm) {
    Friend f = pm.getObjectById(Friend.class, id);
    Set<String> urls = f.getUrls();
    urls.removeAll(badurls);
  }

  /**
   * update existing friend object based on friend DTO id
   * 
   * @param friendDTO
   */
  public void updateFromDTO(FriendDTO friendDTO) {
    this.firstName = friendDTO.getFirstName();
    this.lastName = friendDTO.getLastName();
    this.setEmailAddress(friendDTO.getEmailAddress());
    details.setUrls(friendDTO.getUrls());
  }

  public String getId() {
    return id;
  }

  
  public FriendDetails getDetails() {
    return details;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFullName() {
    return firstName + " " + lastName;
  }

  public String getLcFirstName() {
    return lcFirstName;
  }

  public String getLcLastName() {
    return lcLastName;
  }

  public String getEmailAddress() {
    return details.getEmailAddress();
  }

  public void setEmailAddress(String emailAddress) {
    details.setEmailAddress(emailAddress);
  }

  public void jdoPreStore() {
    if (getLastName() != null) {
      lcLastName = getLastName().toLowerCase();
    } else {
      lcLastName = null;
    }
    if (getFirstName() != null) {
      lcFirstName = getFirstName().toLowerCase();
    } else {
      lcFirstName = null;
    }
  }
  
  public void removeFromCache() {
    CacheSupport.cacheDelete(this.getClass().getName(), id);
  }

  
  public void addToCache() {
    getDetails(); // force load of lazily-loaded field
    CacheSupport.cachePutExp(this.getClass().getName(), id, this, CACHE_EXPIR);
  }

  
  public Set<String> getUrls() {
    return details.getUrls();
  }
  
  public void setUrls(Set<String> urls) {
    details.setUrls(urls);
  }
  
  public void addUrl(String url) {
    details.addUrl(url);
  }

  public void addUrls(Set<String> urls) {
    details.addUrls(urls);
  }
  
} // end class
