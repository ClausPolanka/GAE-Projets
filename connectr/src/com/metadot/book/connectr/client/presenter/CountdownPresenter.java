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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.helper.RPCCall;
import com.metadot.book.connectr.client.service.MessagesServiceAsync;

public class CountdownPresenter implements Presenter {

  public interface Display {
    Widget asWidget();
    void setCountdownLabel(Integer timeleftInSec);
  }

  private final MessagesServiceAsync rpcService;
  @SuppressWarnings("unused")
  private final SimpleEventBus eventBus;
  private final Display display;

  public CountdownPresenter(MessagesServiceAsync rpcService, SimpleEventBus eventBus, Display view) {
    this.rpcService = rpcService;
    this.eventBus = eventBus;
    this.display = view;
  }

  public void bind() {

  }
  
  /**
   * Tell the backend to start update the potentially old news feeds.
   * @param messagesService
   */
  public void initiateFeedUpdate() {
    GWT.log("Requesting news feed update");
    
    new RPCCall<Boolean>() {
      @Override
      protected void callService(AsyncCallback<Boolean> cb) {
        rpcService.initiateUserFeedUpdate(cb);
      }

      @Override
      public void onSuccess(Boolean result) {
    
      }

      @Override
      public void onFailure(Throwable caught) {
        Window.alert("Error: " + caught.getMessage());
      }
    }.retry(3);
  }
  
  
  public void go(final HasWidgets container) {
    bind();
    container.clear();
    container.add(display.asWidget());
  }
}
