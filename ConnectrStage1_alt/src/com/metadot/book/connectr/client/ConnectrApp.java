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
package com.metadot.book.connectr.client;

import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.metadot.book.connectr.shared.MessageDTO;
import com.metadot.book.connectr.shared.UserAccountDTO;

public class ConnectrApp implements EntryPoint {



  @UiField
  HeaderPanel headerPanel;
  @UiField
  ScrollPanel mainPanel;
  @UiField
  FriendList friendList;

  RootLayoutPanel root;

  private UserAccountDTO currentUser;
  private final UserAccountServiceAsync userService = GWT
      .create(UserAccountService.class);
  private final MessagesServiceAsync messagesService = GWT
      .create(MessagesService.class);
  private static ConnectrApp singleton;
  private List<MessageDTO> messageDTOs;

  /**
   * Gets the singleton application instance.
   */
  public static ConnectrApp get() {
    return singleton;
  }

  interface ConnectrAppUiBinder extends UiBinder<DockLayoutPanel, ConnectrApp> {}

  private static final ConnectrAppUiBinder binder = GWT.create(ConnectrAppUiBinder.class);

  public void onModuleLoad() {
    singleton = this;
    DockLayoutPanel outer = binder.createAndBindUi(this);

    root = RootLayoutPanel.get();
    root.add(outer);

    login(); // try to log the user from cookie

  }

  void showEditFriend(String friendId) {
    clearMainPanel();
    mainPanel.add(new FriendEdit(friendId));
  }

  public void cancelEditFriend() {
    clearMainPanel();
    showMessages();
  }

  public void clearMainPanel() {
    mainPanel.clear();
  }

  public void showMessages() {
    mainPanel.clear();
    mainPanel.add(new MessageListView(messageDTOs));
  }

  public void clearFriendsListPanel() {
    // friendsPanel.clear();
  }

  public void showAddFriend() {
    clearMainPanel();
    mainPanel.add(new FriendEdit());
  }

  private void displayUserFriendsAndMessages() {
    this.clearFriendsListPanel();
    this.showFriendList();
    this.getMessages();
  }

  private void getMessages() {
    messagesService.getMessages(null, new AsyncCallback<List<MessageDTO>>() {
      public void onFailure(Throwable caught) {
        Window.alert("An error occurred");
      }

      public void onSuccess(List<MessageDTO> messageDTOs) {
        setAndShowMessages(messageDTOs);

      }
    });
  }

  private void setMessages(List<MessageDTO> messageDTOs) {
    this.messageDTOs = messageDTOs;
  }

  protected void setAndShowMessages(List<MessageDTO> messageDTOs) {
    setMessages(messageDTOs);
    showMessages();
  }

  private void login() {
    userService.login("email", "password",
        new AsyncCallback<UserAccountDTO>() {
          public void onFailure(Throwable caught) {
            Window.alert("An error occurred");
          }

          public void onSuccess(UserAccountDTO result) {
            currentUser = result;
            displayUserFriendsAndMessages();
          }
        });
  }

  public void showFriendList() {
    friendList.showFriends(currentUser);
  }

  public void setCurrentUser(UserAccountDTO currentUser) {
    this.currentUser = currentUser;
  }

  public UserAccountDTO getCurrentUser() {
    return currentUser;
  }

}
