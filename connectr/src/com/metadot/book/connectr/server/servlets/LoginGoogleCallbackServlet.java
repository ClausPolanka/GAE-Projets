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
import java.security.Principal;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metadot.book.connectr.server.LoginHelper;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.AuthenticationProvider;

@SuppressWarnings("serial") 
public class LoginGoogleCallbackServlet extends HttpServlet {
  private static Logger log = Logger.getLogger(LoginGoogleCallbackServlet.class
      .getName());

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    Principal googleUser = request.getUserPrincipal();
    if (googleUser != null) {
      // update or create user
      UserAccount u = new UserAccount(googleUser.getName(),  AuthenticationProvider.GOOGLE);
      u.setName(googleUser.getName());
      UserAccount connectr = new LoginHelper().loginStarts(request.getSession(), u);
  
      log.info("User id:" + connectr.getId().toString() + " " + request.getUserPrincipal().getName());
    }
    response.sendRedirect(LoginHelper.getApplitionURL(request));
  }
}
