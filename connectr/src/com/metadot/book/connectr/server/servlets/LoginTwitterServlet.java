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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.http.RequestToken;

import com.metadot.book.connectr.server.AppLib;
import com.metadot.book.connectr.server.utils.AuthenticationProvider;


@SuppressWarnings("serial") public class LoginTwitterServlet extends LoginSuperServlet {
  private static Logger log = Logger.getLogger(LoginTwitterServlet.class
      .getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    Twitter twitter = new TwitterFactory().getInstance();
    // get auth info from system properties
    String key = AuthenticationProvider.getProp("twitter-consumer-key");
    String secret = AuthenticationProvider.getProp("twitter-consumer-secret");
    
    if(key == null || secret == null){
      response.setContentType("text/html");
      response.getWriter().print(AppLib.INFONOTFOUND);
      return;
    }

    try {
      twitter.setOAuthConsumer(key, secret);
      String callbackURL = buildCallBackURL(request, AuthenticationProvider.TWITTER);
      RequestToken token = twitter.getOAuthRequestToken(callbackURL);
      request.getSession().setAttribute("requestToken", token);
      String loginURL = token.getAuthenticationURL() + "&force_login=true";
      log.info("Redirecting to: " + loginURL);
      response.sendRedirect(loginURL);

    } catch (TwitterException e) {
      response.setContentType("text/html");
      response.getWriter().print("<p>" + e.getMessage() + "</p>");
      response.getWriter().print(AppLib.INFONOTFOUND);
      return;
      // e.printStackTrace();
    }

  }
}
