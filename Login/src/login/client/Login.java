package login.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

public class Login implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get("login").add(new LoginWidget());
	}

}