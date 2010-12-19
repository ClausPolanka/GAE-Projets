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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.http.RequestToken;

import com.metadot.book.connectr.server.LoginHelper;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.AuthenticationProvider;

@SuppressWarnings("serial") public class LoginTwitterCallbackServlet extends HttpServlet {
  private static Logger log = Logger.getLogger(LoginTwitterCallbackServlet.class.getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Twitter twitter =    new TwitterFactory().getInstance();
    String key = AuthenticationProvider.getProp("twitter-consumer-key");
    String secret = AuthenticationProvider.getProp("twitter-consumer-secret");
    

    RequestToken token = (RequestToken) request.getSession().getAttribute("requestToken");
    String verifier = request.getParameter("oauth_verifier");
    twitter.setOAuthConsumer(key, secret);

    try {
      twitter.getOAuthAccessToken(token, verifier);
      User user = twitter.verifyCredentials();
      log.info("Twitter user found:" + user.getName());
      request.getSession().removeAttribute("requestToken");
      String sid = ((Integer) user.getId()).toString();

      UserAccount u = new UserAccount(sid, AuthenticationProvider.TWITTER);
      // use screen name for uid
      u.setName(user.getScreenName());
      UserAccount connectr = new LoginHelper().loginStarts(request.getSession(), u);
      log.info("User id:" + connectr.getId().toString());

    } catch (TwitterException e) {
      e.printStackTrace();
    }

    response.sendRedirect(LoginHelper.getApplitionURL(request));
  }
}
