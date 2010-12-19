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

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestTimeoutException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.metadot.book.connectr.client.ConnectrApp;
import com.metadot.book.connectr.client.event.RPCInEvent;
import com.metadot.book.connectr.client.event.RPCOutEvent;
import com.metadot.book.connectr.shared.exception.NotLoggedInException;

public abstract class RPCCall<T> implements AsyncCallback<T> {

  protected abstract void callService(AsyncCallback<T> cb);

  private void call(final int retriesLeft) {
    onRPCOut();

    callService(new AsyncCallback<T>() {
      public void onFailure(Throwable caught) {
        onRPCIn();
        GWT.log(caught.toString(), caught);
        try {
          throw caught;
        } catch (InvocationException invocationException) {
          if (retriesLeft <= 0) {
            RPCCall.this.onFailure(invocationException);
          } else {
            call(retriesLeft - 1); // retry call
          }
        } catch (IncompatibleRemoteServiceException remoteServiceException) {
          Window.alert("The app may be out of date. Reload this page in your browser.");
        } catch (SerializationException serializationException) {
          Window.alert("A serialization error occurred. Try again.");
        } catch (NotLoggedInException e) {
          // in final version of app, here will fire event that 
          // triggers redirect to the login screen
        } catch (RequestTimeoutException e) {
          Window.alert("This is taking too long, try again");
        } catch (Throwable e) {// application exception
          RPCCall.this.onFailure(e);
        }
      }

      public void onSuccess(T result) {
        onRPCIn();
        RPCCall.this.onSuccess(result);
      }
    });
  }

  private void onRPCIn() {
    ConnectrApp.get().getEventBus().fireEvent(new RPCInEvent());
  }

  private void onRPCOut() {
    ConnectrApp.get().getEventBus().fireEvent(new RPCOutEvent());
  }

  public void retry(int retryCount) {
    call(retryCount);
  }
}
