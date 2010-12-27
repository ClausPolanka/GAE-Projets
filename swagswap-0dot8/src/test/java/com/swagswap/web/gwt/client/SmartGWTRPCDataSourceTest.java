package com.swagswap.web.gwt.client;

import com.google.gwt.junit.client.GWTTestCase;

public class SmartGWTRPCDataSourceTest extends GWTTestCase {

	//TODO where is this supposed to point to?
	public String getModuleName() {
		return "com.swagswap.web.gwt.SwagSwapGWT";
	}


	public void testAppendRandomQueryString() {
		String key = "123";
		String result = SmartGWTRPCDataSource.appendRandomQueryString(key);
		assertTrue(result.contains("?"));
	}
	
}
