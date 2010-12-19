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
package com.metadot.book.connectr.server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.metadot.book.connectr.server.domain.FeedInfo;
import com.sun.syndication.fetcher.FetcherEvent;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;
import com.sun.syndication.io.FeedException;

import java.util.logging.Logger;


/**
 * subclass of HttpURLFeedFetcher, which works with the FeedInfo persistent object
 */
public class FriendFeedFetcher extends com.sun.syndication.fetcher.impl.HttpURLFeedFetcher {
  
  private static Logger logger = Logger.getLogger(FriendFeedFetcher.class.getName());

  public FriendFeedFetcher(FeedFetcherCache ffc) {
    super(ffc);
  }
  
  public FriendFeedFetcher() {
    super();
  }

  public SyndFeedInfo retrieveFeedInfo(URL feedUrl, FeedInfo dtsFeedInfo) 
    throws IllegalArgumentException, IOException, FeedException, FetcherException {
    if (feedUrl == null) {
      throw new IllegalArgumentException("null is not a valid URL");
    }

    URLConnection connection = feedUrl.openConnection();
    if (!(connection instanceof HttpURLConnection)) {       
      throw new IllegalArgumentException(feedUrl.toExternalForm() + " is not a valid HTTP Url");
    }
    HttpURLConnection httpConnection = (HttpURLConnection)connection;   

    SyndFeedInfo syndFeedInfo = null;
    if (dtsFeedInfo != null) {
      setRequestHeaders(connection, dtsFeedInfo.getLastModified(), dtsFeedInfo.getETag());
    }
    else {
      setRequestHeaders(connection, null, null);
    }
    httpConnection.connect();
    try {
      fireEvent(FetcherEvent.EVENT_TYPE_FEED_POLLED, connection);

      if (dtsFeedInfo == null) {
        // this is a feed that hasn't been retrieved
        syndFeedInfo = new SyndFeedInfo();
        try {
          resetFeedInfo(feedUrl, syndFeedInfo, httpConnection);
        }
        catch (java.lang.IllegalArgumentException e) {
          logger.warning("Got exception " + e.getMessage());
          logger.warning("If " + feedUrl + " was a correctly formed Twitter feed, this error is likely due to having the max number of requests for the current hour exceeded");
          throw e;
        }
      } 
      else {
        // check the response code
        int responseCode = httpConnection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_NOT_MODIFIED) {
          logger.fine("*****the feed " + feedUrl + " needs retrieving");
          syndFeedInfo = new SyndFeedInfo();
          resetFeedInfo(feedUrl, syndFeedInfo, httpConnection);
        } 
        else {
          logger.fine("the feed " + feedUrl + " does not need retrieving");
          // the feed does not need retrieving
          fireEvent(FetcherEvent.EVENT_TYPE_FEED_UNCHANGED, connection);
        }
      }
      // if null is returned, feed did not need updating, or there was an error fetching it.
       return syndFeedInfo;
    } 
    finally {
      httpConnection.disconnect();
    }
  }
  
  
  protected void setRequestHeaders(URLConnection connection, Long lm, String eTag) {
       // support the use of both last-modified and eTag headers
       if (lm != null) {         
           connection.setIfModifiedSince(lm.longValue());
       }
       if (eTag != null) {
         connection.setRequestProperty("If-None-Match", eTag);
       }

     // header to retrieve feed gzipped
     connection.setRequestProperty("Accept-Encoding", "gzip");
 
     // set the user agent
     connection.addRequestProperty("User-Agent", getUserAgent());  
     
     if (isUsingDeltaEncoding()) {
         connection.addRequestProperty("A-IM", "feed");
     }   
   }
  
  
} // end class
