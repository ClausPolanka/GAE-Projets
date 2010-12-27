package com.swagswap.web.jsf.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "testBean")
@RequestScoped

public class TestBean {
	
	private String uploadedFile;

	public String getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(String uploadedFile) {
		System.out.println("****  Uploaded file is "+uploadedFile);
		this.uploadedFile = uploadedFile;
	}
	
	public void actionUpload() {
		System.out.println("****  UPLOAD");
	}
	

}
