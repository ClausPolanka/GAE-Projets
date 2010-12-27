package com.swagswap.web.springmvc.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.swagswap.service.SwagSwapUserService;

/**
 * Exposes anything from the  spring context for use by subclasses
 * needed since there's no way to do DI in taglibs
 * TODO this creates another context.  Fix
 *
 */
public abstract class AbstractSpringContextLookupTag extends TagSupport {
	private static final long serialVersionUID = 1L;
	private static final ApplicationContext CTX =
	    new FileSystemXmlApplicationContext("/WEB-INF/applicationContext.xml");
	
	protected SwagSwapUserService getSwagSwapUserService () {
		return (SwagSwapUserService) CTX.getBean("swagSwapUserService");
	}

}