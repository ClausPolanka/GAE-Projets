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

import java.util.logging.Logger;

import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.Utils;

public class AppLib {
  private static final Logger log = Logger.getLogger(Utils.class.getName());

  // sample Friend data
  private static final String[] friendsFirstNameData = new String[] { "Jean", "Billy", "Jacques", "Zoe", "Bella", 
    "Napoleon", "Dona", "Daniel", "Amy" };

  private static final String[] friendsLastNameData = new String[] { "Voss", "Kit", "Martin", "Smith", "Austin", 
    "Dubois", "Houston", "Normand", "Petersen" };

  private static final String[] friendsEmailData = new String[] { "jean@example.com", "billy@example.com", "jacques@example.com", "zoe@example.com",
      "bella@example.com", "napoleon@example.com", "dona@example.com", "daniel@example.com", "amy@example.com" };

  public static final String INFONOTFOUND = "<h1>Error #AF31-G</h1><p>Login service credentials missing in appengine-web.xml." 
     + " Please update this file as indicated with OAuth key information and restart the application.</p>"
     +"<p>If you just want to try the app at once, choose Google authentication.</p>";
  
  /**
   * add the sample Friends
   */
  public static void addFriends(UserAccount user) {
    Friend friend;
    for (int i = 0; i < friendsFirstNameData.length; ++i) {
      friend = new Friend();
      friend.setBasicInfo(friendsFirstNameData[i], friendsLastNameData[i], friendsEmailData[i]);
      log.info("Adding friend: " + friend.getFirstName());
      user.addFriend(friend);
    } // end for
  }
}
