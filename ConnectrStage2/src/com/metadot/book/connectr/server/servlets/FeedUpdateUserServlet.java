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
import javax.servlet.http.*;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;


import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;


@SuppressWarnings("serial")
public class FeedUpdateUserServlet extends HttpServlet {
  
  private static Logger logger = Logger.getLogger(FeedUpdateUserServlet.class.getName());
  
  
  private UserAccount getUserAccount(String userEmail, PersistenceManager pm) {
    UserAccount userAccount = null;
    Query q = null;
    try {
      q = pm.newQuery(UserAccount.class, "emailAddress == :emailAddress");
      q.setRange(0,1);
      @SuppressWarnings("unchecked")
      List<UserAccount> results = (List<UserAccount>) q.execute(userEmail);
      if (results.iterator().hasNext()) {
        for (UserAccount u : results) {
          userAccount = u;
        }
      } 
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
    finally {
      q.closeAll();
    }
    return userAccount;
  }
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
      doPost(req, resp);
    }
  
  /**
   * Update all feeds (as needed) for the friends of the given UserAccount.  
   * This is done by spawning a new subtask (/feedupdatefr) for each friend.
   */
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    String userEmail = req.getParameter("useremail");
    UserAccount userAccount = null;
    try {
      userAccount = getUserAccount(userEmail, pm);
      if (userAccount != null) {
        Set<Friend> friends = userAccount.getFriends();
        // get the default queue
        Queue queue = QueueFactory.getDefaultQueue();
        for (Friend fr : friends ){
          // spawn off tasks to fetch the Friend-associated urls
          queue.add(url("/feedupdatefr").param("fkey", fr.getId()));
          logger.fine("queueing for " + fr.getId());
        }
      }
    }
    finally {
      pm.close();
    }
    resp.setContentType("text/plain");
    if (userAccount != null) {
      resp.getWriter().println("queued up friend feed fetches");
    }
    else {
      resp.getWriter().println("no matching user found");
    }
  }



} //end class
