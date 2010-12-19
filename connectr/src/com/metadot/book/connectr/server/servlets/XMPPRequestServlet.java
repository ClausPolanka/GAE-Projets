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

import com.google.appengine.api.xmpp.*;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * The XMPPRequestServlet is used to send add/remove requests to the
 * 'XMPP breaking news gateway' app, an auxiliary functionality of Connectr
 * used to demonstrate the App Engine XMPP API.
 */
@SuppressWarnings("serial")
public class XMPPRequestServlet extends HttpServlet {

  private static final Logger logger = Logger.getLogger(XMPPRequestServlet.class.getName());
  public static final String XMPP_GATEWAY = "connectr-xmppagent@appspot.com";
  private static final String ADD = "add";
  private static final String REMOVE = "remove";

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
    doPost(req, resp);
  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException  {
    
    String reqString = req.getParameter("request");
    if (reqString != null && reqString.equalsIgnoreCase("add")) {
      sendRequest(ADD);
      resp.setContentType("text/plain");
      resp.getWriter().println("The 'add' request was sent to " + XMPP_GATEWAY);
    }
    else if (reqString != null && reqString.equalsIgnoreCase("remove")) {
      sendRequest(REMOVE);
      resp.setContentType("text/plain");
      resp.getWriter().println("The 'remove' request was sent to " + XMPP_GATEWAY);
    }
    else {
      logger.info("in XMPPRequestServlet- did not understand request: " + reqString);
    }
  }
  
  
  public void sendRequest(String request) {

    JID jid = new JID(XMPP_GATEWAY);
    Message xMessage = new MessageBuilder()
      .withRecipientJids(jid)
      .withBody(request)
      .build();
    boolean result = sendMessage(xMessage, jid);
    logger.info("request: " + xMessage + " sent with result: " + result);
  }
  
  private boolean sendMessage(Message xMessage, JID jid) {

    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    boolean messageSent = false;
    SendResponse status = xmpp.sendMessage(xMessage);
    messageSent = (status.getStatusMap().get(jid) == SendResponse.Status.SUCCESS);

    return messageSent;
  }

}
