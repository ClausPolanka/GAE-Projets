package com.swagswap.service;

public interface OutgoingMailService {

	void send(String googleID, String email, String subject, String msgBody);
	
	void send(String subject, String email, String msgBody);
	
	void sendWithTaskManager(Long swagItemKey, String subject, String msgBody);

	void sendWithTaskManager(String googleID, String subject, String msgBody);

}