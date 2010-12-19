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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.helper.ClickPoint;
import com.metadot.book.connectr.client.presenter.FriendPopupPresenter;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

/**
 * A simple popup that displays friend's info w/ edit / delete buttons.
 */
public class FriendPopupView extends PopupPanel implements FriendPopupPresenter.Display {
  @UiTemplate("FriendPopupView.ui.xml")
  interface Binder extends UiBinder<Widget, FriendPopupView> {
  }

  private static final Binder binder = GWT.create(Binder.class);

  @UiField
  Anchor edit, delete;
  @UiField
  Label friendNameLabel;

  FriendSummaryDTO friend;

  public FriendPopupView() {
    // The popup's constructor's argument is a boolean specifying that
    // it auto-close itself when the user clicks outside of it.
    super(true);
    add(binder.createAndBindUi(this));
  }

  public FriendPopupView(String displayName, ClickPoint location) {
    this();
    setNameAndShow(displayName, location);
  }

  public FriendPopupView(FriendSummaryDTO friend) {
    this();
    this.friend = friend;
    setName(friend.getDisplayName());
  }

  @Override
  public void setName(String name) {
    friendNameLabel.setText(name);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getDeleteButton() {
    return delete;
  }

  @Override
  public HasClickHandlers getEditButton() {
    return edit;
  }

  @Override
  public HasText getFriendNameLabel() {
    return friendNameLabel;
  }

  @Override
  public void hide() {
    super.hide();
  }

  @Override
  public void setNameAndShow(String displayName, ClickPoint location) {
    setName(displayName);
    setPopupPosition(location.getLeft(), location.getTop());
    show();
  }

}