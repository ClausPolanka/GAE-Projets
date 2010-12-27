package com.swagswap.web.gwt.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSCallback;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.XJSONDataSource;
import com.smartgwt.client.data.fields.DataSourceImageField;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Encoding;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.ImageStyle;
import com.smartgwt.client.types.OperatorId;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.RichTextEditor;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.SearchForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.events.SubmitValuesEvent;
import com.smartgwt.client.widgets.form.events.SubmitValuesHandler;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.HiddenItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.SubmitItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.UploadItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tile.TileGrid;
import com.smartgwt.client.widgets.tile.TileRecord;
import com.smartgwt.client.widgets.tile.events.RecordClickEvent;
import com.smartgwt.client.widgets.tile.events.RecordClickHandler;
import com.smartgwt.client.widgets.viewer.DetailFormatter;
import com.smartgwt.client.widgets.viewer.DetailViewerField;
import com.smartgwt.client.widgets.viewer.DetailViewerRecord;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.web.gwt.client.domain.CommentRecord;
import com.swagswap.web.gwt.client.domain.LoginInfo;
import com.swagswap.web.gwt.client.domain.SwagItemCommentGWTDTO;
import com.swagswap.web.gwt.client.domain.SwagItemGWTDTO;

public class SwagSwapGWT implements EntryPoint {

	//services
	private final ItemServiceGWTWrapperAsync itemService = GWT
		.create(ItemServiceGWTWrapper.class);
	private LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	//client state
	private LoginInfo loginInfo; //null if they're not logged in

	//global GUI objects TODO: can scope be reduced?
	final TileGrid itemsTileGrid = new TileGrid();
	protected HStack editFormHStack;
	private DynamicForm boundSwagForm;
	private Img currentSwagImage;
	private List<Img> starImages = new ArrayList<Img>();
	private StarClickHandler starClickHandler1;
	private StarClickHandler starClickHandler2;
	private StarClickHandler starClickHandler3;
	private StarClickHandler starClickHandler4;
	private StarClickHandler starClickHandler5;
	private HStack starHStack;
	private DynamicForm uploadForm;
	private HLayout editButtonsLayout;
	private ButtonItem imFeelingLuckyButton;
	private ListGrid commentsGrid;
	private VStack commentsFormVStack;
	private RichTextEditor richTextCommentsEditor;
	private DateTimeFormat dateFormatter = DateTimeFormat.getFormat("dd-MM-yy HH:mm");
	private Label itemEditTitleLabel;
	private TabSet tabSet;
	private Tab commentsTab;
	private DynamicForm sortForm;
	
	/**
	 * Check login status and build GUI
	 */
	public void onModuleLoad() {
		// Set GWT container invisible
		DOM.setInnerHTML(RootPanel.get("loadingMsg").getElement(),"Checking logged-in-edness");
		// get better exception handling
		setUncaughtExceptionHandler();
		
		loginService.login(GWT.getHostPageBaseURL() + "SwagSwapGWT.html",
			new AsyncCallback<LoginInfo>() {
				public void onFailure(Throwable error) {
					throw new RuntimeException("can't reach SwagSwap server", error);
				}
				public void onSuccess(LoginInfo result) {
					loginInfo = result;
					DOM.setInnerHTML(RootPanel.get("loadingMsg").getElement(),"Fetching Swag Items");
					buildGUI();
				}
			});
	}
	
	/**
	 * Create Menu, items grid, edit, and comment screens
	 */
	public void buildGUI() {
		//Top menu
		HStack menuHStack= new HStack();
		menuHStack.setHeight(25);
		menuHStack.setWidth(200);
		menuHStack.addMember(createLoginLogoutPanel());
		 
		//The rest
		VStack swagItemsVStack = new VStack();
		swagItemsVStack.addMember(createSearchBox());
		swagItemsVStack.addMember(createSortDropDown());
		swagItemsVStack.addMember(createItemsTileGrid());
		
		//hide app until data has loaded
		DOM.setStyleAttribute(RootPanel.get("gwtApp").getElement(), "display", "none");
		itemsTileGrid.fetchData(null, new DSCallback() {
			public void execute(DSResponse response, Object rawData, DSRequest request) {
				//hide loading div and it's border
				DOM.setInnerHTML(RootPanel.get("loading").getElement(),"");
				DOM.setStyleAttribute(DOM.getElementById("loading"), "border", "0");
				//show app
				DOM.setStyleAttribute(RootPanel.get("gwtApp").getElement(), "display", "block");
			}
		});
		
		swagItemsVStack.setWidth(350);
		swagItemsVStack.setHeight(552);
		swagItemsVStack.setBorder("1px solid #C0C3C7"); //blue like the rest of the app
		swagItemsVStack.setShowEdges(false);
		swagItemsVStack.setCanDragResize(true);
		swagItemsVStack.setShowResizeBar(true);
		
		//Put itemsTileGrid next to createEditComments
		HStack itemsEditCommentsHStack = new HStack();
		itemsEditCommentsHStack.addMember(swagItemsVStack);
//		addImageUpload(itemsAndEditHStack);
		itemsEditCommentsHStack.addMember(createEditForm());
		itemsEditCommentsHStack.setHeight(720);
		
		VStack mainStack = new VStack();
		//vertical scrolling if browser window is too small
		mainStack.setOverflow(Overflow.AUTO); 
		mainStack.setWidth100();
		mainStack.setHeight100();
		mainStack.addMember(menuHStack);
		mainStack.addMember(itemsEditCommentsHStack);
		
		RootPanel.get("gwtApp").add(mainStack); //anchored on GWT html page

		mainStack.draw();
	}

	/**
	 * SwagSwapGWT filter implementation.  Shows search box, executes
	 * filter on keyPress which client-side searches a few fields of swagItems
	 */
	private DynamicForm createSearchBox() {
		final DynamicForm filterForm = new DynamicForm();
		filterForm.setNumCols(4);
		filterForm.setAlign(Alignment.LEFT);
		filterForm.setAutoFocus(false);
		filterForm.setWidth("59%"); //make it line up with sort box
		
		filterForm.setDataSource(SmartGWTRPCDataSource.getInstance());
		filterForm.setOperator(OperatorId.OR);
		
		//Visible search box
		TextItem nameItem = new TextItem("name", "Search");
		nameItem.setAlign(Alignment.LEFT);
		nameItem.setOperator(OperatorId.ICONTAINS); // case insensitive
		
		//The rest are hidden and populated with the contents of nameItem
		final HiddenItem companyItem = new HiddenItem("company");
		companyItem.setOperator(OperatorId.ICONTAINS);
		final HiddenItem ownerItem = new HiddenItem("ownerNickName");
		ownerItem.setOperator(OperatorId.ICONTAINS);

		filterForm.setFields(nameItem,companyItem,ownerItem);
		
		filterForm.addItemChangedHandler(new ItemChangedHandler() {
			public void onItemChanged(ItemChangedEvent event) {
				String searchTerm = filterForm.getValueAsString("name");
				companyItem.setValue(searchTerm);
				ownerItem.setValue(searchTerm);
				if (searchTerm==null) {
					itemsTileGrid.fetchData();
				}
				else {
					Criteria criteria = filterForm.getValuesAsCriteria();
					itemsTileGrid.fetchData(criteria);
				}
			}
		});
		return filterForm;
	}
	
	/**
	 * SwagSwapGWT-specific sort.  Sorts itemsTileGrid
	 * 
	 * See http://www.smartclient.com/smartgwt/showcase/#featured_tile_filtering
	 */
	@SuppressWarnings("unchecked")
	private DynamicForm createSortDropDown() {
		sortForm = new DynamicForm();
		//styling
		sortForm.setNumCols(4); //2 labels + two inputs
		sortForm.setAutoFocus(false);
		sortForm.setWrapItemTitles(false);
		sortForm.setWidth("80%");

		SelectItem sortItem = new SelectItem("sortBy", "Sort By");

		LinkedHashMap valueMap = new LinkedHashMap();
		valueMap.put("name", "Name");
		valueMap.put("company", "Company");
		valueMap.put("averageRating", "Avg Rating");
		valueMap.put("ownerNickName", "Owner");
		valueMap.put("lastUpdated", "Last Updated");

		sortItem.setValueMap(valueMap);
		sortItem.setValue("lastUpdated"); //default TODO make sure it sort this way to start!

		final CheckboxItem ascendingItem = new CheckboxItem("chkSortDir");
		ascendingItem.setTitle("Ascending");

		sortForm.setFields(sortItem, ascendingItem);

		sortForm.addItemChangedHandler(new ItemChangedHandler() {
			public void onItemChanged(ItemChangedEvent event) {
				doSort();
			}
		});
		return sortForm;
	}
	
	private void doSort() {
		String sortValue = sortForm.getValueAsString("sortBy");
		Boolean sortDirection = (Boolean) sortForm.getValue("chkSortDir");
		if (sortDirection == null)
			sortDirection = false;
		if (sortValue != null) {
			itemsTileGrid.sortByProperty(sortValue, sortDirection);
		}
	}

	private TileGrid createItemsTileGrid() {
		// build flying swag icons
		
		//styling
		itemsTileGrid.setBorder("1px solid #C0C3C7");
		itemsTileGrid.setTileWidth(100);
		itemsTileGrid.setTileHeight(140);
		itemsTileGrid.setTileValueAlign("left");
		itemsTileGrid.setWidth100();
		itemsTileGrid.setHeight100();
		itemsTileGrid.setAnimateTileChange(true);
		
		itemsTileGrid.setShowAllRecords(true);
		itemsTileGrid.setDataSource(SmartGWTRPCDataSource.getInstance());
		itemsTileGrid.setAutoFetchData(false);

		DetailViewerField imageField = new DetailViewerField("imageKey");
		DetailViewerField name = new DetailViewerField("name");
		DetailViewerField company = new DetailViewerField("company");
		DetailViewerField ownerNickName = new DetailViewerField("ownerNickName");
		DetailViewerField averageRating = new DetailViewerField("averageRating");
		//show star images for the average rating
		averageRating.setDetailFormatter(new DetailFormatter() {
			public String format(Object value, DetailViewerRecord record,
					DetailViewerField field) {
					int averageRating = record.getAttributeAsInt("averageRating");
					int numberOfRatings = record.getAttributeAsInt("numberOfRatings");
				return createStarsHTMLString(averageRating) + " / " + numberOfRatings;
			}
		});
		
		DetailViewerField lastUpdated = new DetailViewerField("lastUpdated");
		lastUpdated.setDetailFormatter(new DetailFormatter()  {
			public String format(Object value, DetailViewerRecord record,
					DetailViewerField field) {
					Date lastUpdated = record.getAttributeAsDate("lastUpdated");
			        if(lastUpdated == null) return null;        
			        return dateFormatter.format(lastUpdated);
			}
		}
		);
		itemsTileGrid.setFields(imageField, name, company ,ownerNickName, 
				averageRating, lastUpdated);
		itemsTileGrid.addRecordClickHandler(new RecordClickHandler() {
			public void onRecordClick(RecordClickEvent event) {
				SwagItemGWTDTO dto = new SwagItemGWTDTO();
				SmartGWTRPCDataSource.copyValues(event.getRecord(),dto);
				prepareAndShowEditForm(event.getRecord());
			}
		});
		return itemsTileGrid;
	}
	
/*	public void addImageUpload(HStack hStack) {
		//create a hidden frame
		Canvas iframe = new Canvas();
		iframe.setID("fileUploadFrame");
		iframe.setContents("<IFRAME NAME=\"fileUploadFrame\" style=\"width:0;height:0;border:0\"></IFRAME>");
		iframe.setVisibility(Visibility.VISIBLE); //Could not get the IFRAME in the page with Visibility HIDDEN
		uploadForm = new DynamicForm();
		uploadForm.setNumCols(4);
		uploadForm.setTarget("fileUploadFrame"); //set target of FORM to the IFRAME
		uploadForm.setEncoding(Encoding.MULTIPART);
		uploadForm.setAction(GWT.getModuleBaseURL()+"TODO-fileuploadservlet");

		//creates a HTML formitem with a textfield and a browse button
		final UploadItem uploadItem = new UploadItem("filename","Select a file");
		uploadItem.setWidth(300);
		uploadItem.addChangedHandler(new ChangedHandler() {
			
			public void onChanged(ChangedEvent event) {
				//The item shows the whole (long) path, so set caret at end 
				//so user can verify his chosen filename
				String filename = (String)uploadItem.getValue();
				if (filename != null) uploadItem.setSelectionRange(filename.length(), filename.length());
				
				//Unfortunately UploadItem does not throw a ChangedEvent :(
			}
		});

		SubmitItem submitItem = new SubmitItem("upload", "Upload");
		submitItem.setStartRow(false);
		submitItem.setEndRow(false);

		uploadForm.setItems(uploadItem, submitItem);

		uploadForm.addSubmitValuesHandler(new SubmitValuesHandler() {
			
			public void onSubmitValues(SubmitValuesEvent event) {
				//maybe do some filename verification
				//String filename = (String)uploadItem.getValue();
				uploadForm.submitForm();
			}
		});

		hStack.addMember(uploadForm);
		hStack.addMember(iframe);
	}*/
	
	public String createStarsHTMLString(float rating) {
		int roundedRating = Math.round(rating);
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 5; i++) {
			s.append((i<roundedRating)?"<img src=\"images/starOn.gif\"/>":"<img src=\"images/starOff.gif\"/>");
		}
		return s.toString();
	}
	
	private HStack createLoginLogoutPanel() {
		HStack loginPanel = new HStack();
		loginPanel.setHeight(20);
		HStack logoutPanel = new HStack();
		logoutPanel.setHeight(20);
		
		Label homeLabel = new Label("Home");
		homeLabel.setPrompt("Go back to the SwagSwap home page");
		homeLabel.setStyleName("menu");
		homeLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open("/", "_self", ""); 
			}
		});
		Label signOutLabel = new Label("Sign Out");
		signOutLabel.setStyleName("menu");
		signOutLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(loginInfo.getLogoutUrl(), "_self", ""); 
			}
		});
		Label signInLabel = new Label("Sign In");
		signInLabel.setPrompt("Sign in to your Google Account to add or rate items. Your email will remain confidential");
		signInLabel.setStyleName("menu");
		signInLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Window.open(loginInfo.getLoginUrl(), "_self", ""); 
			}
		});
		Label addSwagLabel = new Label("Add Swag");
		addSwagLabel.setPrompt("Add a Swag Item to SwagSwap!");
		addSwagLabel.setStyleName("menu");
		addSwagLabel.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				itemEditTitleLabel.setIcon(""); 
				itemEditTitleLabel.setContents("<b>Add Swag</b>");
				currentSwagImage.setSrc("/images/no_photo.jpg");
				starHStack.hide();
				editFormHStack.show();
				commentsFormVStack.hide(); //no comments on add
				tabSet.setTabTitle(0,"Add Item");
				if (tabSet.getTab(1) != null) {
					tabSet.removeTab(commentsTab);
				}
				itemsTileGrid.deselectAllRecords();
				boundSwagForm.enable();
				editButtonsLayout.show();
				imFeelingLuckyButton.show();
				
				boundSwagForm.getField("name").focusInItem();
				boundSwagForm.editNewRecord();
			}
		});
		if (loginInfo.isLoggedIn()) {
			logoutPanel.addMember(homeLabel);
			logoutPanel.addMember(addSwagLabel);
			Label welcomeLabel = new Label("Welcome: " + loginInfo.getNickName());
			welcomeLabel.setWrap(false);
			logoutPanel.addMember(signOutLabel);
			logoutPanel.addMember(welcomeLabel);
			return logoutPanel;
		} else { //not logged in
			loginPanel.addMember(homeLabel);
			loginPanel.addMember(signInLabel);
			return loginPanel;
		}
	}
	
	/**
	 * For adding or editing a swagItem
	 */
	private HStack createEditForm() {
		editFormHStack = new HStack();
		
		VStack editFormVStack = new VStack();
		editFormVStack.addMember(addStarRatings());
		
		boundSwagForm = new DynamicForm();
		boundSwagForm.setNumCols(2);
//		boundSwagForm.setLongTextEditorThreshold(40);
		boundSwagForm.setDataSource(SmartGWTRPCDataSource.getInstance());
		boundSwagForm.setAutoFocus(true);

		HiddenItem keyItem = new HiddenItem("key");
		TextItem nameItem = new TextItem("name");
		nameItem.setLength(50);
		nameItem.setSelectOnFocus(true);
		TextItem companyItem = new TextItem("company");
		companyItem.setLength(20);
		TextItem descriptionItem = new TextItem("description");
		descriptionItem.setLength(100);
		TextItem tag1Item = new TextItem("tag1");
		tag1Item.setLength(15);
		TextItem tag2Item = new TextItem("tag2");
		tag2Item.setLength(15);
		TextItem tag3Item = new TextItem("tag3");
		tag3Item.setLength(15);
		TextItem tag4Item = new TextItem("tag4");
		tag4Item.setLength(15);
		
		StaticTextItem isFetchOnlyItem = new StaticTextItem("isFetchOnly");
		isFetchOnlyItem.setVisible(false);
		StaticTextItem imageKeyItem = new StaticTextItem("imageKey");
		imageKeyItem.setVisible(false);
		
		TextItem newImageURLItem = new TextItem("newImageURL");

		boundSwagForm.setFields(keyItem, nameItem, companyItem, descriptionItem, tag1Item,
				tag2Item, tag3Item, tag4Item, isFetchOnlyItem, imageKeyItem, newImageURLItem);
		editFormVStack.addMember(boundSwagForm);
		
		currentSwagImage = new Img("/images/no_photo.jpg");  
		currentSwagImage.setImageType(ImageStyle.NORMAL); 
		editFormVStack.addMember(currentSwagImage);
		editFormVStack.addMember(createImFeelingLuckyImageSearch());
		
		IButton saveButton = new IButton("Save");
		saveButton.setAutoFit(true);
		saveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				//TODO
				//uploadForm.submitForm();
				//Turn off fetch only (could have been on from them rating the item
				boundSwagForm.getField("isFetchOnly").setValue(false);
				boundSwagForm.saveData();
				handleSubmitComment(); //in case they commented while editing
				//re-sort
				doSort();
				if (boundSwagForm.hasErrors()) {
					Window.alert("" + boundSwagForm.getErrors());
				} else {
					boundSwagForm.clearValues();
					
					editFormHStack.hide();
				}
			}
		});
		IButton cancelButton = new IButton("Cancel");
		cancelButton.setAutoFit(true);
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				boundSwagForm.clearValues();
				editFormHStack.hide();
			}
		});
		
		IButton deleteButton = new IButton("Delete");
		deleteButton.setAutoFit(true);
		deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showConfirmRemovePopup(itemsTileGrid.getSelectedRecord());
				editFormHStack.hide();
			}
		});
		
		editButtonsLayout = new HLayout();
		editButtonsLayout.setHeight(20);
		editButtonsLayout.addMember(saveButton);
		editButtonsLayout.addMember(cancelButton);
		editButtonsLayout.addMember(deleteButton);
		
		editFormVStack.addMember(editButtonsLayout);
		
		tabSet = new TabSet();
		tabSet.setDestroyPanes(false);
        tabSet.setTabBarPosition(Side.TOP);
        tabSet.setTabBarAlign(Side.LEFT);
        tabSet.setWidth(570);
        tabSet.setHeight(570);
        

        Tab viewEditTab = new Tab();
        viewEditTab.setPane(editFormVStack);

        commentsTab = new Tab("Comments");
        commentsTab.setPane(createComments());
        
        tabSet.addTab(viewEditTab);
        tabSet.addTab(commentsTab);
        //put focus in commentsEditor when they click the Comments tab
        tabSet.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				Tab selectedTab = tabSet.getSelectedTab();
				if (commentsTab==selectedTab) {
					richTextCommentsEditor.focus();
				}
			}
		});
        
        VStack tabsVStack = new VStack();
        itemEditTitleLabel = new Label();  
        itemEditTitleLabel.setHeight(30);  
        itemEditTitleLabel.setAlign(Alignment.LEFT);  
        itemEditTitleLabel.setValign(VerticalAlignment.TOP);  
        itemEditTitleLabel.setWrap(false);  
        tabsVStack.addMember(itemEditTitleLabel);
        //make sure this is drawn since we set the tab names early
        tabSet.draw();
        tabsVStack.addMember(tabSet);
		editFormHStack.addMember(tabsVStack);
		editFormHStack.hide();
		return editFormHStack;
	}
	
    private void showConfirmRemovePopup(final TileRecord selectedTileRecord) {
    	final com.smartgwt.client.widgets.Window winModal = new com.smartgwt.client.widgets.Window();
		winModal.setWidth(360);
		winModal.setHeight(115);
		winModal.setTitle("Confirm Delete");
		winModal.setShowMinimizeButton(false);
		winModal.setIsModal(true);
		winModal.setShowModalMask(true);
		winModal.centerInPage();
		winModal.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClientEvent event) {
				winModal.destroy();
			}
		});
    	
		VLayout vLayout = new VLayout();
		String recordName = selectedTileRecord.getAttributeAsString("name");
		Label confirmText = new Label("Are you sure you want to delete " + recordName + "?");
		confirmText.setHeight(50);
		vLayout.addMember(confirmText);
		
		IButton yesButton = new IButton("Yes");
		yesButton.setAutoFit(true);
		yesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				itemsTileGrid.removeData(selectedTileRecord);
				winModal.hide();
			}
		});
		IButton cancelButton = new IButton("Cancel");
		cancelButton.setAutoFit(true);
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				winModal.hide();
			}
		});
		HLayout buttonsLayout = new HLayout();
		buttonsLayout.addMember(yesButton);
		buttonsLayout.addMember(cancelButton);
		
		vLayout.addMember(buttonsLayout);
		
		winModal.addItem(vLayout);
		winModal.show();
    }
    
	private VStack createComments() {
		commentsFormVStack = new VStack();

		richTextCommentsEditor = new RichTextEditor();
		richTextCommentsEditor.setHeight(100);
		richTextCommentsEditor.setWidth(530);
		richTextCommentsEditor.setOverflow(Overflow.HIDDEN);
		richTextCommentsEditor.setCanDragResize(true);
		richTextCommentsEditor.setBorder("1px solid #C0C3C7");

		IButton saveCommentButton = new IButton();
		saveCommentButton.setTitle("Save Comment");
		saveCommentButton.setAutoFit(true); //make it stretch to the width of the button
		saveCommentButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				handleSubmitComment();
			}
		});
		
		commentsGrid = new ListGrid();
		commentsGrid.setWrapCells(true);
		commentsGrid.setFixedRecordHeights(false);
		commentsGrid.setWidth(530);
		commentsGrid.setHeight(390);
		commentsGrid.setShowAllRecords(true);
		commentsGrid.setCanEdit(false);

		ListGridField nickNameField = new ListGridField("swagSwapUserNickname",
				"Nickname", 100);
		ListGridField commentField = new ListGridField("commentText", "Comment");
		ListGridField dateField = new ListGridField("created", "Date", 70);
		dateField.setCellFormatter(new CellFormatter() {
			public String format(Object value, ListGridRecord record,
					int rowNum, int colNum) {
				if (value==null) {
					return null;
				}
				return dateFormatter.format((Date)value);
			}
		});
		commentsGrid.setFields(new ListGridField[] { nickNameField,
				commentField, dateField });

		commentsFormVStack.addMember(richTextCommentsEditor);
		commentsFormVStack.addMember(saveCommentButton);
		Label commentsLabel = new Label("Comments:");
		commentsLabel.setHeight(20);
		commentsFormVStack.addMember(commentsLabel);
		
		VStack commentsFormAndCommentsVStack = new VStack();
		commentsFormAndCommentsVStack.addMember(commentsFormVStack);
		commentsFormAndCommentsVStack.addMember(commentsGrid);
		commentsFormVStack.hide();
		commentsGrid.hide();
		return commentsFormAndCommentsVStack;
	}
	
	private void handleSubmitComment() {
		String comment = richTextCommentsEditor.getValue();
		final Long currentItemKey = (Long)boundSwagForm.getField("key").getValue();
		SwagItemCommentGWTDTO newComment = new SwagItemCommentGWTDTO(
				currentItemKey,
				loginInfo.getGoogleID(),
				loginInfo.getNickName(),
				comment,
				null);
		itemService.addComment(
				newComment,
				new AsyncCallback<Object>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("", caught);
					}
					@Override
					public void onSuccess(Object result) {
						refreshComments(currentItemKey);
						refreshItem();
					}
				});
	}
	
	private HStack addStarRatings() {
		//add five stars
		for (int i = 0; i < 5; i++) {
			starImages.add(new Img("/images/starOff.gif",12,12));
		}
		
		starClickHandler1 = new StarClickHandler();
		starClickHandler2 = new StarClickHandler();
		starClickHandler3 = new StarClickHandler();
		starClickHandler4 = new StarClickHandler();
		starClickHandler5 = new StarClickHandler();
		
		starImages.get(0).addClickHandler(starClickHandler1);
		starImages.get(1).addClickHandler(starClickHandler2);
		starImages.get(2).addClickHandler(starClickHandler3);
		starImages.get(3).addClickHandler(starClickHandler4);
		starImages.get(4).addClickHandler(starClickHandler5);
		
		starHStack = new HStack();
		starHStack.setHeight(15);
		starHStack.setAlign(Alignment.LEFT);
		Label label = new Label("My Rating: ");
		starHStack.addMember(label);
		starHStack.addMember(starImages.get(0));
		starHStack.addMember(starImages.get(1));
		starHStack.addMember(starImages.get(2));
		starHStack.addMember(starImages.get(3));
		starHStack.addMember(starImages.get(4));
		return starHStack;
	}
	
	public class StarClickHandler implements ClickHandler {
		private SwagItemRating newRating;
		public void setSwagItemRating(SwagItemRating swagItemRating) {
			newRating = swagItemRating;
		}
		@Override
		public void onClick(ClickEvent event) {
			//send them to login page if not logged in
			if (!loginInfo.isLoggedIn()) {
				Window.open(loginInfo.getLoginUrl(), "_self", ""); 
			}
			else { //logged in
				loginService.addOrUpdateRating(loginInfo.getEmail(), 
					newRating,
					new AsyncCallback() {
						public void onFailure(Throwable error) {
							GWT.log("", error);
						}
						@Override
						public void onSuccess(Object result) {
							//refresh client side userRating
							SwagItemRating previousRating = loginInfo.getSwagItemRating(newRating.getSwagItemKey());
							if (previousRating!=null) {
								loginInfo.getSwagItemRatings().remove(previousRating);
							}
							loginInfo.getSwagItemRatings().add(newRating);
							
							//update stars
							updateUserRatingStars(newRating.getSwagItemKey());
							
							refreshItem();
						}
				}
				);
			}
		}
	}
	
	private void prepareAndShowEditForm(TileRecord tileRecord) {
		// Make read only if they don't have permission
		String ownerGoogleID = tileRecord.getAttribute("ownerGoogleID");
		String currentGoogleID = loginInfo.getGoogleID();
		//edit
		if (loginInfo.isUserAdmin() || 
				(loginInfo.isLoggedIn() && currentGoogleID.equals(ownerGoogleID))) {
			boundSwagForm.enable();
			editButtonsLayout.show();
			imFeelingLuckyButton.show();
			tabSet.setTabTitle(0,"Edit Item");
		}
		//view
		else {
			boundSwagForm.disable();
			editButtonsLayout.hide();
			imFeelingLuckyButton.hide();
			tabSet.setTabTitle(0,"View Item");
		}
		tabSet.selectTab(0); //put it on the view/edit tab
		itemEditTitleLabel.setIcon("/springmvc/showThumbnail/" + tileRecord.getAttribute("imageKey")); 
		String itemName = tileRecord.getAttribute("name");
		String ownerNickName = tileRecord.getAttribute("ownerNickName");
		Date lastUpdated = tileRecord.getAttributeAsDate("lastUpdated");
		itemEditTitleLabel.setContents("<b>" + itemName + "</b> posted by: <b>"
				+ ownerNickName + "</b> (" + dateFormatter.format(lastUpdated) + ")");
		boundSwagForm.editRecord(tileRecord);
		currentSwagImage.setSrc("/springmvc/showImage/" + tileRecord.getAttribute("imageKey"));  
		currentSwagImage.setWidth(283);
		currentSwagImage.setHeight(212);
		
		//show commentsTab if it's been removed
		if (tabSet.getTab(1) == null) {
			tabSet.addTab(commentsTab);
		}
		
		Long currentKey = Long.valueOf(tileRecord.getAttribute("key"));
		starClickHandler1.setSwagItemRating(new SwagItemRating(currentKey,1));
		starClickHandler2.setSwagItemRating(new SwagItemRating(currentKey,2));
		starClickHandler3.setSwagItemRating(new SwagItemRating(currentKey,3));
		starClickHandler4.setSwagItemRating(new SwagItemRating(currentKey,4));
		starClickHandler5.setSwagItemRating(new SwagItemRating(currentKey,5));
		
		updateUserRatingStars(currentKey);
		
		//get fresh comments
		refreshComments(currentKey);
		starHStack.show();
		editFormHStack.show();
	}

	private void refreshComments(Long currentKey) {
		richTextCommentsEditor.setValue(""); //clear Add Comment box
		commentsGrid.hide();
		itemService.fetch(currentKey, new AsyncCallback<SwagItemGWTDTO>() {
			@Override
			public void onSuccess(SwagItemGWTDTO result) {
				//result is null on an add
				if (result==null) {
					return;
				}
				List<SwagItemCommentGWTDTO> comments = result.getComments();
				commentsGrid.setData(toCommentRecords(comments));
				commentsGrid.show();
				if (loginInfo.isLoggedIn()) {
					commentsFormVStack.show();
				}
				else { //not logged in
					commentsFormVStack.hide();
				}
			}
			@Override
			public void onFailure(Throwable error) {
				GWT.log("", error);
			}
		}
		);
	}

	/**
	 * Turn this into something the SwagSwapGWT Grid can use
	 * @param comments
	 * @return CommentRecord[]
	 */
	private CommentRecord[] toCommentRecords(List<SwagItemCommentGWTDTO> comments) {
		CommentRecord[] recordArray = new CommentRecord[comments.size()];
		for (int i = 0; i < comments.size(); i++) {
			recordArray[i]=new CommentRecord(comments.get(i));
		}
		return recordArray;
	}

	/**
	 * handle current userRating star colors
	 * @param currentKey
	 */
	private void updateUserRatingStars(Long currentKey) {
		SwagItemRating swagItemRatingForKey = loginInfo.getSwagItemRating(currentKey);
		Integer userRating = (swagItemRatingForKey==null) ? 0 : swagItemRatingForKey.getUserRating();
		
		for (int i = 0; i < 5; i++) {
			if (i<userRating) {
				starImages.get(i).setSrc("/images/starOn.gif");
			}
			else {
				starImages.get(i).setSrc("/images/starOff.gif");
			}
		}
	}
	
	/**
	 * Inspired by http://www.smartclient.com/smartgwt/showcase/#featured_json_integration_category_yahoo
	 * @return ImfeelingLucky search button with backing form
	 */
	private SearchForm createImFeelingLuckyImageSearch() {

		XJSONDataSource yahooDS = new XJSONDataSource();
		yahooDS
				.setDataURL("http://api.search.yahoo.com/ImageSearchService/V1/imageSearch?appid=YahooDemo&output=json");
		yahooDS.setRecordXPath("/ResultSet/Result");
		DataSourceImageField thumbnail = new DataSourceImageField("Thumbnail","Thumbnail");
		thumbnail.setWidth(150);
		thumbnail.setImageHeight("imageHeight");
		thumbnail.setImageWidth("imageWidth");
		thumbnail.setValueXPath("Thumbnail/Url");

		DataSourceIntegerField imageWidth = new DataSourceIntegerField("imageWidth");
		imageWidth.setValueXPath("Thumbnail/Width");
		imageWidth.setAttribute("hidden", true);

		DataSourceIntegerField imageHeight = new DataSourceIntegerField("imageHeight");
		imageHeight.setValueXPath("Thumbnail/Height");
		imageHeight.setAttribute("hidden", true);

		DataSourceField title = new DataSourceField("Title", FieldType.TEXT);
		DataSourceField summary = new DataSourceField("Summary", FieldType.TEXT);

		yahooDS.addField(thumbnail);
		yahooDS.addField(imageWidth);
		yahooDS.addField(imageHeight);
		yahooDS.addField(title);
		yahooDS.addField(summary);

		final ListGrid imageResultsGrid = new ListGrid();
		imageResultsGrid.setTop(120);
		imageResultsGrid.setWidth100();
		imageResultsGrid.setHeight(600);
		imageResultsGrid.setWrapCells(true);
		imageResultsGrid.setFixedRecordHeights(false);
		imageResultsGrid.setShowAllRecords(true);
		imageResultsGrid.setDataSource(yahooDS);

		// search form (which is actually just the button showing)
		final SearchForm imFeelingLuckyForm = new SearchForm();
		imFeelingLuckyForm.setNumCols(1);
		imFeelingLuckyForm.setHeight(50);
		final HiddenItem query = new HiddenItem("query");

		imFeelingLuckyButton = new ButtonItem();
		imFeelingLuckyButton.setTitle("I'm Feeling Lucky Image Search");
		imFeelingLuckyButton.setStartRow(false);
		final com.smartgwt.client.widgets.Window imageSearchResultsWindow = 
			createImFeelingLuckyResults(imageResultsGrid);
		imFeelingLuckyButton
				.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
					public void onClick(
							com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
						
						imageSearchResultsWindow.show();
						String swagItemName = (String) boundSwagForm.getField(
								"name").getValue();
						query.setValue(swagItemName);
						imageResultsGrid.fetchData(imFeelingLuckyForm
								.getValuesAsCriteria());
					}
				});
		imageResultsGrid
				.addRecordClickHandler(new com.smartgwt.client.widgets.grid.events.RecordClickHandler() {
					public void onRecordClick(
							com.smartgwt.client.widgets.grid.events.RecordClickEvent event) {
						Record record = event.getRecord();
						String URL = record.getAttributeAsString("Url");
						boundSwagForm.getField("newImageURL").setValue(URL);
						currentSwagImage.setSrc(URL);
						currentSwagImage.setWidth(283);
						currentSwagImage.setHeight(212);
						imageSearchResultsWindow.hide();
					}
				});

		imFeelingLuckyForm.setItems(query, imFeelingLuckyButton);
		return imFeelingLuckyForm;
	}

	private com.smartgwt.client.widgets.Window createImFeelingLuckyResults(ListGrid grid) {
    	final com.smartgwt.client.widgets.Window winModal = new com.smartgwt.client.widgets.Window();
		winModal.setWidth(600);
		winModal.setHeight(650);
		winModal.setTitle("Select an image for your swag");
		winModal.setShowMinimizeButton(false);
		winModal.setIsModal(true);
		winModal.setShowModalMask(true);
		winModal.centerInPage();
		winModal.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClientEvent event) {
				winModal.hide();
			}
		});
    	
		IButton cancelButton = new IButton("Cancel");
		cancelButton.setAutoFit(true);
		cancelButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				winModal.hide();
			}
		});
		
		winModal.addItem(grid);
		winModal.addItem(cancelButton);
		return winModal;
	}
	
	private void setUncaughtExceptionHandler() {
		// better exception handling
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				if (e.getCause() != null
						&& e.getCause() instanceof StatusCodeException) {
					GWT.log("Exception (server-side): ", e);
					Window.alert("Exception (server-side): " + e.getMessage());
					e.printStackTrace();
				} else {
					GWT.log("Exception (client-side): ", e);
					Window.alert("Exception (client-side): " + e.getMessage());
					e.printStackTrace();
				}
			}
		});
	}

	private void refreshItem() {
		//refresh item
		//kludge to execute a fetch through saveData()
		boundSwagForm.getField("isFetchOnly").setValue(true);
		boundSwagForm.saveData(new DSCallback() {
			//reselect selected tile (call to saveData de-selects it)
			public void execute(DSResponse response,
					Object rawData, DSRequest request) {
				//get updated record
				final TileRecord rec = new TileRecord(request.getData());
				//Note: selectRecord seems to only work on the tile index
				itemsTileGrid.selectRecord(itemsTileGrid.getRecordIndex(rec));
				//saveData adds tileRecord to the end.
				//make sure sort order is preserved
				doSort();
			}});
	}
}
