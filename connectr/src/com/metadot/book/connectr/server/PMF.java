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
package com.metadot.book.connectr.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.PersistenceManager;

import com.metadot.book.connectr.server.utils.cache.*;

public final class PMF {
  
    @SuppressWarnings("rawtypes")
    private static final java.lang.Class[] classes = 
      new java.lang.Class[] {com.metadot.book.connectr.server.domain.FeedInfo.class, 
        com.metadot.book.connectr.server.domain.StreamItem.class,
        com.metadot.book.connectr.server.domain.Friend.class};
    private static final PersistenceManagerFactory pmfInstance =
        JDOHelper.getPersistenceManagerFactory("transactions-optional");

    private PMF() {
    }

    public static PersistenceManagerFactory get() {
        return pmfInstance;
    }
    
    @SuppressWarnings("rawtypes")
    public static java.lang.Class[] getClasses() {
      return classes;
    }
    
    public static PersistenceManager getNonTxnPm() {
      PersistenceManager pm = pmfInstance.getPersistenceManager();
      pm.addInstanceLifecycleListener(new CacheMgmtLifecycleListener(), classes);
      return pm;
    }
    
    public static PersistenceManager getTxnPm() {
      PersistenceManager pm = pmfInstance.getPersistenceManager();
      pm.addInstanceLifecycleListener(new CacheMgmtTxnLifecycleListener(), classes);
      return pm;
    }
}
