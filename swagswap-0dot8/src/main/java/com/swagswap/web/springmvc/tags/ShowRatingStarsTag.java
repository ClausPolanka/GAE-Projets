package com.swagswap.web.springmvc.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import com.swagswap.domain.SwagItemRating;
import com.swagswap.domain.SwagSwapUser;

/**
 * Show rating stars, link each star to the ratingForm name passed in
 * 
 */
public class ShowRatingStarsTag extends AbstractSpringContextLookupTag {
	private static final long serialVersionUID = 1L;
	public static final String STAR_ON_IMAGE = "starOn.gif";
	public static final String STAR_OFF_IMAGE = "starOff.gif";
	// tag attributes
	private String rateFormName; // name of the form they'll use to submit
	// rating
	// the following are optional (should have userRating or swagSwapUser and
	// swagaItemKey to calculate rating)
	private Integer userRating; // optional
	private SwagSwapUser swagSwapUser; // optional
	private Long swagItemKey;

	/**
	 * Gets user rating and shows appropriate stars
	 */
	public int doStartTag() throws JspException {
		try {
			Integer userRating = this.userRating;
			if (userRating == null) { // we'll have to look it up
				if (swagSwapUser == null) { // not logged in
					userRating = 0;
				} else {
					SwagItemRating swagItemRating = swagSwapUser
							.getSwagItemRating(swagItemKey);
					if (swagItemRating == null) {
						userRating = 0;
					} else {
						userRating = swagItemRating.getUserRating();
					}
				}
			}
			// Show appropriate star-on star-off images based on their rating.
			// Link each star to the rateForm they pass in
			pageContext.getOut().print("<A NAME=\"" + swagItemKey + "\"></A>");
			for (int i = 1; i <= 5; i++) {
				String starImage = (i <= userRating) ? STAR_ON_IMAGE
						: STAR_OFF_IMAGE;
				pageContext.getOut().print(
						"<a href=\"#\" onclick=\"document." + rateFormName
								+ ".userRating.value='" + i + "';document."
								+ rateFormName + ".submit();\">");
				pageContext.getOut().print(
						"<img src=\"/images/" + starImage
								+ "\" border=\"0\"/></a>");
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return SKIP_BODY;
	}

	public void setRateFormName(String rateFormName) {
		this.rateFormName = rateFormName;
	}

	public void setUserRating(Integer userRating) {
		this.userRating = userRating;
	}

	public void setSwagSwapUser(SwagSwapUser swagSwapUser) {
		this.swagSwapUser = swagSwapUser;
	}

	public void setSwagItemKey(Long swagItemKey) {
		this.swagItemKey = swagItemKey;
	}

}
