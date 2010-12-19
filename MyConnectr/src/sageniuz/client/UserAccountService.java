package sageniuz.client;

import sageniuz.shared.UserAccountDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userAccountService")
public interface UserAccountService extends RemoteService {

	public UserAccountDTO login(String email, String password);

}
