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
package com.metadot.book.connectr.server.utils.cache;

import javax.jdo.listener.DeleteLifecycleListener;
import javax.jdo.listener.InstanceLifecycleEvent;
import javax.jdo.listener.LoadLifecycleListener;
import javax.jdo.listener.StoreLifecycleListener;


public class CacheMgmtLifecycleListener implements 
  DeleteLifecycleListener, LoadLifecycleListener, 
  StoreLifecycleListener {

  public void preDelete(InstanceLifecycleEvent event)    {
    Object o = event.getSource();
    if (o instanceof Cacheable) {
      Cacheable f = (Cacheable) o;
      f.removeFromCache();
    }
  }

  public void postDelete(InstanceLifecycleEvent event)    {
  }

  public void postLoad(InstanceLifecycleEvent event)   {
    addToCache(event);
  }

  public void preStore(InstanceLifecycleEvent event)    {
  }

  public void postStore(InstanceLifecycleEvent event)    {
    addToCache(event);
  }
  
  private void addToCache(InstanceLifecycleEvent event) {
    Object o = event.getSource();
    if (o instanceof Cacheable) {
      Cacheable f = (Cacheable) o;
      f.addToCache();
    }
  }

}