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
package com.metadot.book.connectr.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.metadot.book.connectr.shared.FriendDTO;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

public interface FriendsServiceAsync {

  public void deleteFriend(String id, AsyncCallback<String> callback);

  public void getFriendSummaries(AsyncCallback<ArrayList<FriendSummaryDTO>> callback);
  public void getFriend(String id, AsyncCallback<FriendDTO> callback);
  public void updateFriend(FriendDTO friend, AsyncCallback<FriendDTO> callback);
}
