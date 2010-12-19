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
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamFactory;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.metadot.book.connectr.client.channel.Channel;
import com.metadot.book.connectr.client.channel.ChannelFactory;
import com.metadot.book.connectr.client.channel.SocketListener;
import com.metadot.book.connectr.client.event.ContentAvailableEvent;
import com.metadot.book.connectr.client.event.LoginEvent;
import com.metadot.book.connectr.client.helper.PushService;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.presenter.BusyIndicatorPresenter;
import com.metadot.book.connectr.client.presenter.FriendListPresenter;
import com.metadot.book.connectr.client.presenter.LoginPresenter;
import com.metadot.book.connectr.client.presenter.UserBadgePresenter;
import com.metadot.book.connectr.client.service.FriendsService;
import com.metadot.book.connectr.client.service.FriendsServiceAsync;
import com.metadot.book.connectr.client.service.LoginService;
import com.metadot.book.connectr.client.service.LoginServiceAsync;
import com.metadot.book.connectr.client.service.MessagesService;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;
import com.metadot.book.connectr.client.view.BusyIndicatorView;
import com.metadot.book.connectr.client.view.FriendListView;
import com.metadot.book.connectr.client.view.LoginView;
import com.metadot.book.connectr.client.view.UserBadgeView;
import com.metadot.book.connectr.shared.UserAccountDTO;
import com.metadot.book.connectr.shared.messages.ChannelTextMessage;
import com.metadot.book.connectr.shared.messages.Message;

public class ConnectrApp implements EntryPoint {

  interface ConnectrAppUiBinder extends UiBinder<DockLayoutPanel, ConnectrApp> {}

  SerializationStreamFactory pushServiceStreamFactory;

  @UiField HeaderPanel headerPanel;
  @UiField ScrollPanel mainPanel;
  @UiField VerticalPanel friendListPanel;

  RootLayoutPanel root;

  private static ConnectrApp singleton;
  private UserAccountDTO currentUser;
  private SimpleEventBus eventBus = new SimpleEventBus();
  BusyIndicatorPresenter busyIndicator = new BusyIndicatorPresenter(eventBus, new BusyIndicatorView("Working hard..."));

  // Presenters
  private FriendListPresenter friendListPresenter;
  private UserBadgePresenter userBadgePresenter;

  // RPC services
  private LoginServiceAsync loginService = GWT.create(LoginService.class);;

  /**
   * Gets the singleton application instance.
   */
  public static ConnectrApp get() {
    return singleton;
  }

  private static final ConnectrAppUiBinder binder = GWT.create(ConnectrAppUiBinder.class);

 
  public void onModuleLoad() {
    pushServiceStreamFactory = (SerializationStreamFactory) PushService.App.getInstance();
    singleton = this;

    getLoggedInUser();
    
  }

  private void getLoggedInUser() {
    new RPCCall<UserAccountDTO>() {
      @Override protected void callService(AsyncCallback<UserAccountDTO> cb) {
        loginService.getLoggedInUserDTO(cb);
      }

      @Override public void onSuccess(UserAccountDTO loggedInUserDTO) {
        if (loggedInUserDTO == null) {
          // nobody is logged in
          showLoginView();
        } else {
          // user is logged in
          setCurrentUser(loggedInUserDTO);
          createUI();
        }
      }

      @Override public void onFailure(Throwable caught) {
        Window.alert("Error: " + caught.getMessage());
      }
    }.retry(3);

  }
  
  public void showLoginView() {
    root = RootLayoutPanel.get();
    root.clear();
    LoginPresenter loginPresenter = new LoginPresenter(eventBus, new LoginView());
    loginPresenter.go(root);
  }

  /**
   * Tell the backend to start update the potentially old news feeds.
   * 
   * @param messagesService
   */
  public void initiateFeedUpdate(final MessagesServiceAsync messagesService) {
    GWT.log("Requesting news feed update");

    new RPCCall<Boolean>() {
      @Override protected void callService(AsyncCallback<Boolean> cb) {
        messagesService.initiateUserFeedUpdate(cb);
      }

      @Override public void onSuccess(Boolean result) {}

      @Override public void onFailure(Throwable caught) {
        Window.alert("Error: " + caught.getMessage());
      }
    }.retry(3);
  }

  private void goAfterLogin() {

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

    userBadgePresenter = new UserBadgePresenter(loginService, eventBus, new UserBadgeView());
    userBadgePresenter.go(headerPanel.getuserBadgePanel());

    initiateFeedUpdate(messagesService);

    listenToChannel();
  }

  private void listenToChannel() {
    String channelId = currentUser.getChannelId();
    if (channelId == null)
      return; // Use of Channel API not enabled

    GWT.log("Creating client channel id: " + currentUser.getChannelId());
    Channel channel = ChannelFactory.createChannel(currentUser.getChannelId());
    channel.open(new SocketListener() {
      public void onOpen() {
        GWT.log("Channel onOpen()");
      }

      public void onMessage(String encodedData) {
        try {
          SerializationStreamReader reader = pushServiceStreamFactory.createStreamReader(encodedData);
          Message message = (Message) reader.readObject();
          handleMessage(message);
        } catch (SerializationException e) {
          throw new RuntimeException("Unable to deserialize " + encodedData, e);
        }
      }
    });
  }

  public SimpleEventBus getEventBus() {
    return eventBus;
  }

  private void createUI() {

    GWT.runAsync(new RunAsyncCallback() {
      @Override public void onFailure(Throwable reason) {
        Window.alert("Code download error: " + reason.getMessage());
      }

      @Override public void onSuccess() {
        goAfterLogin();
        eventBus.fireEvent(new LoginEvent(currentUser));
      }
    });
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

  /**
   * Handle messages pushed from the server.
   */
  public void handleMessage(Message msg) {
    switch (msg.getType()) {

    case NEW_CONTENT_AVAILABLE:
      GWT.log("Pushed msg received: NEW_CONTENT_AVAILABLE");
      eventBus.fireEvent(new ContentAvailableEvent());
      break;

    case TEXT_MESSAGE:
      String ttext = ((ChannelTextMessage) msg).get();
      GWT.log("Pushed msg received: TEXT_MESSAGE: " + ttext);
      break;

    default:
      Window.alert("Unknown message type: " + msg.getType());
    }
  }
}
