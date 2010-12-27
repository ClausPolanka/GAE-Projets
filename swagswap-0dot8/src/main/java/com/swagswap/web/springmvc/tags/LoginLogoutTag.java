package com.swagswap.web.springmvc.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class LoginLogoutTag  extends AbstractSpringContextLookupTag {
	private static final long serialVersionUID = 1L;
	
	// required tag attributes
	private String requestURL; //can't get it ourselves cause we can't get the HTTPServletRequest
	//optional tag attributes with default values
	private boolean showLogout=true; 
	private String loginText="login";
	private String loginTooltipText="your email address will remain private";
	private String logoutText="logout";
	
	public int doStartTag() throws JspException {
		try {
			if (getSwagSwapUserService().isUserLoggedIn() && showLogout) {
				String logoutUrl = getSwagSwapUserService().createLogoutURL(requestURL);
				pageContext.getOut().println("<a href=\"" + logoutUrl + "\">"+ logoutText + "</a>");
				pageContext.getOut().println("&nbsp;[Welcome: " + getSwagSwapUserService().getCurrentUser().getNickname() + "]");
			}
			else {
				String loginUrl = getSwagSwapUserService().createLoginURL(requestURL);
				pageContext.getOut().println("<a title=\""+ loginTooltipText + "\" href=\"" + loginUrl + "\">"+ loginText + "</a>");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return SKIP_BODY;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public void setShowLogout(boolean showLogout) {
		this.showLogout = showLogout;
	}

	public void setLoginText(String loginText) {
		this.loginText = loginText;
	}

	public void setLoginTooltipText(String loginTooltipText) {
		this.loginTooltipText = loginTooltipText;
	}

	public void setLogoutText(String logoutText) {
		this.logoutText = logoutText;
	}
}
