package com.swagswap.web.springmvc.displaytag;

import java.util.List;

import javax.servlet.jsp.PageContext;

import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.DisplaytagColumnDecorator;
import org.displaytag.properties.MediaTypeEnum;

/**
 * For showing a list of swagitem tags on the JSP (using the displaytag library)
 */
@SuppressWarnings("unchecked")
public class TagsDecorator implements DisplaytagColumnDecorator {

	/**
	 * @see org.displaytag.decorator.DisplaytagColumnDecorator#decorate(Object,
	 *      PageContext, MediaTypeEnum)
	 */
	public Object decorate(Object columnValue, PageContext pageContext, MediaTypeEnum media) {
		if (columnValue==null || !(columnValue instanceof List)) {
			return null;
		}
		StringBuilder s = new StringBuilder();
		List<String> tags = (List<String>)columnValue;
		for (String tag : tags) {
			if (!"".equals(tag)) {
				s.append(tag +", ");
			}
		}
		return StringUtils.chomp(s.toString(),", "); //chomp off last comma
	}

}