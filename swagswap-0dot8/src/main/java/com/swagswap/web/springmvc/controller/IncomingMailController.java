package com.swagswap.web.springmvc.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.exceptions.ImageTooLargeException;
import com.swagswap.exceptions.InvalidSwagImageException;
import com.swagswap.exceptions.LoadImageFromURLException;
import com.swagswap.service.ItemService;
import com.swagswap.service.OutgoingMailService;
import com.swagswap.service.SwagSwapUserService;

@Controller
public class IncomingMailController {
	private static final Logger log = Logger.getLogger(IncomingMailController.class);
	private SwagSwapUserService swagSwapUserService;
	private ItemService itemService;
	private OutgoingMailService outgoingMailService;

	@Autowired
	public IncomingMailController(SwagSwapUserService swagSwapUserService, 
			ItemService itemService,
			OutgoingMailService outgoingMailService) {
		this.swagSwapUserService = swagSwapUserService;
		this.itemService = itemService;
		this.outgoingMailService = outgoingMailService;
	}

	// Incoming email see
	// http://code.google.com/appengine/docs/java/mail/receiving.html
	@RequestMapping(value = "/_ah/mail/{address}", method = RequestMethod.POST)
	public void createSwagItemFromIncomingEmail(@PathVariable("address") String address,
			HttpServletRequest request, HttpServletResponse response) {
		String fromEmail = null;
		try {
			// Create MimeMessage from request
			// From Appengine docs: http://code.google.com/appengine/docs/java/mail/receiving.html
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			MimeMessage mimeMessage = new MimeMessage(session, request.getInputStream());
			
			String mailSubject = mimeMessage.getSubject();
			
			fromEmail = ((InternetAddress)mimeMessage.getFrom()[0]).getAddress();
			log.debug("Receieved message from " + fromEmail +  " subject " + mailSubject);

			SwagSwapUser user = swagSwapUserService.findByEmail(fromEmail);
			if (user==null) {
				log.debug("user not found for email " + fromEmail);
				sendUserNotFoundEmail(fromEmail);
				return;
			}
			log.debug("found user " + user.getNickName());
	

			// get body and attachment 
			// from http://jeremyblythe.blogspot.com/2009/12/gae-128-fixes-mail-but-not-jaxb.html
			Object content = mimeMessage.getContent();

			String bodyText = "";
			byte[] imageData = null;
			if (content instanceof String) {
				bodyText = (String) content;
			} else if (content instanceof Multipart) {
				Multipart multipart = (Multipart) content;
				Part part = multipart.getBodyPart(0);
				Object partContent = part.getContent();
				if (partContent instanceof String) {
					bodyText = (String) partContent;
				}
				// extract attached image if any
				imageData = getMailAttachmentBytes(multipart);
			}


			SwagItem swagItem = new SwagItem();
			swagItem.setOwnerGoogleID((user.getGoogleID()));
			swagItem.setOwnerNickName(user.getNickName());
			swagItem.setName(mailSubject);
			swagItem.setDescription(bodyText);
			swagItem.setImageBytes(imageData);
			itemService.saveFromEmail(swagItem);
			
			if (user.getOptOut()) {
				log.debug(user.getGoogleID() + " has opted out of emails");
			} else { // send email
				sendItemAddedSuccessfullyEmail(user, swagItem);
			}
		}
		catch (Exception e) {
			log.error("Problem with incoming message from " + fromEmail,e);
			//report error to sender
			sendItemAddExceptionEmail(fromEmail, e);
			//report error to Sam
			sendItemAddExceptionEmailToAdmin(e);
		}
		finally {
			//always send status OK or Appengine will keep retrying
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}



	/**
	 * From http://java.sun.com/developer/onlineTraining/JavaMail/contents.html#JavaMailMessage
	 * @param attachmentInputStream
	 * @param mimeMultipart
	 * @return image data from attachment or null if there is none
	 * @throws MessagingException
	 * @throws IOException
	 */
	private byte[] getMailAttachmentBytes(Multipart mimeMultipart) throws MessagingException, IOException {
		InputStream attachmentInputStream = null;
		try {
			for (int i = 0, n = mimeMultipart.getCount(); i < n; i++) {
				String disposition = mimeMultipart.getBodyPart(i).getDisposition();
				if (disposition == null) {
					continue;
				}
				if ((disposition.equals(Part.ATTACHMENT) || (disposition.equals(Part.INLINE)))) {
					attachmentInputStream = mimeMultipart.getBodyPart(i).getInputStream();
					byte[] imageData = getImageDataFromInputStream(attachmentInputStream);
					return imageData;
				}
			}
		}
		finally {
			try {if (attachmentInputStream!=null) attachmentInputStream.close();} catch (Exception e) {}
		}
		return null;
	}

	private void sendItemAddExceptionEmail(String fromEmail, Exception e) {
		outgoingMailService.send("Your SwagItem email upload failed :(",fromEmail,
			"<b>Please <a href=\"http://code.google.com/p/swagswap/issues/entry?template=Defect%20report%20from%20user\">" +
				"report this issue</a> (requires a google account)</b><br/><br/>Exception:<br/>" + e.toString()
		);
	}
	
	private void sendItemAddExceptionEmailToAdmin(Exception e) {
		outgoingMailService.send("SwagItem email upload failed :(","swagswap.devoxx2009@gmail.com",
				e.toString()
			);
	}

	private void sendItemAddedSuccessfullyEmail(SwagSwapUser user, SwagItem swagItem) {
		outgoingMailService.send(
			user.getGoogleID(),
			user.getEmail(),
			"Your swag item: " + swagItem.getName()
					+ " has been successfuly created.  You may want to fill in additional item information",
			"\n\n<br/><br/>See Your Item here: (Spring MVC impl) http://swagswap.appspot.com/springmvc/view/"
					+ swagItem.getKey()
					+ "\n<br/>or here (JSF 2.0 impl) http://swagswap.appspot.com/jsf/viewSwag.jsf?swagItemKey="
					+ swagItem.getKey());
	}

	private void sendUserNotFoundEmail(String fromEmail) {
		outgoingMailService.send("Your SwagItem email upload failed :(",fromEmail,
				"User with email " + fromEmail + " not found in the SwagSwap system.\n " +
				"please try again and make sure you're sending the email from your Google account email address " +
				"Or <a href=\"http://www.swagswap.org\">signin to SwagSwap</a> to create your user account and retry your email."
			);
	}

	// tsk tsk, this is copy pasted from ItemService.getImageDataFromURL()
	public byte[] getImageDataFromInputStream(InputStream inputStream)
			throws LoadImageFromURLException, ImageTooLargeException {
		BufferedInputStream bis = null;
		ByteArrayOutputStream bos = null;
		try {
			bis = new BufferedInputStream(inputStream);
			// write it to a byte[] using a buffer since we don't know the exact
			// image size
			byte[] buffer = new byte[1024];
			bos = new ByteArrayOutputStream();
			int i = 0;
			while (-1 != (i = bis.read(buffer))) {
				bos.write(buffer, 0, i);
			}
			byte[] imageData = bos.toByteArray();
			if (imageData.length > 1000000) {
				throw new ImageTooLargeException("from email", 1000000);
			}
			return imageData;
		} catch (IOException e) {
			throw new InvalidSwagImageException(e);
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
}