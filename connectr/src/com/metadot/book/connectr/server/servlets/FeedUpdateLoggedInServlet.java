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


import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.UserAccount;

/**
 * update all feeds of 'active' users, by spawning a task for each such user.
 */
@SuppressWarnings("serial")
public class FeedUpdateLoggedInServlet extends HttpServlet {
	
  private static final int MINS = 20; // def'n of 'active' user, in minutes
  private static Logger logger = Logger.getLogger(FeedUpdateLoggedInServlet.class.getName());
    
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
      doPost(req, resp);
      resp.setContentType("text/plain");
      resp.getWriter().println("Starting 'feed update logged in' job");
    }
    

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {

    PersistenceManager pm = PMF.getNonTxnPm();
    try {
      List<UserAccount> results = getActiveUsers(pm);
      if (results == null) {
        return;
      }
      for (UserAccount user : results) {
      // For each active user, spawn tasks to update their feeds
      // and push an update notification as necessary
        Queue queue = QueueFactory.getQueue("userfeedupdates");
        queue.add(url("/feedupdateuser").param("uid", user.getUniqueId()).param("notify", "true"));
        logger.info("queueing feedupdateuser for " + user.getUniqueId());
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
    finally {
      pm.close();
    }
  }
  
  @SuppressWarnings("unchecked")
  private List<UserAccount> getActiveUsers(PersistenceManager pm) {

    Long ts = (new Date()).getTime();
    Long prior1 = ts - (1000 * (MINS * 60));
    // we are looking for users active since this date
    Date prior = new Date(prior1);
    Query q = null;
    List<UserAccount> results = null;

    try {
      q = pm.newQuery(UserAccount.class, "lastActive >= :d1");
      results = (List<UserAccount>) q.execute(prior);
      if (results.iterator().hasNext()) {
        return results;
      } 
      else {
        return null;
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
    finally {
      q.closeAll();
    }
    return results;
  }


} //end class
