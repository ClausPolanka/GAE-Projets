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

import java.io.Serializable;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceException;
import com.google.appengine.api.memcache.MemcacheServiceFactory;


public class CacheSupport {
  
  private static MemcacheService cacheInit(String nameSpace) {
    MemcacheService memcache = MemcacheServiceFactory.getMemcacheService(nameSpace);

    return memcache;
  }

  public static Object cacheGet(String nameSpace, Object id) {
    Object r = null;
    MemcacheService memcache = cacheInit(nameSpace);
    try {
      r = memcache.get(id);
    } 
    catch (MemcacheServiceException e) {
      // nothing can be done.
    }
    return r;
  }

  public static void cacheDelete(String nameSpace, Object id) {
    MemcacheService memcache = cacheInit(nameSpace);
    try {
      memcache.delete(id);
    }
    catch (MemcacheServiceException e) {
      //...
    }
  }

  public static void cachePutExp(String nameSpace, Object id, Serializable o, int exp) {
    MemcacheService memcache = cacheInit(nameSpace);
    try {
      if (exp > 0) {
//        logger.fine("setting expiration in " + exp + " seconds for " + id);
        memcache.put(id, o, Expiration.byDeltaSeconds(exp));
      }
      else {
        memcache.put(id, o);
      }
    } 
    catch (MemcacheServiceException e) {
      // nothing can be done.
    }
  }

  public static void cachePut(String nameSpace, Object id, Serializable o) {
    MemcacheService memcache = cacheInit(nameSpace);
    try {
      memcache.put(id, o);
    }
    catch (MemcacheServiceException e) {
      // nothing can be done
    }
  }
}
