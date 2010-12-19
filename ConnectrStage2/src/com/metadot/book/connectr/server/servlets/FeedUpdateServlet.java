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


import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FeedInfo;


@SuppressWarnings("serial")
public class FeedUpdateServlet extends HttpServlet {
	
  private static final int DEFAULT_BATCH = 5;
  private static final int DEFAULT_MAX = 2000;
  private static Logger logger = Logger.getLogger(FeedUpdateServlet.class.getName());
    
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
      doPost(req, resp);
      resp.setContentType("text/plain");
      resp.getWriter().println("Starting feed update job");
    }
    
  // update the most recently-requested feeds up to the given
  // max number of batches
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {


    int batch, max, batchcount= 0;

    String cursorString = req.getParameter("cursor");
    String numstring = req.getParameter("num");
    String maxstring = req.getParameter("max");
    String bc = req.getParameter("bc");

    batch = setNum(numstring);
    max = setMax(maxstring);
    logger.fine("bc is: "+ bc + " and num is: " + batch);


    if (bc != null) {
      try {
        batchcount = Integer.parseInt(bc);
      }
      catch (Exception e) {
        logger.warning(e.getMessage());
        return;
      }
      if (batchcount >= max) {
        logger.info("Reached max number of feed update batches: " + max);
        return;
      }
    }
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    Query q = null;
    try {
      q = pm.newQuery(FeedInfo.class);
      q.setOrdering("dateRequested desc");
      if (cursorString != null) {
        Cursor cursor = Cursor.fromWebSafeString(cursorString);
        Map<String, Object> extensionMap = new HashMap<String, Object>();
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      q.setRange(0, batch);
      @SuppressWarnings("unchecked")
      List<FeedInfo> results = (List<FeedInfo>) q.execute();
      logger.info("performed query");
      if (results.size() > 0) {

        batchcount++;
        Cursor cursor = JDOCursorHelper.getCursor(results);
        cursorString = cursor.toWebSafeString();
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(url("/feedupdate").param("cursor", cursorString).param("num", 
          ""+batch).param("max", ""+max).param("bc", ""+batchcount));
          
        for (FeedInfo f : results) {
          //  update the feed if needed [if it has not just been updated]
          logger.info("working on feed: "+ f.getFeedTitle());
          f.updateIfNeeded(pm);
        }

      }
      else {
        cursorString = null;
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
    }
    finally {
      q.closeAll();
      pm.close();
    }
  }

  private int setNum(String numstring) {
    int batch;
    if (numstring != null) {
      try {
        batch = Integer.parseInt(numstring);
      }
      catch (Exception e) {
        batch = DEFAULT_BATCH;
      }
    }
    else {
      batch = DEFAULT_BATCH;
    }
    return batch;
  }
  
  private int setMax(String maxstring) {
    int max;
    if (maxstring != null) {
      try {
        max = Integer.parseInt(maxstring);
      }
      catch (Exception e) {
        max = DEFAULT_MAX;
      }
    }
    else {
      max = DEFAULT_MAX;
    }
    return max;
  }


} //end class
