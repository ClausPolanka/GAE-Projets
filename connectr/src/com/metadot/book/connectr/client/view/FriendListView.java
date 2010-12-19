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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.metadot.book.connectr.client.helper.ClickPoint;
import com.metadot.book.connectr.client.presenter.FriendListPresenter;
import com.metadot.book.connectr.client.resources.GlobalResources;

public class FriendListView extends Composite implements FriendListPresenter.Display {

  @UiField
  FlexTable friendsTable;

  @UiField
  Hyperlink addNew;
  
  @UiField
  Label loadingLabel;

  private List<String> deselectedNames = new ArrayList<String>();

  private static FriendListUiBinder uiBinder = GWT.create(FriendListUiBinder.class);

  interface FriendListUiBinder extends UiBinder<Widget, FriendListView> {
  }

  /**
   * Constructor
   */
  public FriendListView() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  private void displayFriends(List<String> friends) {

    int i = 0;
    getSelectedRows(); 
    friendsTable.removeAllRows();

    if (friends == null || friends.size() == 0) {
      loadingLabel.setText("No friends.");
      return;
    }
    
    loadingLabel.setVisible(false);
    List<String> seen = new ArrayList<String>();

    for (final String friend : friends) {
      String name = truncateLongName(friend);
      CheckBox checkBoxName = new CheckBox(truncateLongName(name));
      
      if(deselectedNames.contains(truncateLongName(name))) { 
      checkBoxName.setValue(false);
      seen.add(name);
      GWT.log("not selected: " + name);
      } else {
      checkBoxName.setValue(true);
      }

      final Image propertyButton = new Image(GlobalResources.RESOURCE
          .propertyButton());
      propertyButton.setStyleName("pointer");

      friendsTable.setWidget(i, 0, checkBoxName);
      friendsTable.setWidget(i, 1, propertyButton);
      friendsTable.getCellFormatter().addStyleName(i, 0,
          "friendNameInList");
      i++;
    }
    
    deselectedNames = seen;

  }
  
  

  /**
   * Shorten long displayName to something smaller if name is too long.
   * 
   * @param displayName
   * @return a possibly truncated displayNames
   */
  private String truncateLongName(String displayName) {
    final int MAX = 18; // truncate string if longer than MAX
    final String SUFFIX = "...";

    if (displayName.length() < MAX)
      return displayName;

    String shortened = displayName.substring(0, MAX - SUFFIX.length()) + SUFFIX;

    return shortened;
  }

  @Override
  public Widget asWidget() {
    return this;
  }

  @Override
  public HasClickHandlers getAddButton() {
    return addNew;
  }

  @Override
  public int getClickedRow(ClickEvent event) {
    int selectedRow = -1;
    HTMLTable.Cell cell = friendsTable.getCellForEvent(event);

    if (cell != null) {
      // Suppress clicks if not on the property button
      if (cell.getCellIndex() > 0) {
        selectedRow = cell.getRowIndex();
      }
    }

    return selectedRow;
  }

  @Override
  public ClickPoint getClickedPoint(ClickEvent event) {
    final Image img;
    int selectedRow = -1;
    ClickPoint point = null;
    HTMLTable.Cell cell = friendsTable.getCellForEvent(event);

    if (cell != null) {
      // Suppress clicks if not on the property button
      if (cell.getCellIndex() > 0) {
        selectedRow = cell.getRowIndex();
        img = (Image) friendsTable.getWidget(selectedRow, 1);
        int left = img.getAbsoluteLeft();
        int top = img.getAbsoluteTop();
        point = new ClickPoint(top, left);
      }
    }

    return point;
  }

  @Override
  public HasClickHandlers getList() {
    return friendsTable;
  }

  
  @Override
  public List<Integer> getSelectedRows() {
    ArrayList<Integer> selectedRows = new ArrayList<Integer>();
    ArrayList<String> deselected = new ArrayList<String>();

    for (int i = 0; i < friendsTable.getRowCount(); ++i) {
      CheckBox checkBox = (CheckBox) friendsTable.getWidget(i, 0);
      if (checkBox.getValue()) {
        selectedRows.add(i);
      } else {
        deselected.add(checkBox.getHTML());
        GWT.log(checkBox.getHTML() + " not selected");
      }
    }
    
    deselectedNames = deselected;
    
    return selectedRows;
  }

  @Override
  public void setData(List<String> data) {
    displayFriends(data);
  }

}
