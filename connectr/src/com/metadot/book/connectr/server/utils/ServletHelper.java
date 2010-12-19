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
package com.metadot.book.connectr.server.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.utils.SystemProperty;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.metadot.book.connectr.shared.exception.NotLoggedInException;

public class ServletHelper extends RemoteServiceServlet {

  private static final long serialVersionUID = -6362867182441202099L;

  public HttpSession getSessionIfLoggedOrThrowException() throws NotLoggedInException {
    HttpSession session = null;
    if (session == null)
      throw new NotLoggedInException();

    @SuppressWarnings("unused")
    Object userIdS = session.getAttribute("UserId");

    if (userIdS == null)
      throw new NotLoggedInException();

    Long userid = new Long((String) session.getAttribute("UserId"));

    if (userid == null || userid == -1)
      throw new NotLoggedInException();

    return session;
  }

  /**
   * @return true if the app is in dev mode, false otherwise.
   * @param request
   */
  public static boolean isDevelopment(HttpServletRequest request) {
    return SystemProperty.environment.value() != SystemProperty.Environment.Value.Production;
  }
}
