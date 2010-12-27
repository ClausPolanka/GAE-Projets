package com.swagswap.web.springmvc.tags;

import javax.servlet.jsp.JspException;

/**
 * Checks to see if the user is admin
 * and includes the body of the tag if they are
 *
 */
public class IsAdminTag extends AbstractSpringContextLookupTag {
	private static final long serialVersionUID = 1L;

	public int doStartTag() throws JspException {
		boolean isAdmin = getSwagSwapUserService().isUserLoggedIn()
							&& getSwagSwapUserService().isUserAdmin();
		if (isAdmin) {
			return EVAL_BODY_INCLUDE;
		}
		else {
			return SKIP_BODY;
		}
	}
}