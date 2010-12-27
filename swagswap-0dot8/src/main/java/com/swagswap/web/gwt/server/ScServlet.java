package com.swagswap.web.gwt.server;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * This is to get around appengine's 3000 src file limit
 * SwagSwapGWT creates a directory with 1000+ files.  The solution (as outlined here
 * http://forums.smartclient.com/showthread.php?t=5258) is to zip these resources
 * and serve them from this Servlet (which unzips and caches)
 *
 */
public class ScServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ScServlet.class
			.getName());

	/**
	 * This servlet receives all '/sc/*' requests according to a rule defined in
	 * 'web.xml'. It looks for the necessary resource in 'sc.zip' which should
	 * be present in the application classpath (root of webapp).
	 */
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// Sometimes the request URI begins with '/sc' and other times with
		// '/myappname/sc', so we always remove '/myappname' from the request
		// URI
		String requestedURI = req.getRequestURI()
				.replaceFirst("/swagswap", "");

		// At this point requestedURI begins with '/sc/...' but the zip file
		// entries
		// begin with 'sc/...', so we remove the first '/'
		requestedURI = requestedURI.substring(1);

		requestedURI = requestedURI.replaceAll("//", "/");

//		log.info("Requested URI '" + req.getRequestURI() + "' converted to '"
//				+ requestedURI + "'");

		try {
			resp.getOutputStream().write(
					getResourceFromCache(resp, requestedURI));
		} catch (CacheException e) {
			resp.getOutputStream()
					.write(getResourceFromZip(resp, requestedURI));
		}
	}

	@SuppressWarnings("unchecked")
	private byte[] getResourceFromCache(HttpServletResponse resp, String key)
			throws CacheException, IOException {

		javax.cache.Cache cache = CacheManager.getInstance().getCacheFactory()
				.createCache(Collections.emptyMap());
		byte[] resource = (byte[]) cache.get(key);

		if (resource == null) {
			resource = getResourceFromZip(resp, key);
			cache.put(key, resource);
		}

		return resource;
	}

	private byte[] getResourceFromZip(HttpServletResponse resp,
			String requestedURI) throws FileNotFoundException, IOException {
		// search for resource
		ZipInputStream in = new ZipInputStream(new FileInputStream("sc.zip"));
		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {

			if (requestedURI.equals(entry.getName())) {
				// resource found
				// redirect it to output stream
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
//				log.info("Requested '" + requestedURI
//						+ "' found in zip file entry: " + entry.getName());
				return out.toByteArray();
			}
		}

		log.severe("Requested '" + requestedURI + "' not found in zip file!");
		return null;
	}

}