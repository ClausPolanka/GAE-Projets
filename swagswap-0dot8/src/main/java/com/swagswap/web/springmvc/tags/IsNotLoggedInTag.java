package com.swagswap.web.springmvc.tags;

import javax.servlet.jsp.JspException;

/**
 * Checks to see if the user is not logged in
 * and includes the body of the tag if they are
 *
 */
public class IsNotLoggedInTag extends AbstractSpringContextLookupTag {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		boolean isLoggedIn = getSwagSwapUserService().isUserLoggedIn();
		if (!isLoggedIn) {
			return EVAL_BODY_INCLUDE;
		}
		else {
			return SKIP_BODY;
		}
	}
}