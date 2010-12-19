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
package com.metadot.book.connectr.client.channel;

import com.google.gwt.core.client.JavaScriptObject;

public class Channel extends JavaScriptObject {
    protected Channel() {
    }

    public final native Socket open(SocketListener listener) /*-{
        var socket = this.open();
        socket.onopen = function(event) {
          listener.@com.metadot.book.connectr.client.channel.SocketListener::onOpen()();
        };
        socket.onmessage = function(event) {
          listener.@com.metadot.book.connectr.client.channel.SocketListener::onMessage(Ljava/lang/String;)(event.data);
        };
        return socket;
    }-*/;
}
