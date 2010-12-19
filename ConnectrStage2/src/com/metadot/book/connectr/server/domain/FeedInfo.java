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
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.metadot.book.connectr.server.FriendFeedFetcher;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.server.utils.cache.Cacheable;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

/**
 * The FeedInfo persistence-capable class is used in conjunction with the FeedIndex class
 * to store feed information in the Datastore.  FeedInfo objects hold the actual feed content
 * as a serialized field (stored as a Blob in the Datastore), along with some other
 * bookkeeping information.
 * (See the book which accompanies this app for more information
 * about the FeedIndex/FeedInfo design).
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class FeedInfo implements Serializable, Cacheable {

  protected static FriendFeedFetcher feedFetcher = new FriendFeedFetcher();

  @Persistent
  @PrimaryKey
  private String urlstring;
  
  @Persistent
  private Date dateChecked;
  @Persistent
  private Date dateUpdated;
  @SuppressWarnings("unused")
  @Persistent
  private Date dateRequested;

  @Persistent(serialized = "true")
  private SyndFeedInfo feedInfo;

  @Persistent
  private Long lastModified;
  @Persistent
  private String eTag;
  
  @Persistent
  private String feedTitle;
  
  @Persistent
  Set<Long> ukeys;

  private static Logger logger = Logger.getLogger(FeedInfo.class.getName());
  private int update_mins;
  private static Properties props = System.getProperties();

  private static final int DEFAULT_UPDATE_MINS = 2; // default latency interval before
            // a feed is checked again

  public FeedInfo(SyndFeedInfo feedInfo, String url, String fkey) {
    this.feedInfo = feedInfo;
    this.feedTitle = feedInfo.getSyndFeed().getTitle();
    this.dateChecked = new Date();
    this.dateUpdated = new Date();
    this.dateRequested = new Date();
    this.urlstring = url;
    this.ukeys = new HashSet<Long>();

    try {
      // try to set the latency interval from the system property
      update_mins = Integer.parseInt(props.getProperty("com.metadot.connectr.mins-feed-check"));
    }
    catch (Exception e) {
      // if system property not set or valid, use default
      update_mins = DEFAULT_UPDATE_MINS;
    }

    if (feedInfo.getLastModified() instanceof Long) {
      this.lastModified = (Long)feedInfo.getLastModified();
    }
    else {
      this.lastModified = new Long(0);
    }
    this.eTag = feedInfo.getETag();
  }
  

  public String getUrl() {
    return urlstring;
  }
  
  public Set<Long> getUkeys() {
    return ukeys;
  }
  
  public void setUkeys(Set<Long> ukeys) {
    this.ukeys = ukeys;
  }
  
  public String getFeedTitle() {
    return feedTitle;
  }
  
  public Long getLastModified() {
    return this.lastModified;
  }

  private void setLastModified(Object lm) {
    if (lm instanceof Long) {
      this.lastModified = (Long)lm;
    }
    else {
      this.lastModified = new Long(0);
    }
  }

  public String getETag() {
    return eTag;
  }


  public SyndFeedInfo getFeedInfo() {
    return this.feedInfo;
  }
  
  public void removeFromCache() {
    CacheSupport.cacheDelete(this.getClass().getName(), urlstring);
  }

  
  public void addToCache() {
    getFeedInfo();
    logger.info("adding FeedInfo to cache: " + urlstring);
    CacheSupport.cachePut(this.getClass().getName(), urlstring, this);
  }
  
  /**
   * return the feed info, updated as appropriate
   */
  public SyndFeedInfo updateRequestedFeed(PersistenceManager pm) {
    this.dateRequested = new Date();
    updateIfNeeded(pm);
    return this.feedInfo;
  }

  public void updateIfNeeded(PersistenceManager pm) {
    try {
      if (feedNeedsChecking()) {
        logger.info("feed needs checking: "+ urlstring);
        updateFeed(pm);
        pm.makePersistent(this);
      }
    } 
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
  }


  /**
   * update the feed contents
   */
  private void updateFeed(PersistenceManager pm)   {
    logger.info("in UpdateFeed: " + urlstring);
    try {

      this.dateChecked = new Date();
      SyndFeedInfo fi = feedFetcher.retrieveFeedInfo(new URL(this.urlstring), this);
      // if non-null was returned, update the feed contents.
      // null indicates either that the feed did not need updating, or that there was an error fetching it.
      if (fi != null) {
        this.feedInfo = fi;
        this.eTag = fi.getETag();
        setLastModified(fi.getLastModified());
        this.dateUpdated = new Date();
        JDOHelper.makeDirty(this, "feedInfo");
        logger.info("updating feed " + urlstring + " at " + dateUpdated);
      }
    } //end try
    catch (Exception e) {
      logger.warning(e.getMessage());
      // e.printStackTrace();
    }
  }

  /**
   * determine whether the feed needs checking, based on the time last checked
   * and the specified delay in minutes
   */
  private Boolean feedNeedsChecking() {

    long delay = 1000 * 60 * this.update_mins; 
    Date now = new Date();
    long diff = now.getTime() - dateChecked.getTime();
    return (diff > delay);
  }

} // end class

