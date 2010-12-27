package com.swagswap.web.springmvc.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.swagswap.domain.SearchCriteria;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;
import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagStats;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.exceptions.AccessDeniedException;
import com.swagswap.exceptions.ImageTooLargeException;
import com.swagswap.exceptions.InvalidSwagImageException;
import com.swagswap.exceptions.InvalidSwagItemException;
import com.swagswap.exceptions.LoadImageFromURLException;
import com.swagswap.service.ItemService;
import com.swagswap.service.SwagStatsService;
import com.swagswap.service.SwagSwapUserService;

@Controller
public class ItemController {
	private static final Logger log = Logger.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;
	@Autowired
	private SwagSwapUserService swagSwapUserService;
	@Autowired
	private SwagStatsService swagStatsService;
	
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addHandler(Model model) {
		model.addAttribute("swagItem", new SwagItem());
		return "addEditSwagItem";
	}

	@RequestMapping(value = "/edit/{key}", method = RequestMethod.GET)
	public String editHandler(@PathVariable("key") Long key, Model model) {
		SwagItem swagItem = itemService.get(key, true);
		if (swagItem==null) {
			throw new AccessDeniedException("object with id " + key + " not found");
		}
		model.addAttribute("swagItem", swagItem);
		return "addEditSwagItem";
	}
	
	/**
	 * Insert or update SwagItem
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String saveHandler(@ModelAttribute SwagItem swagItem, Errors errors) {
		try {
			//insert user
			swagSwapUserService.findByEmailOrCreate();
			
			itemService.save(swagItem);
		}
		catch (LoadImageFromURLException e) {
			errors.rejectValue("imageURL","","could not retrieve URL");
		}
		catch (ImageTooLargeException e) {
			errors.rejectValue("imageURL","","image is too large (max size is 150K)");
		}
		catch (InvalidSwagImageException e) {
			errors.rejectValue("","","image of type " + e.getMimeType() + " is not allowed");
		}
		// Normally you would use front end validation for this, but we only need to 
		// validate one field and it's already implemented on the server-side. 
		catch (InvalidSwagItemException e) {
			errors.rejectValue("name","","name is required");
		}
		return (errors.hasErrors()) ? "addEditSwagItem" : "redirect:/springmvc/search";
	}
	
	@RequestMapping(value = "/view/{key}", method = RequestMethod.GET)
	public String viewHandler(@PathVariable("key") Long key, Model model) {
		SwagItem swagItem = itemService.get(key, true);
		if (swagItem==null) {
			throw new AccessDeniedException("object with id " + key + " not found");
		}
		//put rating (if there is one) into the model
		String ratingString = "";
		if (swagSwapUserService.isUserLoggedIn()) {
			//get swagSwapUser using email key from available google user
			//we've got to create them here if they don't already exist in our DB
			SwagSwapUser swagSwapUser = swagSwapUserService.findByEmailOrCreate();
			//see if the already have a rating for this item
			SwagItemRating rating = swagSwapUser.getSwagItemRating(key);
			if (null!=rating) {
				ratingString = rating.getUserRating().toString();
			}
		}
		//make previous rating available to the page to show the right number of stars
		model.addAttribute("userRating", ratingString);
		//backing object for rating `
		model.addAttribute("newRating", new SwagItemRating(swagItem.getKey())); 
		//backing object for comment
		model.addAttribute("newComment", new SwagItemComment(swagItem.getKey())); 
		model.addAttribute("swagItem", swagItem);
		return "viewRateSwagItem";
	}
	
	@RequestMapping(value = "/swagStats", method = RequestMethod.GET)
	public String viewStatsHandler(Model model, HttpServletRequest request) {
		SwagStats swagStats = swagStatsService.getSwagStats();
		model.addAttribute("swagStats", swagStats);
		//put SwagSwapUser (if there is one) into the model
		if (swagSwapUserService.isUserLoggedIn()) {
			//get swagSwapUser using email key from available google user
			//we've got to create them here if they don't already exist in our DB
			SwagSwapUser swagSwapUser = swagSwapUserService.findByEmailOrCreate();
			model.addAttribute("swagSwapUser", swagSwapUser);
		}
		//add backing object for each possible new rating
		for (SwagItem swagItem : swagStats.getAllTopRatedSwagItems()) {
			model.addAttribute("newRating"+"-"+swagItem.getKey(), new SwagItemRating(swagItem.getKey())); 
		}
		model.addAttribute("loginUrl", swagSwapUserService.createLoginURL(getReferringPage(request)));
		return "swagStats";
	}
	
	@RequestMapping(value = "/delete/{key}", method = RequestMethod.GET)
	public String deleteHandler(@PathVariable("key") Long key) {
		itemService.delete(key);
		return "redirect:/springmvc/search";
	}
	
    @RequestMapping(value = "/rate", method = RequestMethod.GET)
	public String rateHandler(@ModelAttribute SwagItemRating swagItemRating, HttpServletRequest request) {
		String email = swagSwapUserService.getCurrentUser().getEmail();
		swagSwapUserService.addOrUpdateRating(email, swagItemRating);
		return "redirect:" + getReferringPage(request);
	}
    
    @RequestMapping(value = "/addComment", method = RequestMethod.GET)
    public String addCommentHandler(@ModelAttribute SwagItemComment swagItemComment, HttpServletRequest request) {
    	itemService.addComment(swagItemComment);
    	return "redirect:" + getReferringPage(request);
    }
	
	//For legacy URL that some tweets had already linked to.
	@RequestMapping(value = "/listSwagItems", method = RequestMethod.GET)
	public String listHandler(Model model) {
		return "redirect:/springmvc/search";
	}

	/**
	 * Searching with no searchString does a listAll
	 * @param searchCriteria
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchHandler(@ModelAttribute SearchCriteria searchCriteria,
			Model model, HttpServletRequest request) {
		if (searchCriteria==null) {
			model.addAttribute("searchCriteria", new SearchCriteria());
		}
		List<SwagItem> swagItems = itemService.search(searchCriteria.getSearchString());
		
		//put SwagSwapUser (if there is one) into the model
		if (swagSwapUserService.isUserLoggedIn()) {
			//get swagSwapUser using email key from available google user
			//we've got to create them here if they don't already exist in our DB
			SwagSwapUser swagSwapUser = swagSwapUserService.findByEmailOrCreate();
			model.addAttribute("swagSwapUser", swagSwapUser);
		}
		//add backing object for each possible new rating
		for (SwagItem swagItem : swagItems) {
			model.addAttribute("newRating"+"-"+swagItem.getKey(), new SwagItemRating(swagItem.getKey())); 
		}
		
		model.addAttribute("swagItems", swagItems);
		//needed in JSP to create loginURL
		model.addAttribute("loginUrl", swagSwapUserService.createLoginURL(getReferringPage(request)));
		return "listSwagItems";
	}

	/**
	 * @param request
	 * @return referring page
	 */
	private String getReferringPage(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		if (referer==null) { //in case browser doesn't support Redirect header
			referer="/springmvc/search";
		}
		return referer;
	}
	
	@InitBinder
	protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder) throws Exception {
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));

		binder.registerCustomEditor(List.class, new CustomCollectionEditor(List.class));

		// for spring in gae?
		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));

		binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
		binder.registerCustomEditor(Boolean.class, new CustomBooleanEditor(false));
		binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
		
		//for image upload
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	}

	//TODO is this needed with @Autowire?
	public void setSwagItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	
}