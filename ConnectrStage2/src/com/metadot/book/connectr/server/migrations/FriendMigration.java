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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.JDOHelper;
import com.google.appengine.api.datastore.Cursor;
import org.datanucleus.store.appengine.query.JDOCursorHelper;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FriendDetails;
import com.metadot.book.connectr.server.migrations.Migration;

/**
 * A migration to initialize a new 'urls' field of Friend.
 * This particular migration is not strictly necessary, as GAE will 
 * in fact handle the uninitialized field gracefully.  However, it is
 * included for purposes of example
 */
public class FriendMigration implements Migration  {

  public Map<String, String> migrate_up(String cursorString, int range, Map<String,String> params) {

    Query q = null;

    PersistenceManager pm = PMF.get().getPersistenceManager();

      try {
        q = pm.newQuery(FriendDetails.class);
        if (cursorString != null) {
          Cursor cursor = Cursor.fromWebSafeString(cursorString);
          Map<String, Object> extensionMap = new HashMap<String, Object>();
          extensionMap.put(JDOCursorHelper.CURSOR_EXTENSION, cursor);
          q.setExtensions(extensionMap);
        }
        q.setRange(0, range);
        @SuppressWarnings("unchecked")
        List<FriendDetails> results = (List<FriendDetails>) q.execute();
        if (results.size() > 0) {
          for (FriendDetails friendd : results) {
            // initialize the new field if necessary
            Set<String> urls = friendd.getUrls();
            if (urls == null || urls.isEmpty()) {
              if (urls == null) {
                friendd.setUrls(new HashSet<String>());
              }
              JDOHelper.makeDirty(friendd, "urls");
            }
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
    return res;
  }
  
  // a no-op for this class
  public Map<String, String> migrate_down(String cs, int range, Map<String,String> params) {
    return null;
  }

}
