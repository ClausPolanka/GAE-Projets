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
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.StreamItem;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.ChannelServer;
import com.metadot.book.connectr.shared.messages.ContentAvailableMessage;



/**
 * The UserNotifServlet servlet determines whether there is any new content for the feeds
 * associated with the Friends of a given user, and if so, sends a push
 * notification to the client (if the channel API has been enabled).
 * This servlet is enqueued as a task that runs after an update of a user's feeds
 */
@SuppressWarnings("serial")
public class UserNotifServlet extends HttpServlet {
  
  private static Logger logger = Logger.getLogger(UserNotifServlet.class.getName());
  
  private UserAccount getUserAccount(String userId, PersistenceManager pm) {
    UserAccount userPrefs = null;
    Query q = null;
    try {
      q = pm.newQuery(UserAccount.class, "uniqueId == :uniqueId");
      q.setRange(0,1);
      @SuppressWarnings("unchecked") List<UserAccount> results = (List<UserAccount>) q.execute(userId);
      if (results.iterator().hasNext()) {
        for (UserAccount u : results) {
          userPrefs = u;
        }
      } 
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      q.closeAll();
    }
    return userPrefs;
  }
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
      doPost(req, resp);
    }
  
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    String userId = req.getParameter("uniqueid");
    UserAccount user = null;
    Query q = null;
    if (!ChannelServer.channelAPIEnabled()) {
      return;
    }
    try {
      user = getUserAccount(userId, pm);
      logger.info("in UserNotifServlet doPost for user " + user.getId());
      if (user != null) {
        // see if there exist any stream items associated with that user, newer than the lastReported date
        q = pm.newQuery(StreamItem.class, "date > :d1 && ukeys == :u1");
        q.setOrdering("date desc");
        q.setRange(0, 1);
        @SuppressWarnings("unchecked") 
        List<StreamItem> sitems = (List<StreamItem>) q.execute(user.getLastReported(), user.getId());
        if (sitems.iterator().hasNext()) {
           // if so (if newer), call pushMessage to trigger the 'push' of the new content notification
           StreamItem sitem = sitems.iterator().next();
           user.setLastReported(sitem.getDate());
           logger.info("pushing 'new content' notification");
           ChannelServer.pushMessage(user, new ContentAvailableMessage());
        }
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

  }


} //end class
