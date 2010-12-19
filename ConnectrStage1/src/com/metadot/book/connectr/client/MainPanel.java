package com.metadot.book.connectr.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class MainPanel extends Composite {

	interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
	}

	private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);

	public MainPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

}
