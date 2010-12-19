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
package com.metadot.book.connectr.server;

import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.service.LoginService;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.shared.UserAccountDTO;
import com.metadot.book.connectr.shared.exception.NotLoggedInException;

@SuppressWarnings("serial")
public class LoginServiceImpl extends RemoteServiceServlet implements
    LoginService {
  public final static String CHANNEL_ID = "channel_id";
  
  @Override
  public UserAccountDTO getLoggedInUserDTO() {
    UserAccountDTO userDTO;
    HttpSession session = getThreadLocalRequest().getSession();

    UserAccount u = LoginHelper.getLoggedInUser(session, null);
    if (u == null)
      return null;
    userDTO = UserAccount.toDTO(u);
    return userDTO;
  }

  @Override
  public void logout() throws NotLoggedInException {
    getThreadLocalRequest().getSession().invalidate();
    throw new NotLoggedInException("Logged out");
  }

} // end class