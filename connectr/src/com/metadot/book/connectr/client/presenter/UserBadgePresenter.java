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
import com.metadot.book.connectr.client.event.LoginEvent;
import com.metadot.book.connectr.client.event.LoginEventHandler;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.LoginServiceAsync;
import com.metadot.book.connectr.shared.AuthTypes;
import com.metadot.book.connectr.shared.UserAccountDTO;

public class UserBadgePresenter implements Presenter {
  public interface Display {
    HasClickHandlers getLogoutLink();
    HasText getUsernameLabel();
    Widget asWidget();
  }

  private final LoginServiceAsync rpcService;
  private final SimpleEventBus eventBus;
  private final Display display;

  private UserAccountDTO currentUser;

  public UserBadgePresenter(LoginServiceAsync rpcService, SimpleEventBus eventBus,
      Display display) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.display = display;
  }

  public void bind() {
    this.display.getLogoutLink().addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        doLogout();
      }
    });


    // Listen to login events
    eventBus.addHandler(LoginEvent.TYPE, new LoginEventHandler() {
      @Override
      public void onLogin(LoginEvent event) {
        currentUser = event.getUser();
        doLogin();
      }
    });
  }

  private void doLogout() {
    new RPCCall<Void>() {
      @Override
      protected void callService(AsyncCallback<Void> cb) {
        if(facebookUser()){
          Window.Location.assign("/facebooklogout.jsp");
        } else { 
          rpcService.logout(cb);
        }
      }

      @Override
      public void onSuccess(Void result) {
        // logout event already fired by RPCCall
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("An error occurred: " + caught.toString());
      }
    }.retry(3);

  }
  
  private boolean facebookUser() {
    return currentUser.getUniqueId().endsWith(AuthTypes.FACEBOOK.toString());
  }

  private void doLogin() {
    if (currentUser != null)
      UserBadgePresenter.this.display.getUsernameLabel().setText(
          currentUser.getName());
    else
      UserBadgePresenter.this.display.getUsernameLabel().setText(
          "Login first");
  }

  public void go(final HasWidgets container) {
    container.clear();
    container.add(display.asWidget());
    bind();
  }


}

  