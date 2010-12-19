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

import javax.jdo.PersistenceManager;

import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;

public class AppMisc {

  // sample Friend data
  private static final String[] friendsFirstNameData = new String[] { "Jean",
      "Billy", "Jacques", "Zoe", "Bella", "Napoleon", "Dona", "Daniel",
      "Amy" };

  private static final String[] friendsLastNameData = new String[] { "Voss", "Kit",
      "Martin", "Smith", "Austin", "Dubois", "Houston", "Normand", "Peterson" };

  private static final String[] friendsEmailData = new String[] {
      "jean@example.com", "billy@example.com", "jacques@example.com",
      "zoe@example.com", "bella@example.com", "napoleon@example.com",
      "dona@example.com", "daniel@example.com", "amy@example.com" };

  static void populateDataStoreOnce() {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    UserAccount defaultUser = UserAccount.getDefaultUser(pm);
    if (defaultUser != null)
      return; // already populated

    Friend friend = null;
    try {

      defaultUser = new UserAccount();
      defaultUser.setBasicInfo("bob", "default@default.com");
      // give our user some friends
      for (int i = 0; i < friendsFirstNameData.length; ++i) {
        friend = new Friend();
        friend.setBasicInfo(friendsFirstNameData[i],
            friendsLastNameData[i], friendsEmailData[i]);
        defaultUser.addFriend(friend);
      } // end for
      
      pm.makePersistent(defaultUser);

    } // end try
    catch (Exception e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }

  } 
}
