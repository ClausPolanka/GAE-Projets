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

import java.util.Date;
import java.util.logging.Logger;

import javax.jdo.JDOCanRetryException;
import javax.jdo.JDOException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.ChannelServer;
import com.metadot.book.connectr.server.utils.ServletHelper;
import com.metadot.book.connectr.server.utils.ServletUtils;

public class LoginHelper extends RemoteServiceServlet {
  private static final long serialVersionUID = 2888983680310646846L;

  private static Logger logger = Logger.getLogger(LoginHelper.class.getName());
  private static final int NUM_RETRIES = 5;
  

  static public String getApplitionURL(HttpServletRequest request) {

    if (ServletHelper.isDevelopment(request)) {
      return "http://127.0.0.1:8888/ConnectrApp.html?gwt.codesvr=127.0.0.1:9997";
    } else {
      return ServletUtils.getBaseUrl(request);
    }

  }

  static public UserAccount getLoggedInUser(HttpSession session, PersistenceManager pm) {

    boolean localPM = false;

    if (session == null)
      return null; // user not logged in

    String userId = (String) session.getAttribute("userId");
    if (userId == null)
      return null; // user not logged in

    Long id = Long.parseLong(userId.trim());

    if (pm == null) {
      // then create local pm
      pm = PMF.getNonTxnPm();
      localPM = true;
    }

    String query = "select from " + UserAccount.class.getName() + " where id == :userId";
    Query q = pm.newQuery(query);
    q.setUnique(true);

    try {
      UserAccount u = (UserAccount) q.execute(id);
      u.setLastActive(new Date());
      return u;
    } finally {
      if (localPM) {
        pm.close();
      }
    }

  }

  static public boolean isLoggedIn(HttpServletRequest req) {

    if (req == null)
      return false;
    else {
      HttpSession session = req.getSession();
      if (session == null) {
        logger.info("Session is null...");
        return false;
      } else {
        Boolean isLoggedIn = (Boolean) session.getAttribute("loggedin");
        if(isLoggedIn == null){
          logger.info("Session found, but did not find loggedin attribute in it: user not logged in");
          return false;
        } else if (isLoggedIn){
          logger.info("User is logged in...");
          return true;
        } else {
          logger.info("User not logged in");
          return false;
        }
      }
    }
  }

  public UserAccount loginStarts(HttpSession session, UserAccount user) {
    UserAccount aUser = UserAccount.findOrCreateUser(user);
    UserAccount u = null;

    // update user info under transactional control
    PersistenceManager pm = PMF.getTxnPm();
    Transaction tx = pm.currentTransaction();
    try {
      for (int i = 0; i < NUM_RETRIES; i++) {
        tx = pm.currentTransaction();
        tx.begin();
        u = (UserAccount) pm.getObjectById(UserAccount.class, aUser.getId());
        String channelId = ChannelServer.createChannel(u.getUniqueId());
        u.setChannelId(channelId);
        u.setLastActive(new Date());
        u.setLastLoginOn(new Date());
        try {
          tx.commit();
          // update session if successful
          session.setAttribute("userId", String.valueOf(u.getId()));
          session.setAttribute("loggedin", true);
          break;
        }
        catch (JDOCanRetryException e1) {
          if (i == (NUM_RETRIES - 1)) { 
            throw e1;
          }
        }
      } // end for
    } 
    catch (JDOException e) {
      e.printStackTrace();
      return null;
    } 
    finally {
      if (tx.isActive()) {
        logger.severe("loginStart transaction rollback.");
        tx.rollback();
      }
      pm.close();
    }

    return u;
  }

}
