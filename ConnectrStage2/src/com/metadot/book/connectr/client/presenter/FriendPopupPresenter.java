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
package com.metadot.book.connectr.client.presenter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.event.FriendDeletedEvent;
import com.metadot.book.connectr.client.event.FriendEditEvent;
import com.metadot.book.connectr.client.helper.ClickPoint;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.client.view.FriendPopupView;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

public class FriendPopupPresenter implements Presenter {

  public interface Display {
    HasClickHandlers getEditButton();
    HasClickHandlers getDeleteButton();
    HasText getFriendNameLabel();
    void hide();
    void setName(String displayName);
    void setNameAndShow(String displayName, ClickPoint location);
    Widget asWidget();
  }

  private FriendSummaryDTO friend;
  FriendPopupView popup;

  private final FriendsServiceAsync friendsService;
  private final SimpleEventBus eventBus;
  private Display display;

  public FriendPopupPresenter(FriendsServiceAsync friendsService, SimpleEventBus eventBus, Display display, FriendSummaryDTO friend) {
    this.friendsService = friendsService;
    this.eventBus = eventBus;
    this.display = display;
    this.friend = friend;
    bind();
  }

  public void bind() {
    this.display.getEditButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        display.hide();
        eventBus.fireEvent(new FriendEditEvent(friend.getId()));
      }
    });

    this.display.getDeleteButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
         display.hide();
        if (Window.confirm("Are you sure?")) {
          deleteFriend(friend.getId());
        }
      }
    });

  }

  private void deleteFriend(final String id) {
    
    new RPCCall<String>() {
      @Override
      protected void callService(AsyncCallback<String> cb) {
          friendsService.deleteFriend(id, cb);
        }

      @Override
      public void onSuccess(String idDeleted) {
        eventBus.fireEvent(new FriendDeletedEvent());
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("An error occurred: " + caught.toString());
      }

    }.retry(3);

  }

  public void go() {
  }

  @Override
  public void go(HasWidgets container) {
    // Auto-generated method stub
    
  }

}
