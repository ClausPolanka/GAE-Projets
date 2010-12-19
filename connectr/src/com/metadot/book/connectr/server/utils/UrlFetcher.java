/** 
 * Copyright 2010 Daniel Guermeur and Amy Unruh
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   See http://connectrapp.appspot.com/ for a demo, and links to more information 
 *   about this app and the book that it accompanies.
 */
package com.metadot.book.connectr.server.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlFetcher {

  private static String getString(InputStream is, String charEncoding) {
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      byte ba[] = new byte[8192];
      int read = is.read(ba);
      while (read > -1) {
        out.write(ba, 0, read);
        read = is.read(ba);
      }
      String returnString = out.toString(charEncoding);
      if (returnString.equalsIgnoreCase("{}")) {
        returnString = "[{}]";
      }
      return returnString;
    } catch (Exception e) {
      return null;
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (Exception e) {
      }
    }
  }

  public static String get(String thisUrl) throws MalformedURLException,
      IOException {
    URL url = new URL(thisUrl);
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    return getString(con.getInputStream(), "utf-8");
  }

}
