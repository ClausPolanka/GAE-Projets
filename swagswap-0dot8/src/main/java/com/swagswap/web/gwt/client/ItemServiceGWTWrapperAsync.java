package com.swagswap.web.gwt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.swagswap.web.gwt.client.domain.SwagItemCommentGWTDTO;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

public interface ItemServiceGWTWrapperAsync {

    public abstract void fetch (AsyncCallback<List<SwagItemGWTDTO>> asyncCallback);
    
    public abstract void fetch (Long key, AsyncCallback<SwagItemGWTDTO> asyncCallback);

    public abstract void add (SwagItemGWTDTO swagItemGWTDTO, AsyncCallback<SwagItemGWTDTO> asyncCallback);

    public abstract void update (SwagItemGWTDTO swagItemGWTDTO, AsyncCallback<SwagItemGWTDTO> asyncCallback);

    public abstract void remove (SwagItemGWTDTO swagItemGWTDTO, AsyncCallback<Object> asyncCallback);

	void updateRating(Long swagItemKey, int computedRatingDifference, boolean isNew, AsyncCallback callback);
	
	void addComment(SwagItemCommentGWTDTO swagItemCommentGWTDTO, AsyncCallback callback);

}
