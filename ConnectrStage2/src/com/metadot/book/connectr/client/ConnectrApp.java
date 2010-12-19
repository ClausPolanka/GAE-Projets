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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.metadot.book.connectr.client.presenter.BusyIndicatorPresenter;
import com.metadot.book.connectr.client.presenter.FriendListPresenter;
import com.metadot.book.connectr.client.service.FriendsService;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.client.service.MessagesService;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;
import com.metadot.book.connectr.client.service.UserAccountService;
import com.metadot.book.connectr.client.service.UserAccountServiceAsync;
import com.metadot.book.connectr.client.view.BusyIndicatorView;
import com.metadot.book.connectr.client.view.FriendListView;
import com.metadot.book.connectr.shared.UserAccountDTO;

public class ConnectrApp implements EntryPoint {

  interface ConnectrAppUiBinder extends UiBinder<DockLayoutPanel, ConnectrApp> {}

  SerializationStreamFactory pushServiceStreamFactory;

  @UiField HeaderPanel headerPanel;
  @UiField ScrollPanel mainPanel;
  @UiField VerticalPanel friendListPanel;

  RootLayoutPanel root;

  private static ConnectrApp singleton;
  private UserAccountDTO currentUser;
  private final UserAccountServiceAsync userService = GWT.create(UserAccountService.class);
  private SimpleEventBus eventBus = new SimpleEventBus();

  // Presenters
  private FriendListPresenter friendListPresenter;

  /**
   * Gets the singleton application instance.
   */
  public static ConnectrApp get() {
    return singleton;
  }

  BusyIndicatorPresenter busyIndicator = new BusyIndicatorPresenter(eventBus, new BusyIndicatorView("Working hard..."));

  private static final ConnectrAppUiBinder binder = GWT.create(ConnectrAppUiBinder.class);

  public void onModuleLoad() {

    singleton = this;

    login();
    createUI();
  }
  
  private void login() {
    userService.login("email", "password",
        new AsyncCallback<UserAccountDTO>() {
          public void onFailure(Throwable caught) {
            Window.alert("An error occurred");
          }

          public void onSuccess(UserAccountDTO result) {
            setCurrentUser(result);
          }
        });
  }

  private void createUI() {

    DockLayoutPanel outer = binder.createAndBindUi(this);
    root = RootLayoutPanel.get();
    root.clear();
    root.add(outer);

    final MessagesServiceAsync messagesService = GWT.create(MessagesService.class);
    FriendsServiceAsync friendService = GWT.create(FriendsService.class);

    AppController appViewer = new AppController(friendService, eventBus);
    appViewer.go();

    friendListPresenter = new FriendListPresenter(friendService, messagesService, eventBus, new FriendListView());
    friendListPresenter.go(friendListPanel);

  }

  public SimpleEventBus getEventBus() {
    return eventBus;
  }

  void setCurrentUser(UserAccountDTO currentUser) {
    this.currentUser = currentUser;
  }

  UserAccountDTO getCurrentUser() {
    return currentUser;
  }

  /**
   * @return the mainPanel
   */
  ScrollPanel getMainPanel() {
    return mainPanel;
  }

  /**
   * @return the friendList
   */
  VerticalPanel getFriendListPanel() {
    return friendListPanel;
  }

}
