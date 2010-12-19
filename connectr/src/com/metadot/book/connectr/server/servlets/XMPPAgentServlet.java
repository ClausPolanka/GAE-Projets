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
import javax.servlet.http.HttpServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.xmpp.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.SimpleItem;
import com.metadot.book.connectr.server.domain.StreamItem;
import com.metadot.book.connectr.server.utils.Utils;


import com.google.gson.Gson;



@SuppressWarnings("serial")

/**
 * process incoming XMPP messages
 */
public class XMPPAgentServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(XMPPAgentServlet.class.getName());
  private static final String OK = "OK";
  private static final String ERROR = "ERROR";

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {

    PersistenceManager pm = PMF.getNonTxnPm();

    // We assume that only the 'XMPP news gateway' will be trying to 
    // communicate with us, and so we don't branch
    // based on sender.  For more complex message-processing scenarios 
    // we would likely want to check sender.
    try {
      XMPPService xmpp = XMPPServiceFactory.getXMPPService();

      Message msg = xmpp.parseMessage(req);
      JID fromJid = msg.getFromJid();
      String body = msg.getBody();
      logger.info("Received a message from " + fromJid + " and body = " + body);

      if (body.startsWith(OK) || body.startsWith(ERROR)) {
        logger.info("Got ERROR or OK");
      }
      else {
        // process message as a 'SimpleItem' (a special-purpose class schema known by the
        // sender as well, and used only for this communication)
        Gson gson = new Gson();
        StreamItem sitem;
        SimpleItem[] sitems = gson.fromJson(body, SimpleItem[].class);
        for (SimpleItem tw: sitems) {
          // create a stream item from the data
          logger.info("conversion: " + tw);
          sitem = new StreamItem(tw.getTtext(), tw.getTtext() + " [XMPP]", tw.getSource(), tw.getTdate(), 
            "", "",  "http://twitter.com/" +tw.getTname() + "/status/"+ tw.getTid(), 
            "xmpp", "http://connectr.s3.amazonaws.com/xmpp-logo2.gif", null);
          pm.makePersistent(sitem);
        }
      }

    } catch (Exception e) {
      logger.log(Level.SEVERE, Utils.stackTraceToString(e));
    }
    finally {
      pm.close();
    }
  }
}
