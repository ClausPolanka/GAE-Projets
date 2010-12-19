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
package com.metadot.book.connectr.client.helper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.metadot.book.connectr.shared.messages.Message;

/**
 * A GWT RPC service interface for RPC calls which are pushed to
 * this game client.
 * <p>
 * This interface is odd in that the client doesn't actually make calls
 * through this interface to the server. Instead the server uses server-side
 * push to send GWT RPC encoded data to the client via an alternate
 * transport. The definition of this interface helps to ensure that all
 * the correct de-serialization code is generated for the client. A call to
 * GWT.create on this service must be made to ensure the de-serialization
 * code is actually generated.
 *
 * @author Toby Reyelts
 */
@RemoteServiceRelativePath("push_service")
public interface PushService extends RemoteService {

  /**
   * Utility/Convenience class. Use PushService.App.getInstance() to access
   * static instance of PushServiceAsync
   */
  public static class App {
    private static final PushServiceAsync ourInstance = (PushServiceAsync) GWT
        .create(PushService.class);

    public static PushServiceAsync getInstance() {
      return ourInstance;
    }
  }
  /**
   * A dummy method ensuring that Message and all its subclasses
   * are client serializable.
   */
  Message receiveMessage();
}
