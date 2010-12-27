package com.swagswap.service;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.TaskOptions;

/**
 * For sending email
 * 
 */
public class OutgoingMailServiceImpl implements OutgoingMailService {

	private static final String FROM_ADDRESS = "swagswap.devoxx2009@gmail.com";

	private static final Logger log = Logger.getLogger(OutgoingMailServiceImpl.class);

	public OutgoingMailServiceImpl() {
	}

	/**
	 * Make sure you check if user has opted out of emails before you call this method!
	 * @param googleId To construct opt-out line in message
	 */
	public void send(String googleId, String email, String subject, String msgBody) {
		log.debug("Processing send for " + subject + " at " + new Date());
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM_ADDRESS));
            msg.addRecipient(Message.RecipientType.TO,new InternetAddress(email));
            msg.setSubject(subject);
            if (googleId!=null) {
	            // add opt-out/in lines
	            msgBody=msgBody + "\n\n\n\n\n<br/><br/><br/><br/><i>Stop receiving emails from SwagSwap: http://swagswap.appspot.com/springmvc/opt-out/"+ googleId +"/true";   
	            msgBody=msgBody + "\n\n<br/><br/>(Re)start receiving emails from SwagSwap: http://swagswap.appspot.com/springmvc/opt-out/"+ googleId +"/false</i>";   
            }
	        msg.setContent(msgBody,"text/html");
            Transport.send(msg);
            log.debug("sending mail to " + email);
        } catch (Exception e) {
           log.error("sender is " +  FROM_ADDRESS, e);
        }
	}
	
	public void send(String subject, String email, String msgBody) {
		send(null,email,subject,msgBody);
	}
	
	/**
	 * Send mail based on user id.  Do it in a Task Queue to prevent delay
	 * The AdminController has a method which is called by the Task Queue,
	 * which in turn calls OutgoingMailService.send() back
	 */
	public void sendWithTaskManager(Long swagItemKey, String subject, String msgBody) {
		log.debug("Queing task for mail with subject " + subject + " at " + new Date());
	    QueueFactory.getDefaultQueue().add(
	    		//only can pass String or byte[] params
	            TaskOptions.Builder
	              .url("/springmvc/admin/sendMailByItemKey")
	              .param("swagItemKey", swagItemKey.toString())
	              .param("subject", subject)
	              .param("msgBody", msgBody)
	              );
	}
	
	public void sendWithTaskManager(String googleID, String subject, String msgBody) {
		log.debug("Queing task for mail with subject " + subject + " at " + new Date());
		QueueFactory.getDefaultQueue().add(
				//only can pass String or byte[] params
				TaskOptions.Builder
				.url("/springmvc/admin/sendMailByGoogleID")
				.param("googleID", googleID)
				.param("subject", subject)
				.param("msgBody", msgBody)
		);
	}

}