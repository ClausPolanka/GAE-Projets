package com.swagswap.web.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.exceptions.InvalidSwagItemRatingException;
import com.swagswap.web.gwt.client.domain.LoginInfo;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

public interface LoginServiceAsync {
  void login(String requestUri, AsyncCallback<LoginInfo> callback);
  
  void addOrUpdateRating(String userEmail, SwagItemRating swagItemRating, AsyncCallback callback) throws InvalidSwagItemRatingException ;
}
