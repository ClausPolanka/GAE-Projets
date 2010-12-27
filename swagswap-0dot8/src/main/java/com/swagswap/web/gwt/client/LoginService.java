package com.swagswap.web.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.exceptions.InvalidSwagItemRatingException;
import com.swagswap.web.gwt.client.domain.LoginInfo;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

@RemoteServiceRelativePath("login")
public interface LoginService extends RemoteService {
  public LoginInfo login(String requestUri);
  
  public void addOrUpdateRating(String userEmail, SwagItemRating swagItemRating) throws InvalidSwagItemRatingException ;
}
