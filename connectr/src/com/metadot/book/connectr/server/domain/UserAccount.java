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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import javax.jdo.Transaction;
import javax.jdo.JDOCanRetryException;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.AppLib;
import com.metadot.book.connectr.server.utils.Utils;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.server.utils.cache.Cacheable;
import com.metadot.book.connectr.shared.UserAccountDTO;


/**
 * The UserAccount persistence-capable class holds information about a
 * given user of Connectr.  A bidirectional JDO owned relationship is used to
 * manage its child Friend data objects.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class UserAccount implements Serializable, Cacheable {

  private static final Logger log = Logger.getLogger(Utils.class.getName());
  private static final int NUM_RETRIES = 5;
  

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String name;

  @Persistent
  private String emailAddress;

  @Persistent
  private Date lastLoginOn;

  @Persistent
  private Date lastActive;
  
  @Persistent
  private Date lastReported;
  
  @Persistent
  private String channelId;


  /**
   * loginId and loginProvider form a unique key. 
   * E.g.: loginId = supercobra, loginProvider = LoginProvider.TWITTER
   */
  @Persistent
  private String uniqueId;

  @Persistent(mappedBy = "userPrefs")
  @Element(dependent = "true")
  private Set<Friend> friends = new HashSet<Friend>();

  public UserAccount() {
  }

  public UserAccount(String loginId, Integer loginProvider) {
    this();
    this.setUniqueId(loginId + "-" + loginProvider);
    this.setName(loginId);
    
  }


  public void setBasicInfo(String name, String emailAddress, String uniqueId) {
    this.name = name;
    this.emailAddress = emailAddress;
    this.uniqueId = uniqueId;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public void addFriend(Friend fr) {
    friends.add(fr);
  }

  public Set<Friend> getFriends() {
    return friends;
  }
  
  public static UserAccount findOrCreateUser(UserAccount user) {

    PersistenceManager pm = PMF.getTxnPm();
    Transaction tx = null;
    UserAccount oneResult = null, detached = null;

    String uniqueId = user.getUniqueId();

    Query q = pm.newQuery(UserAccount.class, "uniqueId == :uniqueId");
    q.setUnique(true);

    // perform the query and creation under transactional control,
    // to prevent another process from creating an acct with the same id.
    try {
      for (int i = 0; i < NUM_RETRIES; i++) {
        tx = pm.currentTransaction();
        tx.begin();
        oneResult = (UserAccount) q.execute(uniqueId);
        if (oneResult != null) {
          log.info("User uniqueId already exists: " + uniqueId);
          detached = pm.detachCopy(oneResult);
        } else {
          log.info("UserAccount " + uniqueId + " does not exist, creating...");
          AppLib.addFriends(user);
          pm.makePersistent(user);
          detached = pm.detachCopy(user);
        }
        try {
          tx.commit();
          break;
        }
        catch (JDOCanRetryException e1) {
          if (i == (NUM_RETRIES - 1)) { 
            throw e1;
          }
        }
      } // end for
    } 
    catch (Exception e) {
      e.printStackTrace();
    } 
    finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
      q.closeAll();
    }
    
    return detached;
  }

  public static UserAccountDTO toDTO(UserAccount user) {
    if (user == null) {
      return null;
    }
    UserAccountDTO accountDTO = new UserAccountDTO(user.getEmailAddress(), user.getName(), user.getUniqueId());
    accountDTO.setChannelId(user.getChannelId());
    return accountDTO;
  }

  public void removeFromCache() {
    CacheSupport.cacheDelete(this.getClass().getName(), id);
  }

  public void addToCache() {
    getFriends(); // force a load of this lazily-loaded field before caching
    CacheSupport.cachePut(this.getClass().getName(), id, this);
  }

  public void setLastLoginOn(Date lastLoginOn) {
    this.lastLoginOn = lastLoginOn;
  }

  public Date getLastLoginOn() {
    return lastLoginOn;
  }


  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public void setLastActive(Date lastActive) {
    this.lastActive = lastActive;
  }

  public Date getLastActive() {
    return lastActive;
  }
  
  public void setLastReported(Date lastReported) {
    this.lastReported = lastReported;
  }

  public Date getLastReported() {
    return lastReported;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getChannelId() {
    return channelId;
  }


}
