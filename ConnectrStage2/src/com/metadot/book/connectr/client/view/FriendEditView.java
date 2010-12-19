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
/**
 * 
 */
package com.metadot.book.connectr.client.view;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.presenter.FriendEditPresenter;

/**
 * Demonstrate UIbinder Styles Separating UI and actions
 * Leaving w/o saving warning
 * 
 */
public class FriendEditView extends Composite implements
    FriendEditPresenter.Display {

  private static FriendEditUiBinder uiBinder = GWT
      .create(FriendEditUiBinder.class);

  interface FriendEditUiBinder extends UiBinder<Widget, FriendEditView> {
  }

  @UiField
  TextBox firstNameField, lastNameField, emailField, url;
  @UiField
  Button cancelButton, saveButton, addUrl;
  @UiField
  FlexTable urlTable;
  @UiField
  Label loadingLabel;

  public FriendEditView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  protected void onLoad() {
    super.onLoad();
    firstNameField.setFocus(true);
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getCancelButton() {
    return cancelButton;
  }

  @Override
  public HasValue<String> getEmailAddress() {
    return emailField;
  }

  @Override
  public HasValue<String> getFirstName() {
    return firstNameField;
  }

  @Override
  public HasValue<String> getLastName() {
    return lastNameField;
  }
  
  @Override
  public HasValue<String> getUrl() {
    return url;
  }

  @Override
  public HasClickHandlers getSaveButton() {
    return saveButton;
  }

  @Override
  public HasClickHandlers getAddUrlButton() {
    return addUrl;
  }
  
  @Override
  public HasClickHandlers getList() {
    return urlTable;
  }
  
  @Override
  public String getDeletedUrl(ClickEvent event) {
    String deletedUrl = null;
    int selectedRow = -1;
    HTMLTable.Cell cell = urlTable.getCellForEvent(event);

    if (cell != null) {
      // Suppress clicks on the URL field
      int index = cell.getCellIndex();
      if (index == 0) {
        selectedRow = cell.getRowIndex();
        deletedUrl = urlTable.getText(selectedRow, 1);
        urlTable.removeRow(selectedRow);
      }
    }

    return deletedUrl;
  }

  @Override public void setData(Set<String> urls) {
    displayUrls(urls);
  }
  
  private void displayUrls(Set<String> urls) {

    int i = 0;

    if (urls == null || urls.size() == 0) {
      loadingLabel.setText("No URLs yet.");
      return;
    }
    
    loadingLabel.setVisible(false);
    for (final String url : urls) {
      urlTable.setWidget(i, 0, new Button(" X "));
      urlTable.setText(i, 1, url);
      i++;
    }

  }

}
