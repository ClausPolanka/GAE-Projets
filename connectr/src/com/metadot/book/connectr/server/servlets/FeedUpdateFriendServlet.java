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
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FeedIndex;
import com.metadot.book.connectr.server.domain.FeedInfo;


/**
 * Updates the feeds (as necessary) listed for the given Friend
 */
@SuppressWarnings("serial")
public class FeedUpdateFriendServlet extends HttpServlet {
  
  private static Logger logger = Logger.getLogger(FeedUpdateFriendServlet.class.getName());

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    Query q = null;
    try {
      String fkey = req.getParameter("fkey");
      if (fkey != null) {

        logger.info("in FeedUpdateFriendServlet, updating feeds for: " + fkey);
       // query for matching FeedIndex keys 
        q = pm.newQuery("select key from " + FeedIndex.class.getName() + " where friendKeys == :id");
        List<?> ids = (List<?>) q.execute(fkey);
        if (ids.size() == 0) {
          return;
        }
        // else, get the parent keys of the ids
        Key k = null;
        List<Key> parentlist = new ArrayList<Key>();
        for (Object id : ids) {
          // cast to key
          k = (Key)id;
          parentlist.add(k.getParent());
        }
        // fetch the parents using the keys
        Query q2 = pm.newQuery("select from " + FeedInfo.class.getName() + " where urlstring == :keys");
        // setting query deadline, allowing eventual consistency on read
        q2.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");
        // we could also set timeout if we wanted a short-latency query. (We would then 
        // have to handle the query timeout properly).
        // q.setTimeoutMillis(10000);
        @SuppressWarnings("unchecked")
        List<FeedInfo> results = (List<FeedInfo>) q2.execute(parentlist);
        
        if (results.iterator().hasNext()) {
          for (FeedInfo fi : results) {
          logger.fine("working on feedinfo " + fi);
          fi.updateRequestedFeed(pm);
          }
        } 
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
    finally {
      if (q != null) {
        q.closeAll();
      }
      pm.close();
    }
  }
    
} //end class
