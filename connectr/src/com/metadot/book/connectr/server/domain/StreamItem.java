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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import com.google.appengine.api.datastore.Text;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.server.utils.cache.Cacheable;
import com.metadot.book.connectr.shared.StreamItemDTO;
import com.metadot.book.connectr.shared.StreamItemSummaryDTO;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndLink;
import com.sun.syndication.feed.synd.SyndPerson;


/**
 * The StreamItem persistence-capable class holds information about feed items. 
 * Objects from this class form the basis of the 'activity stream' display.
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class StreamItem implements Serializable, Cacheable {

  private static final int CACHE_EXPIR = 1440 * 60;  // expire the stream items after 24 hours, in seconds
  private static final int SUMMARY_LENGTH = 246; // length of the summary text
  
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @Extension(vendorName="datanucleus", key="gae.encoded-pk", value="true")
  private String id;
   
  @Persistent
  @Extension(vendorName="datanucleus", key="gae.pk-name", value="true")
  private String url; // this field defines the 'key name' portion of the key

  // we do not need to index every field of the StreamItem objects,
  // so we mark some as 'unindexed'
  
  @Persistent
  private String title;
  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private Text description;

  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private String descrSummary;
  
  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private String feedDescription;

  @Persistent
  private String feedUrl;

  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private String imageUrl;

  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private String feedTitle;
  @Persistent
  @Extension(vendorName = "datanucleus", key = "gae.unindexed", value="true")
  private String author;

  @Persistent
  private Date date;
  
  @Persistent
  Set<Long> ukeys;

  public StreamItem() {
  }

  public StreamItem(String title, String description, String feedDescription, Date date, 
      String feedTitle, String author, String url, String feedUrl, String imageUrl, Set<Long> ukeys) {
    this();
    this.title = title; 
    this.description = new Text(description); 
    this.feedDescription = feedDescription;
    this.date = date;
    this.author = author;
    this.feedTitle = feedTitle;
    this.url = url;
    this.feedUrl = feedUrl;
    this.imageUrl = imageUrl;
    this.descrSummary = null;
    this.ukeys = ukeys;

  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }

  public void setDescription(String description) {
    this.description = new Text(description);
  }

  public Text getDescription() {
    return description;
  }

  public String getId() {
    return id;
  }
  
  public Set<Long> getUkeys() {
    return ukeys;
  }
  
  public void setUkeys(Set<Long> ukeys) {
    this.ukeys = ukeys;
  }

  public String getFeedDescription() {
    return feedDescription;
  }

  public void setFeedDescription(String feedDescription) {
    this.feedDescription = feedDescription;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getFeedUrl() {
    return feedUrl;
  }

  public void setFeedUrl(String feedUrl) {
    this.feedUrl = feedUrl;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getFeedTitle() {
    return feedTitle;
  }

  public void setFeedTitle(String feedTitle) {
    this.feedTitle = feedTitle;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  

  public StreamItemDTO toDTO() {
      return new StreamItemDTO(id, url, title, description.getValue(), descrSummary, feedDescription,
        feedUrl, imageUrl, feedTitle, author, date);
  }

  public void removeFromCache() {
    CacheSupport.cacheDelete(this.getClass().getName(), id);
  }

  
  private String buildDescrSummary() {
    
    String cleaned = Jsoup.clean(description.getValue(), Whitelist.none());
    if (cleaned.length() > SUMMARY_LENGTH) {
      this.descrSummary = cleaned.substring(0,SUMMARY_LENGTH - 1) + "...";
    }
    else {
      this.descrSummary = cleaned;
    }
    return this.descrSummary;
  }

  private String getDescrSummary() {
    
    if (this.descrSummary != null) {
      return this.descrSummary;
    }
    else {
      return buildDescrSummary();
    }
  }
  
  /**
   * returns list of feed entries converted to StreamItem objects
   */
  public static List<String> buildItems(FeedInfo fi, PersistenceManager pm) {

    Set<Long> ukeys = fi.getUkeys();
    SyndFeed sf = fi.getFeedInfo().getSyndFeed();
    @SuppressWarnings("unchecked")
    List<SyndEntry> entries = sf.getEntries();
    StreamItem item;
    for (SyndEntry entry : entries) {
      item = StreamItem.buildItem(fi.getUrl(), sf, entry, ukeys);
      pm.makePersistent(item);
    }
    return null;
  }
  
  /**
   * convert a feed entry to a StreamItem
   */
  @SuppressWarnings("unchecked")
  private static StreamItem buildItem(String urlstring, SyndFeed sf, SyndEntry entry,
    Set<Long> ukeys) {

      StreamItem item = null;
      String title = "", url = "", description = "", feedDescription = "", feedTitle = "", 
        author = "", imageUrl = "";
      Date date = null;
      String descr = null;

      try {
        // gather all the information.....
        url = entry.getLink();
        date = entry.getPublishedDate();
        feedTitle = sf.getTitle();
        feedDescription = sf.getDescription();
        title = Jsoup.clean(entry.getTitle(), Whitelist.simpleText());
        // for twitter, first remove the leading author string
        if (url.contains("twitter.com")) { 
          int cindex = title.indexOf(":");
          if (cindex > 0) {
          title = title.substring(cindex + 1);
          }
        }
        if (entry.getDescription() != null) {
          descr = entry.getDescription().getValue();
        }
        if (descr == null) {
          if (entry.getContents().size() > 0) {
            SyndContent content = (SyndContent)entry.getContents().get(0);
            if (content.getType().equalsIgnoreCase("html")) {
              descr = content.getValue();
            }
          }
        }
        if (descr != null) {
          // sanitize the content 
          description = Jsoup.clean(descr, Whitelist.basicWithImages());
        } 

        List<SyndPerson> sauthors = entry.getAuthors();
        List<String> auths = new ArrayList<String>();
        for (SyndPerson auth : sauthors) {
          auths.add(auth.getName());
        }
        author = StringUtils.join(auths, ", ");
        SyndImage eImg = sf.getImage();
        if (eImg != null) {
          imageUrl = eImg.getUrl();
        }
        else {
          // if twitter link... 
          if (url.contains("twitter.com")) {
            // then see if a second link is available-- if so, it should be the link to the image
            List<SyndLink> links = entry.getLinks();
            if (links.size() >= 2) {
              SyndLink imgl = links.get(1);
              imageUrl = imgl.getHref();
            }
          }
        }
        item = new StreamItem(title, description, feedDescription, date, feedTitle, author, 
          url, urlstring, imageUrl, ukeys);
        item.buildDescrSummary();
      }
      catch (Exception e) {
        e.printStackTrace();
      }

      return item;
  }

  public StreamItemSummaryDTO buildSummaryItem() {
    String descrSummary = getDescrSummary();
    StreamItemSummaryDTO summ = new StreamItemSummaryDTO(id, title, url, descrSummary, date, author, imageUrl);
    return summ;
  }

  public StreamItemSummaryDTO addToCacheGetSumm() {
    CacheSupport.cachePutExp(this.getClass().getName(), id, this, CACHE_EXPIR);
    return addSummaryToCache();
  }
  
  public void addToCache() {
      CacheSupport.cachePutExp(this.getClass().getName(), id, this, CACHE_EXPIR);
      addSummaryToCache();
  }
  
  public StreamItemSummaryDTO addSummaryToCache() {
    StreamItemSummaryDTO summ = buildSummaryItem();
    CacheSupport.cachePut(StreamItemSummaryDTO.class.getName(), summ.getId(), summ);
    return summ;
  }
}
