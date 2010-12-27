package com.swagswap.web.springmvc.tags;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

/**
 * Checks to see if the logged in user is allowed to access the current item
 * and includes the body of the tag if they are
 *
 */
public class IsAllowedTag extends AbstractSpringContextLookupTag {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(IsAllowedTag.class);
	// tag attributes
	private String swagItemOwnerGoogleID;

	public int doStartTag() throws JspException {
		boolean isLoggedIn = getSwagSwapUserService().isUserLoggedIn();
		boolean isAdmin=false;
		boolean iDMatchesCurrentItemOwnerID=false;
		if (isLoggedIn) {
			isAdmin = getSwagSwapUserService().isUserAdmin();
			iDMatchesCurrentItemOwnerID = getSwagSwapUserService().getCurrentUser().getUserId().equals(swagItemOwnerGoogleID);
		}
		if (isLoggedIn && (isAdmin || iDMatchesCurrentItemOwnerID)) {
			return EVAL_BODY_INCLUDE;
		}
		else {
			return SKIP_BODY;
		}
	}

	public void setSwagItemOwnerGoogleID(String swagItemOwnerGoogleID) {
		this.swagItemOwnerGoogleID = swagItemOwnerGoogleID;
	}
}