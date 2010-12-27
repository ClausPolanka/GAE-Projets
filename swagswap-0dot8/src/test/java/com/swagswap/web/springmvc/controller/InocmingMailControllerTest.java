package com.swagswap.web.springmvc.controller;

import junit.framework.TestCase;

import com.swagswap.dao.ImageDaoImpl;
import com.swagswap.domain.SwagImage;
import com.swagswap.web.springmvc.controller.ImageController;

public class InocmingMailControllerTest extends TestCase {
	public void testExtractMessageText() throws Exception {
		String gmailMessageText = "--001636c92780f9518f047853eda6 Content-Type: text/plain; " +
				"charset=ISO-8859-1  This is a terrible stinky shoe" +
				"  --001636c92780f9518f047853eda6 Content-Type: text/html; charset=ISO-8859-1" +
				"  This is a terrible stinky shoe   --001636c92780f9518f047853eda6--";
		
		String outlookMessageText ="------=_NextPart_001_0016_01CA6532.5E0B4ED0 Content-Type:" +
				" text/plain; 	charset=\"iso-8859-1\" Content-Transfer-Encoding: quoted-printable" +
				"  This is a terrible stinky shoe ------=_NextPart_001_0016_01CA6532.5E0B4ED0 Content-Type:" +
				" text/html; 	charset=\"iso-8859-1\" Content-Transfer-Encoding: quoted-printable" +
				"  <!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"> <HTML><HEAD>" +
				" <META http-equiv=3DContent-Type content=3D\"text/html; = charset=3Diso-8859-1\">" +
				" <META content=3D\"MSHTML 6.00.2900.5512\" name=3DGENERATOR> <STYLE></STYLE> </HEAD>" +
				" <BODY bgColor=3D#ffffff> <DIV><FONT face=3DArial size=3D2>this is an = item</FONT>" +
				"</DIV></BODY></HTML>  ------=_NextPart_001_0016_01CA6532.5E0B4ED0--";
		
		IncomingMailController incomingMailController = new IncomingMailController(null, null, null);
		
		String resultGmail = incomingMailController.extractMessageText(gmailMessageText);
		assertEquals("This is a terrible stinky shoe", resultGmail);
		String resultOutlookMessageText = incomingMailController.extractMessageText(outlookMessageText);
		assertEquals("This is a terrible stinky shoe", resultOutlookMessageText);
		
	}
}
