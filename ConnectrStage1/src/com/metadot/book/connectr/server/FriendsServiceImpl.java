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

import java.util.ArrayList;
import java.util.Set;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.FriendsService;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.shared.FriendDTO;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

/**
 * the FriendsService communicates Friend data via RPC between client and server.
 */
@SuppressWarnings("serial")
public class FriendsServiceImpl extends RemoteServiceServlet implements
    FriendsService {

  public FriendsServiceImpl() {
    AppMisc.populateDataStoreOnce();
  }

  public FriendDTO updateFriend(FriendDTO friendDTO) {
    if (friendDTO.getId() == null){ // create new
      Friend newFriend = addFriend(friendDTO);
      return newFriend.toDTO();
    }

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Friend friend = null;
    try {
      friend = pm.getObjectById(Friend.class, friendDTO.getId());
      friend.updateFromDTO(friendDTO);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return friendDTO;
  }

  public Boolean deleteFriend(String id) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      Friend friend = pm.getObjectById(Friend.class, id);
      if (friend != null) {
        pm.deletePersistent(pm.getObjectById(Friend.class, id));
      }
    } finally {
      pm.close();
    }
    return true;
  }

  // create new Friend object in Datastore
  private Friend addFriend(FriendDTO friendDTO) {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Friend friend = null;
    try {
      // for this version of the app, just get hardwired 'default' user
      UserAccount currentUser = UserAccount.getDefaultUser(); // detached object
      currentUser = pm.makePersistent(currentUser); // attach
      friend = new Friend(friendDTO);
      currentUser.getFriends().add(friend);
    } finally {
      pm.close();
    }
    return friend;
  }
  

  public ArrayList<FriendSummaryDTO> getFriendSummaries() {

    ArrayList<FriendSummaryDTO> friendsSummaries = new ArrayList<FriendSummaryDTO>();
    PersistenceManager pm = PMF.get().getPersistenceManager();

    try {
      UserAccount user = UserAccount.getDefaultUser(pm);
      Set<Friend> friends = user.getFriends();
      for (Friend friend : friends) {
        friendsSummaries.add(friend.toLightWeightDTO());
      }
    } finally {
      pm.close();
    }

    return friendsSummaries;
  }

  /**
   * 
   */
  public FriendDTO getFriend(String id) {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    Friend dsFriend, detached;

    try {
      dsFriend = pm.getObjectById(Friend.class, id);
      dsFriend.getDetails();
      detached = pm.detachCopy(dsFriend);
    } finally {
      pm.close();
    }

    return detached.toDTO();
  }

} // end class
