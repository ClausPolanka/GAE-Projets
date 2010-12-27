package com.swagswap.web.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.fields.DataSourceDateField;
import com.smartgwt.client.data.fields.DataSourceFloatField;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceImageFileField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

/**
 * A SwagSwapGWT client helper that makes remote calls to ItemServiceGWTWrapper
 * It conforms to the requirements of SwagSwapGWT components (which are a layer on top
 * of the SmartClient javascript lib)
 * 
 * This is inspired by http://code.google.com/p/smartgwt-extensions/source/browse/trunk/src/main/java/com/smartgwt/extensions/gwtrpcds/client/example/SimpleGwtRPCDS.java
 */
public class SmartGWTRPCDataSource extends AbstractGWTRPCDataSource {

	// TODO is singleton what we want here?
	private static SmartGWTRPCDataSource instance = null;

	public static SmartGWTRPCDataSource getInstance() {
		if (instance == null) {
			instance = new SmartGWTRPCDataSource();
		}
		return instance;
	}

	private SmartGWTRPCDataSource() {
		DataSourceField key = new DataSourceIntegerField("key");
		key.setPrimaryKey(true);
	    // AutoIncrement on server.
		key.setRequired (false);
        addField (key);

        addField(new DataSourceTextField("name", "Name", 30, true));
        addField(new DataSourceTextField("company", "Company", 30, false));
        addField(new DataSourceTextField("description", "Description", 30, false));
		
		DataSourceImageField imageField = new DataSourceImageField("imageKey", "Image");
		imageField.setImageURLPrefix("/springmvc/showThumbnail/");
		addField(imageField);
		
		addField(new DataSourceTextField("ownerGoogleID", "Owner", 20, false));
		addField(new DataSourceTextField("ownerNickName", "Owner Nick Name", 30, false));
		addField(new DataSourceFloatField("averageRating", "Avg Rating", 5, false));
		addField(new DataSourceIntegerField("numberOfRatings", "No. Ratings", 5, false));
		addField(new DataSourceDateField("created", "Created", 10, false));
		addField(new DataSourceDateField("lastUpdated", "Updated", 10, false));
		addField(new DataSourceTextField("tag1", "Tag 1", 30, false));
		addField(new DataSourceTextField("tag2", "Tag 2", 30, false));
		addField(new DataSourceTextField("tag3", "Tag 3", 30, false));
		addField(new DataSourceTextField("tag4", "Tag 4", 30, false));
		addField(new DataSourceImageFileField("newImageBytes", "New Swag Image", 20, false));
	}

	@Override
	protected void executeFetch(final String requestId,
			final DSRequest request, final DSResponse response) {
		ItemServiceGWTWrapperAsync service = GWT
				.create(ItemServiceGWTWrapper.class);
		service.fetch(new AsyncCallback<List<SwagItemGWTDTO>>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}
			public void onSuccess(List<SwagItemGWTDTO> result) {
				int size = result.size();
				// Create list for return - it is just requested records
				TileRecord[] list = new TileRecord[size];
				if (size > 0) {
					for (int i = 0; i < result.size(); i++) {
						TileRecord record = new TileRecord();
						copyValues(result.get(i), record);
						list[i] = record;
					}
				}
				response.setData(list);
				response.setTotalRows(result.size());
				processResponse(requestId, response);
			}
		});
	}
	
	@Override
	protected void executeAdd(final String requestId, final DSRequest request,
			final DSResponse response) {
		// Retrieve record which should be added.
		JavaScriptObject data = request.getData();
		TileRecord rec = new TileRecord(data);
		SwagItemGWTDTO testRec = new SwagItemGWTDTO();
		copyValues(rec, testRec);
		ItemServiceGWTWrapperAsync service = GWT
				.create(ItemServiceGWTWrapper.class);
		
		//get newImageBytes
		//TODO figure out where request.getUploadedFile is
//		Object newImageBytes = request.getAttribute("newImageBytes");
		
		service.add(testRec, new AsyncCallback<SwagItemGWTDTO>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}

			public void onSuccess(SwagItemGWTDTO result) {
				TileRecord[] list = new TileRecord[1];
				TileRecord newRec = new TileRecord();
				copyValues(result, newRec);
				list[0] = newRec;
				response.setData(list);
				processResponse(requestId, response);
			}
		});
	}

	@Override
	protected void executeUpdate(final String requestId,
			final DSRequest request, final DSResponse response) {
		// Retrieve record which should be updated.
		// Next line would be nice to replace with line:
		// TileRecord rec = request.getEditedRecord ();
		TileRecord rec = getEditedRecord(request);
		SwagItemGWTDTO testRec = new SwagItemGWTDTO();
		copyValues(rec, testRec);
		ItemServiceGWTWrapperAsync service = GWT
				.create(ItemServiceGWTWrapper.class);
		
		//Just do a fetch to refresh the item
		if (testRec.isFetchOnly()) {
			service.fetch(testRec.getKey(),new UpdateOrFetchCallback(requestId, response));
		}
		else { //really do an update
			service.update(testRec, new UpdateOrFetchCallback(requestId, response));
		}
		
	}

	/**
	 * 
	 * Used to trick the browser cache
	 *
	 */
	final class UpdateOrFetchCallback implements AsyncCallback<SwagItemGWTDTO> {
		private final String requestId;
		private final DSResponse response;

		private UpdateOrFetchCallback(String requestId, DSResponse response) {
			this.requestId = requestId;
			this.response = response;
		}
		
		public void onFailure(Throwable caught) {
			throw new RuntimeException(caught);
		}
		
		public void onSuccess(SwagItemGWTDTO result) {
			TileRecord[] list = new TileRecord[1];
			TileRecord updRec = new TileRecord();
			//Trick the cache so that the image updates in the TileGrid
			result.setImageKey(appendRandomQueryString(result.getImageKey()));
			copyValues(result, updRec);
			list[0] = updRec;
			response.setData(list);
			processResponse(requestId, response);
		}
	}
	
	@Override
	protected void executeRemove(final String requestId,
			final DSRequest request, final DSResponse response) {
		// Retrieve record which should be removed.
		JavaScriptObject data = request.getData();
		final TileRecord rec = new TileRecord(data);
		SwagItemGWTDTO testRec = new SwagItemGWTDTO();
		copyValues(rec, testRec);
		ItemServiceGWTWrapperAsync service = GWT
				.create(ItemServiceGWTWrapper.class);
		service.remove(testRec, new AsyncCallback<Object>() {
			public void onFailure(Throwable caught) {
				response.setStatus(RPCResponse.STATUS_FAILURE);
				processResponse(requestId, response);
			}

			public void onSuccess(Object result) {
				TileRecord[] list = new TileRecord[1];
				// We do not receive removed record from server.
				// Return record from request.
				list[0] = rec;
				response.setData(list);
				processResponse(requestId, response);
			}

		});
	}

	/**
	 * Remove cache tricking side effect QueryString from imageKey 
	 * see above: result.setImageKey(appendRandomQueryString(result.getImageKey()));
	 * @param imageKey
	 * @return imageKey without cache trick QueryString
	 */
	public static String removeQueryString(String imageKey) {
		// if it's a new item there is no imageKey
		if (imageKey==null) {
			return null;
		}
		Integer queryStringSuffix = imageKey.indexOf("?");
		if (queryStringSuffix == -1) {
			queryStringSuffix = imageKey.length();
		}
		String imagekeyNoQueryString = imageKey.substring(0, queryStringSuffix);
		return imagekeyNoQueryString;
	}
	
	/**
	 * Used to trick the browser cache
	 * @param imageKey
	 * @return imageKey with Random QueryString
	 */
	public static String appendRandomQueryString(String imageKey) {
		String imageKeyNoQueryString = removeQueryString(imageKey);
		if (imageKeyNoQueryString==null) {
			return null;
		}
		String imageKeyWithRandomQueryString = imageKeyNoQueryString + "?" + Random.nextInt();
		return imageKeyWithRandomQueryString;
	}
	
	public static void copyValues(TileRecord from, SwagItemGWTDTO to) {
		//key is null if adding
		to.setKey((from.getAttributeAsString("key")==null)?null:Long.valueOf(from.getAttributeAsString("key")));
		to.setOwnerGoogleID(from.getAttributeAsString("ownerGoogleID"));
		to.setName(from.getAttributeAsString("name"));
		to.setCompany(from.getAttributeAsString("company"));
		to.setDescription(from.getAttributeAsString("description"));
		to.setImageKey(removeQueryString(from.getAttributeAsString("imageKey")));
		to.setAverageRating(from.getAttributeAsFloat("averageRating"));
		to.setNumberOfRatings(from.getAttributeAsInt("numberOfRatings"));
		to.setCreated(from.getAttributeAsDate("created"));
		to.setLastUpdated(from.getAttributeAsDate("lastUpdated"));
		to.setIsFetchOnly(from.getAttributeAsBoolean("isFetchOnly"));
		to.setOwnerNickName(from.getAttributeAsString("ownerNickName"));
		ArrayList<String> tags = new ArrayList<String>();
		tags.add((from.getAttributeAsString("tag1")==null)?"":from.getAttributeAsString("tag1"));
		tags.add((from.getAttributeAsString("tag2")==null)?"":from.getAttributeAsString("tag2"));
		tags.add((from.getAttributeAsString("tag3")==null)?"":from.getAttributeAsString("tag3"));
		tags.add((from.getAttributeAsString("tag4")==null)?"":from.getAttributeAsString("tag4"));
		to.setTags(tags);
		//don't need comments
		to.setNewImageURL(from.getAttributeAsString("newImageURL"));
	}

	public static void copyValues(SwagItemGWTDTO from, TileRecord to) {
		to.setAttribute("key", from.getKey());
		to.setAttribute("ownerGoogleID", from.getOwnerGoogleID());
		to.setAttribute("ownerNickName", from.getOwnerNickName());
		to.setAttribute("name", from.getName());
		to.setAttribute("company", from.getCompany());
		to.setAttribute("description", from.getDescription());
		to.setAttribute("imageKey", from.getImageKey());
		to.setAttribute("averageRating", from.getAverageRating());
		to.setAttribute("numberOfRatings", from.getNumberOfRatings());
		to.setAttribute("created", from.getCreated());
		to.setAttribute("lastUpdated", from.getLastUpdated());
		to.setAttribute("tag1", from.getTags().get(0));
		to.setAttribute("tag2", from.getTags().get(1));
		to.setAttribute("tag3", from.getTags().get(2));
		to.setAttribute("tag4", from.getTags().get(3));
		to.setAttribute("comments", from.getComments());
		//newImageURL not needed here
	}

	private TileRecord getEditedRecord(DSRequest request) {
		// Retrieving values before edit
		JavaScriptObject oldValues = request
				.getAttributeAsJavaScriptObject("oldValues");
		// Creating new record for combining old values with changes
		TileRecord newRecord = new TileRecord();
		// Copying properties from old record
		JSOHelper.apply(oldValues, newRecord.getJsObj());
		// Retrieving changed values
		JavaScriptObject data = request.getData();
		// Apply changes
		JSOHelper.apply(data, newRecord.getJsObj());
		return newRecord;
	}
}
