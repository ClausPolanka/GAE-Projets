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
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.metadot.book.connectr.server.LoginHelper;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.ChannelServer;
import com.metadot.book.connectr.shared.messages.ChannelTextMessage;
/**
 * Use this to test the Channel API manually by going to the servlet URL in a web browser 
 * where the GWT client is *already logged in*.
 *
 */
@SuppressWarnings("serial") public class TestPushServlet extends HttpServlet {
  private static Logger logger = Logger.getLogger(UserNotifServlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    sendMessage(req, resp);
  }

  public void sendMessage(HttpServletRequest req, HttpServletResponse resp) {

    if (!ChannelServer.channelAPIEnabled()) {
      logger.info("Channel API not enabled");

      return;
    }
    
    HttpSession session = req.getSession();
    PersistenceManager pm = PMF.get().getPersistenceManager();
    UserAccount user = LoginHelper.getLoggedInUser(session, pm);

    logger.info("trying to push a message to channel: " + user.getChannelId());
    ChannelServer.pushMessage(user, new ChannelTextMessage("Hello from push"));
  }

} // end class
