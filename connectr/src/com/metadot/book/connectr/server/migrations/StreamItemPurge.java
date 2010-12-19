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
package com.metadot.book.connectr.server.migrations;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import com.google.appengine.api.datastore.Cursor;
import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.StreamItem;
import com.metadot.book.connectr.server.migrations.Migration;


/**
 * StreamItemPurge is a migration to remove from the Datastore all 
 * StreamItem entities older than a given date.
 */
public class StreamItemPurge implements Migration  {

  @SuppressWarnings("unchecked")
  public Map<String, String> migrate_down(String cursorString, int range, Map<String,String> params) {

    Query q = null;
    Date prior = null;
    Long priorl = null;
    List<StreamItem> results = null;
    int hoursOld = 168; // default number of hours older than which to purge
    Long ts = (new Date()).getTime();
    
    if (params != null) {
      try {
        String hstring = (String) params.get("hours");
        hoursOld = Integer.parseInt(hstring);
      }
      catch (Exception e1) {
        // e1.printStackTrace();
      }
      try {
        String tsstring = (String) params.get("ts");
        ts = Long.parseLong(tsstring);
      }
      catch (Exception e2) {
        // just catch the parse exception.
        // e2.printStackTrace();
      }
      priorl = ts - (1000 * (hoursOld * 3600));
      prior = new Date(priorl);

    }
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      if (prior != null) {
        q = pm.newQuery(StreamItem.class, "date <= :d1");        
      }
      else {
        q = pm.newQuery(StreamItem.class);
      }
      if (cursorString != null) {
        Cursor cursor = Cursor.fromWebSafeString(cursorString);
        Map<String, Object> extensionMap = new HashMap<String, Object>();
        extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
        q.setExtensions(extensionMap);
      }
      q.setRange(0, range);
      if (prior == null) {
        results = (List<StreamItem>) q.execute();
      }
      else {
        results = (List<StreamItem>) q.execute(prior);
      }
      if (results.iterator().hasNext()) {
        for (StreamItem streamItem : results) {
          pm.deletePersistent(streamItem);
        }
        Cursor cursor = JDOCursorHelper.getCursor(results);
        cursorString = cursor.toWebSafeString();
      }
      else {
        // no results
        cursorString = null;
      }
    }
    finally {
      q.closeAll();
      pm.close();
    }
    Map<String,String> res = new HashMap<String,String>();
    res.put("cursor", cursorString);
    res.put("hours", ""+hoursOld);
    res.put("ts", ""+ ts);
    return res;
  }

  // a no-op for this class
  public Map<String, String> migrate_up(String cs, int range, Map<String,String> params) {
    return null;
  }

}