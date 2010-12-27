package com.swagswap.web.gwt.client;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.swagswap.exceptions.AccessDeniedException;
import com.swagswap.exceptions.ImageTooLargeException;
import com.swagswap.exceptions.LoadImageFromURLException;
import com.swagswap.web.gwt.client.domain.SwagItemCommentGWTDTO;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;
/**
 * method names map to SmartGWT Datasource naming conventions
 *
 */
@RemoteServiceRelativePath ("smartGWTItemServiceWrapper")
public interface ItemServiceGWTWrapper extends RemoteService {

	//maps to getAll in ItemService
    List<SwagItemGWTDTO> fetch ();
    
	public SwagItemGWTDTO fetch(Long key);

    SwagItemGWTDTO add (SwagItemGWTDTO swagItemGWTDTO) throws AccessDeniedException, LoadImageFromURLException, ImageTooLargeException;

    SwagItemGWTDTO update (SwagItemGWTDTO swagItemGWTDTO) throws AccessDeniedException, LoadImageFromURLException, ImageTooLargeException;

    void remove (SwagItemGWTDTO swagItemGWTDTO);
    
	void updateRating(Long swagItemKey, int computedRatingDifference, boolean isNew);
	
	void addComment(SwagItemCommentGWTDTO swagItemCommentGWTDTO);
	
    public static class Util {
		private static ItemServiceGWTWrapperAsync instance;
		public static ItemServiceGWTWrapperAsync getInstance() {
			return (instance == null) ? (instance = GWT.create(ItemServiceGWTWrapper.class)) : instance;
		}
	}

}
