package com.swagswap.web.springmvc.displaytag;

import org.displaytag.decorator.TableDecorator;

import com.swagswap.domain.SwagItem;

/**
 * For creating a link from a SwagItem.name to it's view page
 */
public class NameDecorator extends TableDecorator {

	public String getSwagItemKeyLink()
	{
			SwagItem currentSwagItem= (SwagItem)getCurrentRowObject();
	        return "<a href=\"/springmvc/view/" + currentSwagItem.getKey() + "\">" +
	        	currentSwagItem.getName() + "</a>";
	}

}