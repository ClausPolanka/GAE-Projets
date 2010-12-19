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

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.event.RPCInEvent;
import com.metadot.book.connectr.client.event.RPCInEventHandler;
import com.metadot.book.connectr.client.event.RPCOutEvent;
import com.metadot.book.connectr.client.event.RPCOutEventHandler;

public class BusyIndicatorPresenter implements Presenter {

  public interface Display {
    void show();
    void hide();
    Widget asWidget();
  }

  int outCount = 0; // # of RPC calls sent by the app. If > 0 we'll show a
            // 'busy' indicator.

  private final SimpleEventBus eventBus;
  private Display display;

  public BusyIndicatorPresenter(SimpleEventBus eventBus, Display view) {
    this.eventBus = eventBus;
    this.display = view;

    bind();
  }

  public void bind() {
    eventBus.addHandler(RPCInEvent.TYPE, new RPCInEventHandler() {
      @Override
      public void onRPCIn(RPCInEvent event) {
        outCount = outCount > 0 ? --outCount : 0;
        if (outCount <= 0) {
          display.hide();
        }
      }
    });
    eventBus.addHandler(RPCOutEvent.TYPE, new RPCOutEventHandler() {
      @Override
      public void onRPCOut(RPCOutEvent event) {
        outCount++;
        display.show();
      }
    });
  }

  public void go(HasWidgets container) {
    // nothing to do
  }
}
