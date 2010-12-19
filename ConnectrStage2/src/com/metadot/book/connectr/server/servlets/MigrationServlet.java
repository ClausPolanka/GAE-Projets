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

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.datastore.DatastoreTimeoutException;
import com.google.appengine.api.labs.taskqueue.TaskOptions;


import com.metadot.book.connectr.server.migrations.Migration;

@SuppressWarnings("serial")
public class MigrationServlet extends HttpServlet {
  
  protected static final int DEFAULT_BATCH = 5;
  protected static Logger logger = Logger.getLogger(MigrationServlet.class.getName());
  
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws IOException {
      resp.setContentType("text/plain");
      resp.getWriter().println("Starting migration");
      doPost(req, resp);
    }

  public void doPost(HttpServletRequest req, HttpServletResponse resp)
  throws IOException {

    String migration = req.getParameter("migration");
    String direction = req.getParameter("dir");
    String cursor = req.getParameter("cursor");
    String rangestring = req.getParameter("num");

    int range;
    Map<String,String> params = new HashMap<String,String>();
    Map<String,String> res = null;

    if (migration == null || migration.equals("")) {
      resp.setContentType("text/plain");
      resp.getWriter().println("error: no migration class provided");
      return;
    }
    if (direction == null || !direction.equalsIgnoreCase("down")) {
      direction = "up";
    }
    range = setBatch(rangestring);
    
    Enumeration<?> en = req.getParameterNames();
    while (en.hasMoreElements()) {
         
         String paramName = (String) en.nextElement();
         if (!paramName.equals("cursor") && !paramName.equals("num") && !paramName.equals("dir")
           && !paramName.equals("migration")) {
         // logger.fine("adding " + paramName + " to params");
         params.put(paramName, req.getParameter(paramName));
       }
     }
    

    try {
      // make an instance of the passed-in migration class.
      // it should be a string, e.g. "com.metadot.connectr.server.migrations.FriendMigration"
      Class<?> [] classParm = null;
      Object [] objectParm = null;
      Class<?> cl = Class.forName(migration);
      java.lang.reflect.Constructor<?> co = cl.getConstructor(classParm);
      // there will be a cast exception here if the object does not implement Migration
      Migration mg = (Migration) co.newInstance(objectParm);

      // call the 'migrate_up' or down method as appropriate, which every 
      // class of type Migration must support.
      if (direction.equals("down")) {
        logger.info("migrating down: " + mg);
        res = mg.migrate_down(cursor, range, params);
        cursor = res.get("cursor");
      }
      else {
        logger.info("migrating up: " + mg);
        res = mg.migrate_up(cursor, range, params);
        cursor = res.get("cursor");
        // logger.info("finished migrating up");
      }
      if (cursor != null) {
        Queue queue = QueueFactory.getDefaultQueue();
        TaskOptions topt = TaskOptions.Builder.url("/migration");
        for (String rkey : res.keySet()) {
          if (!rkey.equals("cursor")) {
            topt = topt.param(rkey, ""+ res.get(rkey));
          }
        }
        queue.add(topt.param("cursor", cursor).param("num", ""+range).
          param("dir", direction).param("migration", migration));
      }
    } 
    catch (ClassNotFoundException e) {
      logger.warning(e.getMessage());
      resp.setContentType("text/plain");
      resp.getWriter().println("error: got a 'class not found' exception for " + migration);
    }
    catch (DatastoreTimeoutException e) {
      throw e;
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
      resp.setContentType("text/plain");
      resp.getWriter().println(e.getMessage());
    }
  }

  protected int setBatch(String numstring) {
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

} // end class
