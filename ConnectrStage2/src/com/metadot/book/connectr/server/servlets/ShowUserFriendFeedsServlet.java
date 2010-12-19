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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FeedIndex;
import com.metadot.book.connectr.server.domain.FeedInfo;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;


/**
 * This servlet can be run to show the items from the user's friend's feeds. 
 * It is run like this: /showuserfeeds?useremail=default@default.com
 * Once the real item display panel is in place, in the 'full' version of Connectr, 
 * this servlet won't be necessary.
 */
@SuppressWarnings("serial")
public class ShowUserFriendFeedsServlet extends HttpServlet {


  private UserAccount getConnectrUser(String uemail, PersistenceManager pm) {
    UserAccount userPrefs = null;
    try {
      Query q = pm.newQuery(UserAccount.class, "emailAddress == :email");
      q.setRange(0,1);
      @SuppressWarnings("unchecked")
      List<UserAccount> results = (List<UserAccount>) q.execute(uemail);
      if (results.iterator().hasNext()) {
        for (UserAccount u : results) {
          userPrefs = u;
        }
      } 
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return userPrefs;
  }

  @SuppressWarnings("unchecked")
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {

     PersistenceManager pm = PMF.get().getPersistenceManager();

    String respstr = "";
    Query q = null;

    try {
      String uemail = req.getParameter("useremail");
      UserAccount userPrefs = getConnectrUser(uemail, pm);
      if (userPrefs != null) {

        respstr = "<html><head><title>Feed Display for " + uemail + "</title></head><body>";
        // get the Friends for that User
        Set<Friend> fset = userPrefs.getFriends();
        Set<String> feedids = new HashSet<String>();
        List<String> cachedFeedids = new ArrayList<String>();

        q = pm.newQuery("select key from " + FeedIndex.class.getName() + " where friendKeys == :id");
        for (Friend friend : fset) {
          List<?> ids = (List<?>) q.execute(friend.getId());
          Key k;
          for (Object o : ids) {
            k = (Key) o;
            feedids.add(k.getParent().getName());
          }
        }
        
        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        respstr += "<p>feed item titles for " + uemail + ":</p>\n<ul>";

        // weed out the cached feedinfo objects
        FeedInfo fic = null;
        for (String id : feedids) {
          Object o = CacheSupport.cacheGet(FeedInfo.class.getName(), id);
          if (o != null && o instanceof FeedInfo) {
            fic = (FeedInfo) o;
            SyndFeed sf = fic.getFeedInfo().getSyndFeed();
            entries.addAll(sf.getEntries());
            cachedFeedids.add(id); // don't need to fetch from Datastore as below
          }
        }
        feedids.removeAll(cachedFeedids);
        if (feedids.size() > 0) {
          // get the a list of ids of the relevant FeedInfo objects associated with these friends.
          q = pm.newQuery("select from " + FeedInfo.class.getName() + " where urlstring == :keys");
          // allowing eventual consistency on read
          q.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");
          List<FeedInfo> feeds = (List<FeedInfo>) q.execute(feedids);

            for (FeedInfo fi : feeds) {
              SyndFeedInfo sfi = fi.updateRequestedFeed(pm);
              SyndFeed sf = sfi.getSyndFeed();
              // add to combined list of entries for all the feeds
              entries.addAll(sf.getEntries());
            }
          }

        // sort the entries list by pub date
        Collections.sort(entries, new Comparator<SyndEntry>() {
          public int compare(SyndEntry o1, SyndEntry o2) {
            if (o1.getPublishedDate() != null) {
              if (o2.getPublishedDate() != null) {
                      return o2.getPublishedDate().compareTo(o1.getPublishedDate());
              } else {
                return -1;
              }}
            else {
              return 1;
            }
          }});
        for (SyndEntry entry: entries) {
          respstr += "<li><a href=\"" + entry.getLink() + "\" target=_blank>" +
          entry.getTitle() + "</a><br>" + "</li>";
        }
        respstr += "</ul></body></html>";
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(respstr);
      } // end if 
      else {
        resp.setContentType("text/plain");
        resp.getWriter().println("no User info retrieved based on email " + uemail);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (q != null) {
        q.closeAll();
      }
      pm.close();
    }
  } // end doGet


} //end class
