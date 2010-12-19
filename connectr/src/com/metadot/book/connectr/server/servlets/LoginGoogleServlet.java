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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.metadot.book.connectr.server.utils.AuthenticationProvider;

public class LoginGoogleServlet extends LoginSuperServlet {
  private static final long serialVersionUID = -4565961422877273742L;
  private static Logger log = Logger.getLogger(LoginGoogleServlet.class
      .getName());
  
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String callbackURL = buildCallBackURL(request, AuthenticationProvider.GOOGLE);
    UserService userService = UserServiceFactory.getUserService();
    String googleLoginUrl = userService.createLoginURL(callbackURL);
    log.info("Going to Google login URL: " + googleLoginUrl);
    response.sendRedirect(googleLoginUrl);
    }
  }