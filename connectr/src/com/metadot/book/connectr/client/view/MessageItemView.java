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

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class MessageItemView extends Composite {

  private static MessageItemUiBinder uiBinder = GWT.create(MessageItemUiBinder.class);

  interface MessageItemUiBinder extends UiBinder<Widget, MessageItemView> {}

  @UiField HTML title, description;

  @UiField Label date, author;

  public MessageItemView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @SuppressWarnings("deprecation") public MessageItemView(String title, String description, Date createdOn, String url, String author,
      String imageUrl) {
    this();
    this.title.setHTML("<a target='_new' href=" + url + ">" + title + "</a>");
    String img = "";
    if (imageUrl != null && !imageUrl.isEmpty()) {
      img = "<img src=\"" + imageUrl + "\" width=25 valign=\"middle\">&nbsp;";
    }
    this.description.setHTML(img + description);
    if (createdOn != null) {
      this.date.setText(createdOn.toLocaleString());
    }
    else {
      this.date.setText("");
    }
    if (author != null && !author.isEmpty())
      this.author.setText(author);
    else
      this.author.setText("-");

  }

}
