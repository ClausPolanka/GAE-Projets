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
package com.metadot.book.connectr.server.servlets;


import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FeedIndex;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.utils.Utils;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;


@SuppressWarnings("serial")
public class UpdateFeedUrlsServlet extends HttpServlet {
  
  private static Logger logger = Logger.getLogger(UpdateFeedUrlsServlet.class.getName());
  private static Properties props = System.getProperties();
  private String feedids_nmspce;
  

  @SuppressWarnings("unchecked")
  public void doPost(HttpServletRequest req, HttpServletResponse resp)  throws IOException {

    feedids_nmspce = props.getProperty("com.metadot.connectr.feedids-cache");

    Set<String> badurls = null;
    // deserialize the request
    Object o = Utils.deserialize(req);
    Map<String,Object> hm = (Map<String, Object>) o;
    logger.fine("in UpdateFeedUrlsServlet; deserialized object is: " + hm);
    Set<String> origurls = (Set<String>)hm.get("origurls");
    Set<String> newurls = (Set<String>)hm.get("newurls");
    Boolean replace = (Boolean)hm.get("replace");
    Boolean delete = (Boolean)hm.get("delete");
    String fid = (String)hm.get("fid");

    // w/out the Friend id, can't do anything
    if (fid == null) {
      return;
    }

    CacheSupport.cacheDelete(feedids_nmspce, fid);
    if (delete != null && delete) {
      if (origurls != null) {
        logger.fine("calling FeedIndex.removeFeedsFriend with original urls: " + origurls);
        FeedIndex.removeFeedsFriend(origurls, fid);
      }
      else {
        return;
      }
    }

    // else, add or update operation
    else { 
      if (newurls == null) {
        return; // don't have the correct info
      }
      if (replace == null) {
        replace = true;
      }
      if (origurls == null || origurls.isEmpty()) {
        // then add only -- no old ones to deal with
        logger.info("calling addFeedURLs with new urls " + newurls + " and fid " + fid);
        badurls = FeedIndex.addFeedURLs(newurls, fid);
      }
      else { //update
        logger.info("doing an update-- calling FeedIndex.updateFeedURLs with new urls " + 
          newurls + "\nand original urls " + origurls + " and fid " + fid);
        badurls = FeedIndex.updateFeedURLs(newurls, origurls, fid, replace);
      }
      if (!badurls.isEmpty()) {
        // then update the Friend to remove those bad urls from its set.  
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Friend.removeBadURLs(badurls, fid, pm);
        }
        finally {
          pm.close();
        }
      }
    }
  }
    
} //end class
