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

import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.labs.taskqueue.Queue;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.service.FriendsService;
import com.metadot.book.connectr.server.AppMisc;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.Utils;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.shared.FriendDTO;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

/**
 * FriendsServiceImpl provides the server-side part of FriendsService, sending 
 * Friend-related data to/from the client via RPC using DTOs.
 */
@SuppressWarnings("serial")
public class FriendsServiceImpl extends RemoteServiceServlet implements
FriendsService {

  private static Logger logger = Logger.getLogger(FriendsServiceImpl.class.getName());
  private static Properties props = System.getProperties();
  private String feedids_nmspce;
  HttpSession session;


  public FriendsServiceImpl() {
    feedids_nmspce = props.getProperty("com.metadot.connectr.feedids-cache");
    AppMisc.populateDataStoreOnce();
  }

  public FriendDTO updateFriend(FriendDTO friendDTO) {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    if (friendDTO.getId() == null) { // create new
      Friend newFriend = addFriend(friendDTO);
      return newFriend.toDTO();
    }

    Friend friend = null;
    try {
        friend = pm.getObjectById(Friend.class, friendDTO.getId());

        Set<String> origurls = new HashSet<String>(friend.getUrls());
        logger.info("original Friend urls are: " + origurls);

        // delete feed information from feedids cache
        // we only need to do this if the URLs set has changed
        if (!origurls.equals(friendDTO.getUrls())) {
          CacheSupport.cacheDelete(feedids_nmspce, friendDTO.getId());
        }

        friend.updateFromDTO(friendDTO);

        if (!(origurls.isEmpty() && friendDTO.getUrls().isEmpty())) {
          // build task payload:
          Map<String, Object> hm = new HashMap<String, Object>();
          hm.put("newurls", friendDTO.getUrls());
          hm.put("origurls", origurls);
          hm.put("replace", true);
          hm.put("fid", friendDTO.getId());
          byte[] data = Utils.serialize(hm);

          Queue queue = QueueFactory.getDefaultQueue();
          queue.add(url("/updatefeedurls").payload(data,
            "application/x-java-serialized-object"));
        }
    } 
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning(e.getMessage());
      friendDTO = null;
    } 
    finally {
      pm.close();
    }
    return friendDTO;
  }
  
  private Friend addFriend(FriendDTO friendDTO) {

    PersistenceManager pm = PMF.get().getPersistenceManager();

    Friend friend = null;
    String fid = null;
    try {

        UserAccount currentUser = UserAccount.getDefaultUser(pm);
        if (currentUser == null) {
            return null;
        }
        friend = new Friend(friendDTO);
        currentUser.getFriends().add(friend);
        pm.makePersistent(currentUser);
        fid = friend.getId();
        
        if (!(friendDTO.getUrls().isEmpty())) {
          // build task payload:
          Map<String, Object> hm = new HashMap<String, Object>();
          hm.put("newurls", friendDTO.getUrls());
          hm.put("fid", fid);
          byte[] data = Utils.serialize(hm);
          Queue queue = QueueFactory.getDefaultQueue();
          queue.add(url("/updatefeedurls").payload(data,
            "application/x-java-serialized-object"));
        }
    } 
    catch (Exception e) {
      logger.warning(e.getMessage());
      friend = null;
    } 
    finally {
      pm.close();
    }
    return friend;
  }
  

  public String deleteFriend(String id)  {

    PersistenceManager pm = PMF.get().getPersistenceManager();

    CacheSupport.cacheDelete(feedids_nmspce, id);

    Set<String> urls = null;
    Friend friend = null;
    String retval = null;
    try {
        friend = pm.getObjectById(Friend.class, id);

        if (friend != null) {
          urls = friend.getDetails().getUrls();

          Map<String, Object> hm = new HashMap<String, Object>();
          hm.put("origurls", urls);
          hm.put("delete", true);
          hm.put("fid", id);
          byte[] data = Utils.serialize(hm);

          pm.deletePersistent(friend);

          Queue queue = QueueFactory.getDefaultQueue();
          queue.add(url("/updatefeedurls").payload(data,
          "application/x-java-serialized-object"));
        }
    } 
    catch (Exception e) {
      logger.warning(e.getMessage());
      friend = null;
    } 
    finally {
      pm.close();
    }
    return retval;
  }


  public ArrayList<FriendSummaryDTO> getFriendSummaries() {

    ArrayList<FriendSummaryDTO> friendsSummaries = new ArrayList<FriendSummaryDTO>();
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {

      UserAccount user = UserAccount.getDefaultUser(pm);
      if (user == null) {
        return null;
      }

      logger.info("* Getting friends for: " + user.getId());
      Set<Friend> friends = user.getFriends();
      for (Friend friend : friends) {
        friendsSummaries.add(friend.toLightWeightDTO());
      }
    } finally {
      pm.close();
    }

    return friendsSummaries;
  }

  public static TaskOptions getTaskOptions() {
      return url("/tasks/deferred");
  }
  
  public FriendDTO getFriend(String id) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    Friend detached = null;

    try {
      detached = getFriendViaCache(id, pm);
    } finally {
      pm.close();
    }
    logger.fine("in getFriend- urls are: " + detached.getUrls());
    return detached.toDTO();
  }

  private Friend getFriendViaCache(String id, PersistenceManager pm) {
    Friend dsFriend = null, detached = null;

    // check cache first
    Object o = null;
    o = CacheSupport.cacheGet(Friend.class.getName(), id);
    if (o != null && o instanceof Friend) {
      detached = (Friend) o;
    } 
    else {
      dsFriend = pm.getObjectById(Friend.class, id); 
      dsFriend.getDetails();
      detached = pm.detachCopy(dsFriend);
    }
    return detached;
  }

} // end class
