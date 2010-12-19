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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.Resources.GlobalResources;
import com.metadot.book.connectr.shared.FriendSummaryDTO;
import com.metadot.book.connectr.shared.UserAccountDTO;

/**
 * @author supercobra
 * 
 */
public class FriendList extends Composite {

  @UiField
  FlexTable friendsTable;

  private final FriendsServiceAsync friendsService = GWT
      .create(FriendsService.class);

  private static FriendListUiBinder uiBinder = GWT
      .create(FriendListUiBinder.class);

  interface FriendListUiBinder extends UiBinder<Widget, FriendList> {}

  /**
   * Constructor
   */
  public FriendList() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  /**
   * Constructor
   * 
   * @param user
   *            the current logged in user
   */
  public FriendList(UserAccountDTO user) {
    this();
    getFriendsSummaryList();
  }

  public void showFriends(UserAccountDTO user) {
    getFriendsSummaryList();
  }

  public void getFriendsSummaryList() {
    friendsService
        .getFriendSummaries(new AsyncCallback<ArrayList<FriendSummaryDTO>>() {
          public void onFailure(Throwable caught) {
            Window.alert("An error occurred");
          }

          public void onSuccess(ArrayList<FriendSummaryDTO> result) {
            displayFriends(result);
          }
        });
  }

  private void displayFriends(List<FriendSummaryDTO> friends) {

    // first sort the friends list alphabetically on name
    sortFriendSummaryDTO(friends);
    friendsTable.clear();
    int i = 0;
    for (final FriendSummaryDTO friend : friends) {
      CheckBox checkBoxName = new CheckBox(truncateLongName(friend
          .getDisplayName()));

      final Image propertyImg = new Image(GlobalResources.RESOURCE
          .propertyButton());
      propertyImg.setStyleName("pointer");
      propertyImg.addClickHandler(new ShowFriendPopupPanel(friend,
          propertyImg));

      friendsTable.setWidget(i, 0, checkBoxName);
      friendsTable.setWidget(i, 1, propertyImg);
      friendsTable.getCellFormatter().addStyleName(i, 0,
          "friendNameInList");
      i++;
    }
  }
  
  private void sortFriendSummaryDTO(List<FriendSummaryDTO> friendSummaries) {
    for (int i = 0; i < friendSummaries.size(); ++i) {
      for (int j = 0; j < friendSummaries.size() - 1; ++j) {
        if (friendSummaries.get(j).getDisplayName().compareToIgnoreCase(friendSummaries.get(j + 1).getDisplayName()) >= 0) {
          FriendSummaryDTO tmp = friendSummaries.get(j);
          friendSummaries.set(j, friendSummaries.get(j + 1));
          friendSummaries.set(j + 1, tmp);
        }
      }
    }
  }

  /**
   * Shorten long displayName to something smaller if name is too long.
   * 
   * @param displayName
   * @return a possibly truncated displayNames
   */
  private String truncateLongName(String displayName) {
    final int MAX = 18; // truncate string if it is longer than MAX
    final String SUFFIX = "...";

    if (displayName.length() < MAX)
      return displayName;

    String shorten = displayName.substring(0, MAX - SUFFIX.length())
        + SUFFIX;

    return shorten;
  }

  @UiHandler("addNew")
  void onClick(ClickEvent e) {
    ConnectrApp.get().showAddFriend();
  }

  /**
   * A simple popup that displays friend's info w/ edit / delete buttons.
   */
  static class FriendPopup extends PopupPanel {
    @UiTemplate("FriendPopup.ui.xml")
    interface Binder extends UiBinder<Widget, FriendPopup> {
    }

    private static final Binder binder = GWT.create(Binder.class);

    @UiField
    Element nameDiv;
    FriendSummaryDTO friend;

    public FriendPopup(FriendSummaryDTO friend) {
      // The popup's constructor's argument is a boolean specifying that
      // it auto-close itself when the user clicks outside of it.
      super(true);
      this.friend = friend;
      add(binder.createAndBindUi(this));
      nameDiv.setInnerText(friend.getDisplayName());

    }

    private final static FriendsServiceAsync friendsService = GWT
        .create(FriendsService.class);

    private static void deleteFriend(FriendSummaryDTO friend) {
      friendsService.deleteFriend(friend.getId(),
          new AsyncCallback<Boolean>() {
            public void onFailure(Throwable caught) {
              Window.alert("An error occurred");
            }

            public void onSuccess(Boolean result) {
              ConnectrApp.get().showFriendList();
            }
          });

    }

    @UiHandler("delete")
    void onDeleteClick(ClickEvent e) {
      if (Window.confirm("Are you sure?")) {
        deleteFriend(friend);
        this.hide();
      }
    }

    @UiHandler("edit")
    void onEditClick(ClickEvent e) {
      ConnectrApp.get().showEditFriend(friend.getId());
      this.hide();
    }
  }

  /**
   * 
   * ShowPopupPanel
   * 
   */
  final class ShowFriendPopupPanel implements ClickHandler {
    private final FriendSummaryDTO friend;
    private final Image propertyImg;

    private ShowFriendPopupPanel(FriendSummaryDTO friend, Image propertyImg) {
      this.friend = friend;
      this.propertyImg = propertyImg;
    }

    @Override
    public void onClick(ClickEvent event) {
      FriendPopup popup = new FriendPopup(friend);
      int left = propertyImg.getAbsoluteLeft() + 14;
      int top = propertyImg.getAbsoluteTop() + 14;
      popup.setPopupPosition(left, top);
      popup.show();
    }
  }

}
