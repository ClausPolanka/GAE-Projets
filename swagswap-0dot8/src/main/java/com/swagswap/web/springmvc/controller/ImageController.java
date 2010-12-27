package com.swagswap.web.springmvc.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.swagswap.domain.SwagImage;
import com.swagswap.service.ImageService;

@Controller
public class ImageController {
	private static final Logger log = Logger.getLogger(ImageController.class);
	
	@Autowired
	private ImageService imageService; 

	@Autowired
	public ImageController(ImageService imageService) {
		this.imageService = imageService;
	}

	@RequestMapping("/imageList")
	public String showImageList(ModelMap model) {
		model.addAttribute("images", imageService.getAll());
		return "imageList";
	}
	
	/**
	 * Stream SwagImage.image from database or show defaultImage if there is none
	 */
	@RequestMapping(value = "/showImage/{key}", method = RequestMethod.GET)
	public void streamImageContent(@PathVariable("key") String key,
					HttpServletRequest req, HttpServletResponse response, 
					OutputStream outputStream) throws IOException {
		// set to no cache: NOTE this could fix caching problem 
		// but may not be ideal to stop all browser image caching
		//response.setDateHeader( "Expires", DateUtils.addMonths( new Date(), -2 ).getTime() );
		SwagImage swagImage = imageService.get(key);
		byte[] swagImageBytes;
		//if there's no image, return default image
		if (swagImage.getImage()==null) {
			swagImageBytes=imageService.getDefaultImageBytes(req.getRequestURL().toString());
		}
		else {
			swagImageBytes = swagImage.getImage().getBytes();
		}
		try {
			outputStream.write(swagImageBytes, 0, swagImageBytes.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Stream SwagImage.image from database or show defaultImage if there is none
	 */
	@RequestMapping(value = "/showThumbnail/{key}", method = RequestMethod.GET)
	public void streamThumbnailContent(@PathVariable("key") String key,
					HttpServletRequest req, OutputStream outputStream) throws IOException {

		byte[] swagImageBytes  = imageService.getThumbnailBytes(key);
		//if there's no image, return default image
		if (swagImageBytes == null) {
			swagImageBytes=imageService.getResizedThumbnailImageBytes(
					imageService.getDefaultImageBytes(req.getRequestURL().toString()));
		}
		
		try {
			outputStream.write(swagImageBytes, 0, swagImageBytes.length);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}