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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.metadot.book.connectr.server.LoginHelper;
import com.metadot.book.connectr.shared.SharedConstants;

public final class LoginFilter implements Filter {

  private static Logger logger = Logger.getLogger(LoginFilter.class.getName());

  @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException {

    logger.info("in LoginFilter.doFilter");
    try {
      HttpServletRequest req = (HttpServletRequest) request;

      
      if (LoginHelper.isLoggedIn(req)) {
        logger.info("User is logged in...");
        chain.doFilter(request, response);
      } else {
        logger.warning("User is not logged in...");
        if (request.getContentType().contains("x-gwt-rpc")){
          // GWT requests
          HttpServletResponse resp = (HttpServletResponse) response;
          resp.setHeader("content-type", request.getContentType());
          resp.getWriter().print(SharedConstants.LOGGED_OUT);
        } else {
          HttpServletResponse resp = (HttpServletResponse) response;
          resp.sendRedirect("/");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  @Override public void destroy() {
    //  Auto-generated method stub

  }

  @Override public void init(FilterConfig arg0) throws ServletException {
    //  Auto-generated method stub

  }

} // end class
