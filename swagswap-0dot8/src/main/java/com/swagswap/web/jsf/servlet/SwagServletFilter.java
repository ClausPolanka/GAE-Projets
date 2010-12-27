package com.swagswap.web.jsf.servlet;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.swagswap.web.jsf.fileupload.MemoryFileItemFactory;

public class SwagServletFilter implements Filter {

	private int sizeMax = 1*1024*1024;  //1MB

	public void init(FilterConfig config) throws ServletException {

		try {
			String paramValue = config
					.getInitParameter("com.swagswap.UploadFilter.sizeMax");
			if (paramValue != null)
				sizeMax = Integer.parseInt(paramValue);
		}

		catch (NumberFormatException ex) {
			ServletException servletEx = new ServletException();
			servletEx.initCause(ex);
			throw servletEx;
		}
	}

	public void destroy() {

	}

	@SuppressWarnings("unchecked")
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (!(request instanceof HttpServletRequest)) {
			chain.doFilter(request, response);
			return;
		}

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		boolean isMultipartContent = ServletFileUpload
				.isMultipartContent(httpRequest);
		if (!isMultipartContent) {
			chain.doFilter(request, response);
			return;
		}

		// It's a Multipart request
		MemoryFileItemFactory factory = new MemoryFileItemFactory();
		factory.setSizeThreshold(sizeMax);

		ServletFileUpload upload = new ServletFileUpload(factory);
		// maximum size before a FileUploadException will be thrown. In this
		// case all are stored in Memory
		upload.setSizeMax(1000000);

		try {
			List list = upload.parseRequest(httpRequest);
			final Map map = new HashMap();
			for (int i = 0; i < list.size(); i++) {
				FileItem item = (FileItem) list.get(i);
				String str = item.getString();
				if (item.isFormField())
					map.put(item.getFieldName(), new String[] { str });
				else {
					httpRequest.setAttribute(item.getFieldName(), item.get());
				}
			}
			chain.doFilter(new HttpServletRequestWrapper(httpRequest) {

				public Map getParameterMap() {
					return map;
				}

				// Move this to RequestWrapper
				public String[] getParameterValues(String name) {
					Map map = getParameterMap();
					return (String[]) map.get(name);
				}

				public String getParameter(String name) {
					String[] params = getParameterValues(name);
					if (params == null)
						return null;
					return params[0];
				}

				public Enumeration getParameterNames() {
					Map map = getParameterMap();
					return Collections.enumeration(map.keySet());
				}
			}, response);
		} catch (FileUploadException ex) {
			ServletException servletEx = new ServletException();
			servletEx.initCause(ex);
			throw servletEx;
		}

	}
}
