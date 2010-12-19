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

import org.json.JSONException;
import org.json.JSONObject;

import com.metadot.book.connectr.server.LoginHelper;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.AuthenticationProvider;
import com.metadot.book.connectr.server.utils.UrlFetcher;

@SuppressWarnings("serial") public class LoginFacebookCallbackServlet extends HttpServlet {
  private static Logger log = Logger
      .getLogger(LoginFacebookCallbackServlet.class.getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String code = request.getParameter("code");
    String callbackURL = "http://connectr.appspot.com/loginfacebook";

    if (code != null && !code.isEmpty()) {
      // -------------------------------------------------- step 2
      log.info("STEP 2: got code=" + code);
      String step2 = "https://graph.facebook.com/oauth/access_token"
          + "?client_id=zzzzzzzzzzzzzzz" + "&redirect_uri="
          + callbackURL
          + "&client_secret=sssssssssssssssssssssssss"
          + "&code=" + code;

      /*
       * Get access token
       */
      log.info("requesting access token url=" + step2);
      String resp = UrlFetcher.get(step2);
      log.info("Response = " + resp);
      int beginIndex = "access_token=".length();
      String token = resp.substring(beginIndex);
      log.info("Extracted token = " + token);

      /*
       * Get user info
       */
      String url = "https://graph.facebook.com/me?access_token=" + token;
      log.info("requesting user info: " + url);
      resp = UrlFetcher.get(url);
      log.info("Response: " + resp);
      UserAccount connectr = extractUserInfo(resp);
      connectr = new LoginHelper()
          .loginStarts(request.getSession(), connectr);
      log.info("User id is logged in:" + connectr.getId().toString());

      /*
       * All done. Let's go home.
       */
      response.sendRedirect(LoginHelper.getApplitionURL(request));

    }
  }

  private UserAccount extractUserInfo(String resp) {
    log.info("Extracting user info");
    JSONObject j;
    UserAccount u = null;
    try {
      j = new JSONObject(resp);
      String first = j.getString("first_name");
      String last = j.getString("last_name");
      String id = j.getString("id");
      log.info("User info from JSON: " + first + " " + last + " id = "
          + id);
      u = new UserAccount(id, AuthenticationProvider.FACEBOOK);
      u.setName(first + " " + last);
    } catch (JSONException e) {
      e.printStackTrace();
    }
    return u;
  }
}
