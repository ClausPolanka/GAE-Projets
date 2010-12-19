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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.event.FriendAddEvent;
import com.metadot.book.connectr.client.event.FriendDeletedEvent;
import com.metadot.book.connectr.client.event.FriendDeletedEventHandler;
import com.metadot.book.connectr.client.event.FriendListChangedEvent;
import com.metadot.book.connectr.client.event.FriendUpdatedEvent;
import com.metadot.book.connectr.client.event.FriendUpdatedEventHandler;
import com.metadot.book.connectr.client.event.ShowFriendPopupEvent;
import com.metadot.book.connectr.client.helper.ClickPoint;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

public class FriendListPresenter implements Presenter {

  private List<FriendSummaryDTO> friendSummaries;
  private List<Integer> selectedRows;

  public interface Display {
    HasClickHandlers getAddButton();

    HasClickHandlers getList();

    void setData(List<String> friendNames);

    int getClickedRow(ClickEvent event);

    ClickPoint getClickedPoint(ClickEvent event);

    List<Integer> getSelectedRows();

    Widget asWidget();
  }

  private final FriendsServiceAsync rpcService;
  private final SimpleEventBus eventBus;
  private final Display display;

  public FriendListPresenter(FriendsServiceAsync rpcService, MessagesServiceAsync messagesService, SimpleEventBus eventBus,
      Display view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.display = view;
    bind();
  }

  public void bind() {
    GWT.log("FriendListPresenter: binding");

    display.getAddButton().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        eventBus.fireEvent(new FriendAddEvent());
      }
    });

    if (display.getList() != null)
      display.getList().addClickHandler(new ClickHandler() {

        public void onClick(ClickEvent event) {
          int selectedPropertyButtonRow = display.getClickedRow(event);
          GWT.log("Friend list clicked");
          if (selectedPropertyButtonRow >= 0) {
            GWT.log("Friend list property button clicked: " + selectedPropertyButtonRow);
            ClickPoint point = display.getClickedPoint(event);
            FriendSummaryDTO friend = friendSummaries.get(selectedPropertyButtonRow);
            eventBus.fireEvent(new ShowFriendPopupEvent(friend, point));
          } else {
            GWT.log("Friend list check box clicked");
            selectedRows = display.getSelectedRows();
            fireFriendListChangeEvent();
          }
        }

      });

    // Listen to events
    eventBus.addHandler(FriendUpdatedEvent.TYPE, new FriendUpdatedEventHandler() {
      @Override public void onFriendUpdated(FriendUpdatedEvent event) {
        fetchFriendSummaryDTO();
      }
    });

    eventBus.addHandler(FriendDeletedEvent.TYPE, new FriendDeletedEventHandler() {
      @Override public void onFriendDeleted(FriendDeletedEvent event) {
        fetchFriendSummaryDTO();
      }
    });
  }

  public void go(final HasWidgets container) {
    GWT.log("FriendListPresenter.go");
    container.clear();
    container.add(display.asWidget());
    fetchFriendSummaryDTO();
  }

  public void sortFriendSummaryDTO() {
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

  public void setFriendSummaryDTO(List<FriendSummaryDTO> FriendSummaryDTO) {
    this.friendSummaries = FriendSummaryDTO;
  }

  public FriendSummaryDTO getFriendSummary(int index) {
    return friendSummaries.get(index);
  }

  private void fetchFriendSummaryDTO() {

    new RPCCall<ArrayList<FriendSummaryDTO>>() {
      @Override protected void callService(AsyncCallback<ArrayList<FriendSummaryDTO>> cb) {
        rpcService.getFriendSummaries(cb);
      }

      @Override public void onSuccess(ArrayList<FriendSummaryDTO> result) {
        friendSummaries = result;
        sortFriendSummaryDTO();
        display.setData(toStringList(friendSummaries));
        selectedRows = display.getSelectedRows();
        fireFriendListChangeEvent();
      }

      @Override public void onFailure(Throwable caught) {
        Window.alert("Error fetching friend summaries: " + caught.getMessage());
      }
    }.retry(3);

  }

  private List<String> toStringList(List<FriendSummaryDTO> friendSummaries) {
    List<String> list = new ArrayList<String>();
    for (FriendSummaryDTO f : friendSummaries) {
      list.add(f.getDisplayName());
    }

    return list;
  }

  private void fireFriendListChangeEvent() {
    Integer row;
    List<FriendSummaryDTO> selection = new ArrayList<FriendSummaryDTO>();
    if (selectedRows != null) {
      for (Iterator<Integer> i = selectedRows.iterator(); i.hasNext();) {
        row = (Integer) i.next();
        GWT.log("selected: " + row.toString());
        selection.add(friendSummaries.get(row));
      }
    }

    GWT.log("Firing FriendListChangedEvent");
    eventBus.fireEvent(new FriendListChangedEvent(selection, friendSummaries));
  }

}
