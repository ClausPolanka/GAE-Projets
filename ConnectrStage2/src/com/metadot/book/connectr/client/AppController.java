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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.History;
import com.metadot.book.connectr.client.event.FriendAddEvent;
import com.metadot.book.connectr.client.event.FriendAddEventHandler;
import com.metadot.book.connectr.client.event.FriendEditCancelledEvent;
import com.metadot.book.connectr.client.event.FriendEditCancelledEventHandler;
import com.metadot.book.connectr.client.event.FriendEditEvent;
import com.metadot.book.connectr.client.event.FriendEditEventHandler;
import com.metadot.book.connectr.client.event.FriendUpdatedEvent;
import com.metadot.book.connectr.client.event.FriendUpdatedEventHandler;
import com.metadot.book.connectr.client.event.ShowFriendPopupEvent;
import com.metadot.book.connectr.client.event.ShowFriendPopupEventHandler;
import com.metadot.book.connectr.client.presenter.FriendEditPresenter;
import com.metadot.book.connectr.client.presenter.FriendPopupPresenter;
import com.metadot.book.connectr.client.presenter.MessageListPresenter;
import com.metadot.book.connectr.client.presenter.Presenter;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.client.service.MessagesService;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;
import com.metadot.book.connectr.client.view.FriendEditView;
import com.metadot.book.connectr.client.view.FriendPopupView;
import com.metadot.book.connectr.client.view.MessageListView;

public class AppController implements ValueChangeHandler<String> {
  private final SimpleEventBus eventBus;
  private final FriendsServiceAsync friendService;
  private final MessagesServiceAsync messagesService = GWT.create(MessagesService.class);
  private String currentFriendId;

  public AppController(FriendsServiceAsync rpcService, SimpleEventBus eventBus) {
    this.eventBus = eventBus;
    this.friendService = rpcService;
    bind();
  }

  private void bind() {
    History.addValueChangeHandler(this);

    eventBus.addHandler(FriendAddEvent.TYPE, new FriendAddEventHandler() {
      public void onAddFriend(FriendAddEvent event) {
        doAddNewFriend();
      }
    });

    eventBus.addHandler(ShowFriendPopupEvent.TYPE, new ShowFriendPopupEventHandler() {
      public void onShowFriendPopup(ShowFriendPopupEvent event) {
        FriendPopupPresenter friendPopupPresenter = new FriendPopupPresenter(friendService, eventBus, new FriendPopupView(event.getFriend()
            .getDisplayName(), event.getClickPoint()), event.getFriend());
        friendPopupPresenter.go();
      }
    });

    eventBus.addHandler(FriendEditEvent.TYPE, new FriendEditEventHandler() {
      public void onEditFriend(FriendEditEvent event) {
        GWT.log("Friend edit event received");
        doEditFriend(event.getId());
      }
    });

    eventBus.addHandler(FriendEditCancelledEvent.TYPE, new FriendEditCancelledEventHandler() {
      public void onEditFriendCancelled(FriendEditCancelledEvent event) {
        doEditFriendCancelled();
      }
    });

    eventBus.addHandler(FriendUpdatedEvent.TYPE, new FriendUpdatedEventHandler() {
      public void onFriendUpdated(FriendUpdatedEvent event) {
        doFriendUpdated();
      }
    });
    
  }

  private void doAddNewFriend() {
    History.newItem("add");
  }

  private void doEditFriend(String id) {
    currentFriendId = id;
    History.newItem("edit");
  }

  private void doEditFriendCancelled() {
    History.newItem("list");
  }

  private void doFriendUpdated() {
    History.newItem("list");
  }


  public void go() {

    if ("".equals(History.getToken())) {
      History.newItem("list");
    } else {
      History.fireCurrentHistoryState();
    }
  }

  public void onValueChange(ValueChangeEvent<String> event) {
    String token = event.getValue();

    if (token != null) {
      Presenter presenter = null;

      if (token.equals("list")) {
        presenter = new MessageListPresenter(messagesService, eventBus, new MessageListView());
        presenter.go(ConnectrApp.get().getMainPanel());
        return;

      } else if (token.equals("add")) {
        presenter = new FriendEditPresenter(friendService, eventBus, new FriendEditView());
        presenter.go(ConnectrApp.get().getMainPanel());
        return;

      } 

      else if (token.equals("edit")) {
        presenter = new FriendEditPresenter(friendService, eventBus, new FriendEditView(), currentFriendId);
        presenter.go(ConnectrApp.get().getMainPanel());

        return;
      }
    }

  }
}
