package login.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LoginWidget extends Composite {
	Button login = new Button("Login");
	Button cancel = new Button("Cancel");

	public LoginWidget() {
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Please login");
		initWidget(dialogBox);

		VerticalPanel container = new VerticalPanel();
		container.setSpacing(4);
		container.add(new Label("Username:"));
		container.add(new TextBox());
		container.add(new Label("Password:"));
		container.add(new PasswordTextBox());

		HorizontalPanel buttons = new HorizontalPanel();
		buttons.setSpacing(12);
		buttons.add(login);
		buttons.add(cancel);

		container.add(buttons);

		// add user click handler
		login.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onLoginClick();
			}
		});
		dialogBox.setWidget(container);
	}

	void onLoginClick() {
		Window.alert("You are logged in!");
	}
}
