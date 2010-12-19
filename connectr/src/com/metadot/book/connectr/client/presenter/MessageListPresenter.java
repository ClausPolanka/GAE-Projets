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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.event.ContentAvailableEvent;
import com.metadot.book.connectr.client.event.ContentAvailableEventHandler;
import com.metadot.book.connectr.client.event.FriendListChangedEvent;
import com.metadot.book.connectr.client.event.FriendListChangedEventHandler;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;
import com.metadot.book.connectr.shared.FriendSummaryDTO;
import com.metadot.book.connectr.shared.StreamItemSummaryDTO;

public class MessageListPresenter implements Presenter {

  private static final int NUM_MSGS = 50;

  public interface Display {

    void setData(List<StreamItemSummaryDTO> data);

    void showLoadingMessage();

    HasClickHandlers getRefreshButton();

    Widget asWidget();
  }

  private final MessagesServiceAsync rpcService;
  private final SimpleEventBus eventBus;
  private final Display display;
  protected List<FriendSummaryDTO> friendSummaries;
  protected List<StreamItemSummaryDTO> messages;
  protected List<FriendSummaryDTO> allFriends;

  public MessageListPresenter(MessagesServiceAsync rpcService, SimpleEventBus eventBus, Display view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.display = view;

    bind();

  }

  public void bind() {

    eventBus.addHandler(ContentAvailableEvent.TYPE, new ContentAvailableEventHandler() {
      @Override public void onContentAvailable(ContentAvailableEvent event) {
        GWT.log("ContentAvailableEvent received");
        getMessageList();
      }
    });

    this.display.getRefreshButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        GWT.log("Refresh button clicked");
        getMessageList();
      }

    });
    eventBus.addHandler(FriendListChangedEvent.TYPE, new FriendListChangedEventHandler() {
      @Override public void onFriendListChanged(FriendListChangedEvent event) {
        GWT.log("MessageListPresenter: FriendListChangedEvent received");
        friendSummaries = event.getFriendSummaries();
        allFriends = event.getAllFriends();
        getMessageList();
      }
    });

  }

  public void go(final HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
    display.setData(messages);
  }

  private void getMessageList() {

    GWT.log("Getting message list");
    new RPCCall<List<StreamItemSummaryDTO>>() {
      @Override protected void callService(AsyncCallback<List<StreamItemSummaryDTO>> cb) {
        rpcService.getNLastMessages(friendIdList(), NUM_MSGS, cb);
      }

      @Override public void onSuccess(List<StreamItemSummaryDTO> messageList) {
        messages = messageList;
        display.setData(messages);
      }

      @Override public void onFailure(Throwable caught) {
        Window.alert("Error: " + caught.getMessage());
      }
    }.retry(3);
  }

  /**
   * @return a list of friend ids
   */
  private Set<String> friendIdList() {
    Set<String> list = new HashSet<String>();

    if (friendSummaries.size() == 0) {
      GWT.log("No friend selected");
      return list; // empty list
    }
    
    if (friendSummaries.size() == allFriends.size()) {
      GWT.log("All friends selected: " + friendSummaries.size());
      return null;  // returning null signals to the server that all are selected
    }

    for (Iterator<FriendSummaryDTO> i = friendSummaries.iterator(); i.hasNext();) {
      String id = ((FriendSummaryDTO) i.next()).getId();
      list.add(id);
      GWT.log("Friend id selected: " + id);
    }

    return list;
  }
}
