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
import java.util.HashSet;
import java.util.Set;
import java.net.URL;

import javax.jdo.PersistenceManager;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import javax.jdo.annotations.PrimaryKey;

import com.metadot.book.connectr.server.PMF;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.util.logging.Logger;

/**
 * The FeedIndex persistence-capable class is used in conjunction with the FeedInfo class
 * to store feed information in the Datastore.  This class points to the Friend object(s) 
 * for which this feed was specified.
 * (See the book which accompanies this app for more information
 * about the FeedIndex/FeedInfo design).
 */
@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class FeedIndex implements Serializable {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private Set<String> friendKeys;

  private static Logger logger = Logger.getLogger(FeedIndex.class.getName());

  public FeedIndex(String fkey, String url) {
    this.friendKeys = new HashSet<String>();
    this.friendKeys.add(fkey);
    KeyFactory.Builder keyBuilder = 
      new KeyFactory.Builder(FeedInfo.class.getSimpleName(), url);
    keyBuilder.addChild(FeedIndex.class.getSimpleName(), url);
    // make the child key
    Key ckey = keyBuilder.getKey();
    this.key = ckey;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public Key getKey() {
    return key;
  }

  private Set<String> getFriendKeys() {
    return friendKeys;
  }

  private void addFriendKey(String fkey) {
    this.friendKeys.add(fkey);
  }

  // used for existing URLs lists.
  public static Set<String> updateFeedURLs (Set<String> newurls, Set<String> origUrls, String id, boolean replace) {
    if (id == null || newurls == null || origUrls == null ) {
      return null;
    }
    Set<String> added = new HashSet<String>(newurls);
    added.removeAll(origUrls);
    Set<String> badUrls = FeedIndex.addFeedsFriend(added, id);
    if (replace) {
      Set<String> removed = new HashSet<String>(origUrls);
      removed.removeAll(newurls);
      FeedIndex.removeFeedsFriend(removed, id);
    }
    return badUrls;
  }

  /**
   * used for Friends with no existing urls
   */ 
  public static Set<String> addFeedURLs (Set<String> urls, String id) {
    if (id == null || urls == null) {
      return null;
    }
    Set<String> badUrls = FeedIndex.addFeedsFriend(urls, id);
    return badUrls;
  }


  public static FeedIndex findFeedIndex(String url, PersistenceManager pm) {
    FeedIndex fi = null;
    // query for a feed index with that url
    try {
      KeyFactory.Builder keyBuilder = new KeyFactory.Builder(FeedInfo.class.getSimpleName(), url);
      keyBuilder.addChild(FeedIndex.class.getSimpleName(), url);
      Key ckey = keyBuilder.getKey();
      fi = pm.getObjectById(FeedIndex.class, ckey);
    }
    catch (javax.jdo.JDOObjectNotFoundException e) {
      logger.info(e.getMessage());
    }
    return fi;
  }

  public static void removeFeedsFriend(Set<String> urls, String fkey) {
    for (String url : urls) {
      removeFeedFriend(url, fkey);
    }
  }

  public static void removeFeedFriend(String urlstring, String fkey) {

    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      FeedIndex findex = findFeedIndex(urlstring, pm);
      if (findex != null) {
        Set<String> fkeys = findex.getFriendKeys();
        fkeys.remove(fkey);
        if (fkeys.isEmpty()) {
          logger.info("orphaned feed - deleting - " + urlstring);
          // then delete parent
          pm.deletePersistent(pm.getObjectById(FeedInfo.class, findex.getKey().getParent()));
          // then delete index obj
          pm.deletePersistent(findex);
        }
        else {
          // update the set of users associated with this feed
          FeedInfo feedInfo = pm.getObjectById(FeedInfo.class, urlstring);
          Set<Long> ukeys = findex.findUkeys();
          feedInfo.setUkeys(ukeys);
        }
        pm.makePersistent(findex);
      }
    }
    finally {
      pm.close();
    }
  }

  public static Set<String> addFeedsFriend(Set<String> urls, String fkey) {

    boolean status;
    Set<String> badUrls = new HashSet<String>();
    for (String url: urls) {
      try {
        status = FeedIndex.addFeedFriend(url, fkey);
        if (status == false) { // then malformed url or some issue with the feed
          badUrls.add(url);
        }
      }
      catch (Exception e) { // also treat url as bad if anything else goes wrong
        // [if timeout issue on initial add, user can try again to add feed ]
        badUrls.add(url);
      }
    } // end for
    return badUrls;
  }


  /**
   * build a new feed object if necessary, otherwise add fkey to existing feedinfo object
   */
  public static boolean addFeedFriend(String urlstring, String fkey) {

    FeedInfo feedInfo = null;
    FeedIndex findex = null;
    boolean status = false;
    SyndFeedInfo fi = null;

    PersistenceManager pm = PMF.get().getPersistenceManager();
    PersistenceManager pm2 = PMF.get().getPersistenceManager();

    try {
      // first just check if the url is valid -- if it's not, nothing else to do.
      URL url = new URL(urlstring);
      logger.fine("looking for feedindex for " + urlstring);
      findex = findFeedIndex(urlstring, pm);
      if (findex == null) { 
        // then build new
        logger.fine("building new feed for " + urlstring);
        fi = FeedInfo.feedFetcher.retrieveFeedInfo(url, null);
        // create parent
        feedInfo = new FeedInfo(fi, urlstring, fkey);
        pm.makePersistent(feedInfo);
        // create child
        findex = new FeedIndex(fkey, urlstring);
        pm.makePersistent(findex);
      }
      else { // existing feed info for that url
        findex.addFriendKey(fkey);
        logger.info("added friendKey " + fkey + " for " + urlstring);
        pm.makePersistent(findex);
      }
      feedInfo = pm2.getObjectById(FeedInfo.class, urlstring);
      // update the set of users associated with this feed
      Set<Long> ukeys = findex.findUkeys();
      feedInfo.setUkeys(ukeys);
      feedInfo.updateRequestedFeed(pm2);
      status = true;
    } 
    catch (Exception e) {
      logger.warning(e.getMessage());
    } 
    finally {
      pm2.close();
      pm.close();
    }
    return status;
  }

  protected Set<Long> findUkeys() {

    Set<Long> ukeys = new HashSet<Long>();
    Long ukey;
    for (String fk : getFriendKeys()) {
      ukey = KeyFactory.stringToKey(fk).getParent().getId();
      ukeys.add(ukey);
    }
    return ukeys;
  }

} // end class

