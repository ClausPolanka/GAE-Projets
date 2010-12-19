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
import java.util.Enumeration;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * This demo class shows a simple filter at work.  It logs information 
 * about the request that it is filtering.
 * This filter is not required by Connectr.
 */
public final class LogFilter implements Filter {

  private static Logger logger = Logger.getLogger(LogFilter.class.getName());


  @Override
  public void doFilter(ServletRequest request,
      ServletResponse response, FilterChain chain) 
  throws IOException, ServletException {

    try {
      Enumeration<?> pnames = request.getParameterNames();
      String p;
      String pval = null;
      String reqUrl = ((HttpServletRequest)(request)).getRequestURL().toString();
      logger.info("request URL: " + reqUrl);

      while(pnames.hasMoreElements()) {
        p = (String)pnames.nextElement();

        pval = request.getParameter(p);
        logger.info("request parameter " + p + " has value " + pval);
      }
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    chain.doFilter(request, response);

  }

  @Override
  public void destroy() {
    // Auto-generated method stub
  }


  @Override
  public void init(FilterConfig arg0) throws ServletException {
    // Auto-generated method stub

  }

} // end class
