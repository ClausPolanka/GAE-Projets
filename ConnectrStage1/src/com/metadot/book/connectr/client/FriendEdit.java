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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.shared.FriendDTO;

/**
 * Demonstrate UIbinder Styles Separating UI and actions (tag del for example)
 * Leaving w/o saving warning
 * 
 * @author Daniel Guermeur
 * 
 */
public class FriendEdit extends Composite {

  private static FriendEditUiBinder uiBinder = GWT
      .create(FriendEditUiBinder.class);

  interface FriendEditUiBinder extends UiBinder<Widget, FriendEdit> {
  }

  @UiField
  TextBox firstNameField, lastNameField, emailField;
  @UiField
  Button cancelButton, saveButton;

  private FriendDTO currentFriend = new FriendDTO();

  private final FriendsServiceAsync friendsService = GWT
      .create(FriendsService.class);

  public FriendEdit() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public FriendEdit(FriendDTO friend) {
    this();
    // Can access @UiField after calling createAndBindUi
    if (null != friend) { // editing an existing friend
      this.currentFriend = friend;
      displayFriend();
    }

    firstNameField.setFocus(true);
  }

  /**
   * Populate form with fiend values
   */
  private void displayFriend() {
    firstNameField.setText(currentFriend.getFirstName());
    lastNameField.setText(currentFriend.getLastName());
    emailField.setText(currentFriend.getEmailAddress());
    // Move cursor focus to the first input box.
    firstNameField.setFocus(true);
  }

  public FriendEdit(String friendId) {
    this();
    getFriendInfo(friendId);
  }

  private void getFriendInfo(String friendId) {
    friendsService.getFriend(friendId, new AsyncCallback<FriendDTO>() {
      public void onFailure(Throwable caught) {
        Window.alert("An error occurred");
      }

      public void onSuccess(FriendDTO result) {
        currentFriend = result;
        displayFriend();
      }
    });
  }


  @UiHandler("cancelButton")
  void onCancelButtonClicked(ClickEvent e) {
    ConnectrApp.get().cancelEditFriend();
  }


  @UiHandler("saveButton")
  void onSaveButtonClicked(ClickEvent e) {
    getFormValues();
    updateFriend();
  }

  private void getFormValues() {
    currentFriend.setEmailAddress(emailField.getText());
    currentFriend.setFirstName(firstNameField.getText());
    currentFriend.setLastName(lastNameField.getText());
  }

  private void updateFriend() {
    friendsService.updateFriend(currentFriend,
        new AsyncCallback<FriendDTO>() {
          public void onFailure(Throwable caught) {
            Window.alert("An error occurred");
          }

          public void onSuccess(FriendDTO result) {
            // Window.alert("Updated");
            ConnectrApp.get().showFriendList();
            ConnectrApp.get().showMessages();
          }
        });
  }


  @Override
  protected void onLoad() {
    super.onLoad();
    firstNameField.setFocus(true);
  }

}
