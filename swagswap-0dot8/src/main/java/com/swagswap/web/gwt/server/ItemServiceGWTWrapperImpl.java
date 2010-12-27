package com.swagswap.web.gwt.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;
import com.swagswap.exceptions.AccessDeniedException;
import com.swagswap.exceptions.ImageTooLargeException;
import com.swagswap.exceptions.LoadImageFromURLException;
import com.swagswap.service.ItemService;
import com.swagswap.web.gwt.client.ItemServiceGWTWrapper;
import com.swagswap.web.gwt.client.domain.SwagItemCommentGWTDTO;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

/**
 * Wraps ItemService to apply the DTO (anti)pattern.  Necessary since annotated
 * JDO objects (with GAE specific types like Text and Blob) won't serialize for GWT RPC 
 */
public class ItemServiceGWTWrapperImpl extends
		AutoinjectingRemoteServiceServlet implements ItemServiceGWTWrapper {
	private static final long serialVersionUID = 1L;

	private ItemService itemService;

	@Autowired
	@Required
	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	/**
	 * fetch all
	 */
	public List<SwagItemGWTDTO> fetch() {
		return toDTOList(itemService.getAll());
	}
	
	public SwagItemGWTDTO fetch(Long key) {
		return toDTO(itemService.get(key));
	}

	//SwagSwapGWT requires the updated item to be returned
	public SwagItemGWTDTO add(SwagItemGWTDTO swagItemGWTDTO) throws AccessDeniedException, LoadImageFromURLException, ImageTooLargeException {
		SwagItem updatedItem = itemService.save(toSwagItem(swagItemGWTDTO));
		return toDTO(updatedItem);
	}

	// TOOD combine these if possible
	public SwagItemGWTDTO update(SwagItemGWTDTO swagItemGWTDTO) throws AccessDeniedException, LoadImageFromURLException, ImageTooLargeException {
		return add(swagItemGWTDTO);
	}

	public void remove(SwagItemGWTDTO swagItemGWTDTO) {
		itemService.delete(swagItemGWTDTO.getKey());
	}
//
//	public void save(SwagItemGWTDTO swagItemDto){
//		itemService.save(toSwagItem(swagItemDto));
//	}

	public void updateRating(Long swagItemKey, int computedRatingDifference,
			boolean isNew) {
		itemService.updateRating(swagItemKey, computedRatingDifference, isNew);
	}
	
	public void addComment(SwagItemCommentGWTDTO swagItemComment) {
		itemService.addComment(toComment(swagItemComment));
	}
	
	private ArrayList<SwagItemGWTDTO> toDTOList(List<SwagItem> swagItems) {
		ArrayList<SwagItemGWTDTO> dtos = new ArrayList<SwagItemGWTDTO>();
		for (SwagItem swagItem : swagItems) {
			dtos.add(toDTO(swagItem));
		}
		return dtos;
	}

	private <T> ArrayList<T> toCopiedArrayList(List<T> listItems) {
		ArrayList<T> copiedList = new ArrayList<T>();
		for (T listItem : listItems) {
			copiedList.add(listItem);
		}
		return copiedList;
	}
	
	private ArrayList<SwagItemCommentGWTDTO> toCommentDTOList(List<SwagItemComment> listItems) {
		ArrayList<SwagItemCommentGWTDTO> copiedList = new ArrayList<SwagItemCommentGWTDTO>();
		for (SwagItemComment listItem : listItems) {
			copiedList.add(new SwagItemCommentGWTDTO(
					listItem.getSwagItemKey(),
					listItem.getItemOwnerGoogleID(),
					listItem.getSwagSwapUserNickname(), 
					listItem.getCommentText(), 
					listItem.getCreated()));
		}
		return copiedList;
	}
	

	private SwagItemComment toComment(
			SwagItemCommentGWTDTO swagItemCommentGWTDTO) {
		return new SwagItemComment(
				swagItemCommentGWTDTO.getSwagItemKey(),
				swagItemCommentGWTDTO.getGoogleID(),
				swagItemCommentGWTDTO.getSwagSwapUserNickname(),
				swagItemCommentGWTDTO.getCommentText());
	}
	
	public SwagItem toSwagItem(SwagItemGWTDTO dto) {
		SwagItem swagItem = new SwagItem();
		swagItem.setKey(dto.getKey());
		swagItem.setOwnerGoogleID(dto.getOwnerGoogleID());
		swagItem.setOwnerNickName(dto.getOwnerNickName());
		swagItem.setName(dto.getName());
		swagItem.setCompany(dto.getCompany());
		swagItem.setDescription(dto.getDescription());
		swagItem.setImageKey(dto.getImageKey());
		swagItem.setCreated((dto.getCreated()));
		swagItem.setLastUpdated((dto.getLastUpdated()));
//		swagItem.setImageBytes(dto.getNewImageBytes());
		swagItem.setImageURL(dto.getNewImageURL());
		// no need for
		// image,averageRating,numberOfRatings,created,lastUpdated,comments
		swagItem.setTags(dto.getTags());
		return swagItem;
	}

	public SwagItemGWTDTO toDTO(SwagItem swagItem) {
		if (swagItem==null) {
			return null;
		}
		ArrayList<String> paddedTags = toCopiedArrayList(swagItem.getTags());
		padTags(paddedTags);
		return new SwagItemGWTDTO(swagItem.getKey(), swagItem.getName(),
				swagItem.getCompany(), swagItem.getDescription(), swagItem.getImageKey(), swagItem
						.getOwnerGoogleID(), swagItem.getOwnerNickName(), swagItem
						.getAverageRating(), swagItem.getNumberOfRatings(),
				swagItem.getCreated(), swagItem.getLastUpdated(),
				paddedTags,
				toCopiedArrayList(toCommentDTOList(swagItem.getComments())));
	}

	//always want 4 tags present for backing form so add empty strings on the end to get the count up to 4
	protected void padTags(ArrayList<String> tagsArrayList) {
		int numberOfEmptiesToAdd = 4 - tagsArrayList.size();
		for (int i = 0; i < numberOfEmptiesToAdd; i++) {
			tagsArrayList.add("");
		}
	}
}
