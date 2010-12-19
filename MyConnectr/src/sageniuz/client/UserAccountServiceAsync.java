package sageniuz.client;

import sageniuz.shared.UserAccountDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserAccountServiceAsync {

	void login(String email, String password, AsyncCallback<UserAccountDTO> callback);

}
