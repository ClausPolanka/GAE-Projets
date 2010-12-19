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

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.event.FriendEditCancelledEvent;
import com.metadot.book.connectr.client.event.FriendUpdatedEvent;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.shared.FriendDTO;

public class FriendEditPresenter implements Presenter {
  public interface Display {
    HasClickHandlers getAddUrlButton();
    HasClickHandlers getCancelButton();
    String getDeletedUrl(ClickEvent event);
    HasValue<String> getEmailAddress();
    HasValue<String> getFirstName();
    HasValue<String> getLastName();
    HasClickHandlers getList();
    HasClickHandlers getSaveButton();
    HasValue<String> getUrl();
    void setData(Set<String> urls);
    Widget asWidget();
  }


  private FriendDTO friend;
  private final FriendsServiceAsync rpcService;
  private final SimpleEventBus eventBus;
  private final Display display;

  public FriendEditPresenter(FriendsServiceAsync rpcService, SimpleEventBus eventBus, Display display) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.display = display;
    this.friend = new FriendDTO();
    bind();
  }

  public FriendEditPresenter(final FriendsServiceAsync rpcService, SimpleEventBus eventBus, Display display, final String id) {
    this(rpcService, eventBus, display);

    new RPCCall<FriendDTO>() {
      @Override
      protected void callService(AsyncCallback<FriendDTO> cb) {
        rpcService.getFriend(id, cb);
      }

      @Override
      public void onSuccess(FriendDTO result) {
        friend = result;
        FriendEditPresenter.this.display.getFirstName().setValue(friend.getFirstName());
        FriendEditPresenter.this.display.getLastName().setValue(friend.getLastName());
        FriendEditPresenter.this.display.getEmailAddress().setValue(friend.getEmailAddress());
        FriendEditPresenter.this.display.setData(friend.getUrls());
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error retrieving friend");
      }
    }.retry(3);

  }

  public void bind() {
    this.display.getSaveButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        doSave();
      }
    });

    this.display.getAddUrlButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        addURL();
      }

    });

    this.display.getCancelButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new FriendEditCancelledEvent());
        GWT.log("FriendEditCancelledEvent fired");
      }
    });
    
    if (display.getList() != null)
      display.getList().addClickHandler(new ClickHandler() {
        public void onClick(ClickEvent event) {
          String url = display.getDeletedUrl(event);

          if (url != null) {
            deleteUrl(url);
          }
        }

        private void deleteUrl(String url) {
          friend.getUrls().remove(url);
          display.setData(friend.getUrls());
        }
      });

  }

  public void go(final HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
  }

  private void addURL() {
    String url = display.getUrl().getValue().trim();
    if (url.equals(""))
      return;
    
    friend.addUrl(url);
    display.setData(friend.getUrls());
  }
  
  private void doSave() {
    friend.setFirstName(display.getFirstName().getValue().trim());
    friend.setLastName(display.getLastName().getValue().trim());
    friend.setEmailAddress(display.getEmailAddress().getValue().trim());

    new RPCCall<FriendDTO>() {
      @Override
      protected void callService(AsyncCallback<FriendDTO> cb) {
        rpcService.updateFriend(friend, cb);
      }

      @Override
      public void onSuccess(FriendDTO result) {
        eventBus.fireEvent(new FriendUpdatedEvent(result));
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error retrieving friend");
      }
    }.retry(3);
  }

}
