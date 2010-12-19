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
package com.metadot.book.connectr.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.presenter.MessageListPresenter;
import com.metadot.book.connectr.shared.StreamItemSummaryDTO;

public class MessageListView extends Composite implements
    MessageListPresenter.Display {

  private static MessageListViewUiBinder uiBinder = GWT
      .create(MessageListViewUiBinder.class);

  interface MessageListViewUiBinder extends UiBinder<Widget, MessageListView> {
  }

  @UiField
  VerticalPanel messagesPanel;
  private List<StreamItemSummaryDTO> messages;

  public MessageListView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public MessageListView(List<StreamItemSummaryDTO> messages) {
    this();
    this.messages = messages;
    displayMessages();
  }

  private void displayMessages() {
    messagesPanel.clear();
    if (messages == null)
      return;
    for (StreamItemSummaryDTO message : messages) {
      messagesPanel.add(new MessageItemView(message.getTitle(), 
        message.getDescription(), message.getCreatedOn()));
    }
  }


  @Override
  public Widget asWidget() {
    return this;
  }

  
  @Override
  public void setData(List<StreamItemSummaryDTO> data) {
    this.messages = data;
    displayMessages();
  }

  @Override
  public void showLoadingMessage() {
    messagesPanel.clear();
    messagesPanel.add(new Label("Loading..."));
  }

}
