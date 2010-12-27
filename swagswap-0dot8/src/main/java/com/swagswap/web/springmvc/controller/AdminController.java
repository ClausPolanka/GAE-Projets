package com.swagswap.web.springmvc.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.service.AdminService;
import com.swagswap.service.ItemService;
import com.swagswap.service.OutgoingMailService;
import com.swagswap.service.SwagSwapUserService;

@Controller
public class AdminController {
	private static final Logger log = Logger.getLogger(AdminController.class);
	private AdminService adminService;
	private SwagSwapUserService swagSwapUserService;
	private ItemService itemService;
	private OutgoingMailService outgoingMailService;

	@Autowired
	public AdminController(AdminService adminService,
			SwagSwapUserService swagSwapUserService, ItemService itemService,
			OutgoingMailService outgoingMailService) {
		this.adminService = adminService;
		this.swagSwapUserService = swagSwapUserService;
		this.itemService = itemService;
		this.outgoingMailService = outgoingMailService;
	}

	@RequestMapping("/admin/main")
	public String adminMain() {
		return "admin";
	}

	/**
	 * populate images
	 */
	@RequestMapping(value = "/admin/populateTestSwagItems", method = RequestMethod.GET)
	public String populateTestSwagItems(
			@RequestParam("numberOfSwagItems") int numberOfSwagItems,
			Model model) throws IOException {
		adminService.populateTestSwagItems(numberOfSwagItems);
		model.addAttribute("message", "added " + numberOfSwagItems
				+ " swag items");
		return "admin";
	}

	@RequestMapping(value = "/admin/deleteTestSwagItems", method = RequestMethod.GET)
	public String deleteTestSwagItems(Model model) throws IOException {
		int numberDeleted = adminService.deleteTestSwagItems();
		model.addAttribute("message", "deleted " + numberDeleted
				+ " test swag items");
		return "admin";
	}

	@RequestMapping(value = "/admin/blackListUser", method = RequestMethod.GET)
	public String blackListUser(@RequestParam("email") String email, Model model)
			throws IOException {
		swagSwapUserService.blackListUser(email);
		model.addAttribute("message", "blacklisted " + email);
		return "admin";
	}

	@RequestMapping(value = "/opt-out/{googleID}/{optOut}", method = RequestMethod.GET)
	public String optOut(@PathVariable("googleID") String googleId,
			@PathVariable("optOut") boolean optOut, Model model)
			throws IOException {
		swagSwapUserService.optOut(googleId, optOut);

		model.addAttribute("message", "You have been"
				+ ((optOut) ? " removed from" : " added to")
				+ " future SwagSwap mailings.");
		return "opt-out";
	}

	/**
	 * Construct mail message and send it using the OutgoingMailService Called by Task
	 * Queue
	 * 
	 * @param swagItemKey
	 * @param subject
	 * @param msgBody
	 * @param response
	 *            returns HTTP 200 response status
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = "/admin/sendMailByItemKey", method = RequestMethod.POST)
	public void sendMailByItemKey(@RequestParam("swagItemKey") String swagItemKey,
			@RequestParam("subject") String subject,
			@RequestParam("msgBody") String msgBody,
			HttpServletResponse response, Model model) throws IOException {
		SwagItem swagItem = itemService.get(Long.parseLong(swagItemKey));
		String googleID = swagItem.getOwnerGoogleID();
		sendMail(googleID, subject, msgBody, response);
	}
	
	@RequestMapping(value = "/admin/sendMailByGoogleID", method = RequestMethod.POST)
	public void sendMailByGoogleID(@RequestParam("googleID") String googleID,
			@RequestParam("subject") String subject,
			@RequestParam("msgBody") String msgBody,
			HttpServletResponse response, Model model) throws IOException {
		sendMail(googleID, subject, msgBody, response);
	}
	
	@RequestMapping(value = "/admin/mailAllUsers", method = RequestMethod.GET)
	public String mailAllUsers(
			@RequestParam("subject") String subject,
			@RequestParam("msgBody") String msgBody,
			HttpServletResponse response, Model model) throws IOException {
		List<SwagSwapUser> allUsers = swagSwapUserService.getAll();
		for (SwagSwapUser swagSwapUser : allUsers) {
			outgoingMailService.sendWithTaskManager(swagSwapUser.getGoogleID(), subject, msgBody);
		}
		model.addAttribute("message", "number of users emailed (counting opt-outed users) " + allUsers.size());
		return "admin";
		
		
	}

	/**
	 * Called by methods that are called from the task manager
	 * @param googleID
	 * @param subject
	 * @param msgBody
	 * @param response
	 */
	private void sendMail(String googleID, String subject, String msgBody,
			HttpServletResponse response) {
		SwagSwapUser swagSwapUser = swagSwapUserService.findByGoogleID(googleID);
		
		if (swagSwapUser.getOptOut()) {
			log.debug(swagSwapUser.getGoogleID() + " has opted out of emails");
		} else {
			String email = swagSwapUser.getEmail();
			log.debug("Sending mail to " + email + " msg is subject:" + subject
					+ " msgBody:" + msgBody);
			try {
				outgoingMailService.send(googleID, email, subject, msgBody);
			} catch (Exception e) {
				log.error(e);
			}
		}
		// if you don't do this, task queue retries the task (a lot)
		response.setStatus(HttpServletResponse.SC_OK);
	}
	
		
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void startPage(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// Check here for iPhone and redirect to mobile app
		String userAgent = request.getHeader("User-Agent").toLowerCase();
		if (userAgent.contains("iphone")) {
			response.sendRedirect("/mobile/mobileHome.jsf");
		} else {
			response.sendRedirect("/jsf/home.jsf");
		}
	}
}