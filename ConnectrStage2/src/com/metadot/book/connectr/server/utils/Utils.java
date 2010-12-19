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

/**
 * Code adapted from: the Google App Engine Virtual File System (GaeVFS) project,
 * http://code.google.com/p/gaevfs/
 */

import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.labs.taskqueue.QueueFailureException;


public class Utils {
        
    private static final Logger log = Logger.getLogger( Utils.class.getName() );
    
    /**
     * Serialize an object into a byte array.
     * 
     * @param obj An object to be serialized.
     * @return A byte array containing the serialized object
     * @throws QueueFailureException If an I/O error occurs during the
     * serialization process.
     */
    public static byte[] serialize( Object obj ) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            ObjectOutputStream objectOut = new ObjectOutputStream( 
                                                new BufferedOutputStream( bytesOut ) );
            objectOut.writeObject( obj );
            objectOut.close();
            return encodeBase64( bytesOut.toByteArray() );
        } catch ( IOException e ) {
            throw new QueueFailureException( e );
        }
    }
    
    /**
     * Deserialize an object from an HttpServletRequest input stream. Does not
     * throw any exceptions; instead, exceptions are logged and null is returned.
     * 
     * @param req An HttpServletRequest that contains a serialized object.
     * @return An object instance, or null if an exception occurred.
     */
    public static Object deserialize( HttpServletRequest req ) {
        if ( req.getContentLength() == 0 ) {
            log.severe( "request content length is 0" );
            return null;
        }
        try {
            byte[] bytesIn = new byte[ req.getContentLength() ];
            req.getInputStream().readLine( bytesIn, 0, bytesIn.length );
            return deserialize( bytesIn );
        } catch ( IOException e ) {
            log.log( Level.SEVERE, "Error deserializing task", e );
            return null; // don't retry task
        }
    }

    /**
     * Deserialize an object from a byte array. Does not throw any exceptions;
     * instead, exceptions are logged and null is returned.
     * 
     * @param bytesIn A byte array containing a previously serialized object.
     * @return An object instance, or null if an exception occurred.
     */
    public static Object deserialize( byte[] bytesIn ) {
        ObjectInputStream objectIn = null;
        try {
            bytesIn = decodeBase64( bytesIn );
            objectIn = new ObjectInputStream( new BufferedInputStream(
                                        new ByteArrayInputStream( bytesIn ) ) );
            return objectIn.readObject();
        } catch ( Exception e ) {
            log.log( Level.SEVERE, "Error deserializing task", e );
            return null; // don't retry task
        } finally {
            try {
                if ( objectIn != null ) {
                    objectIn.close();
                }
            } catch ( IOException ignore ) {
            }
        }
    }
    
    public static String stackTraceToString(Throwable e) {
      String retValue = null;
      StringWriter sw = null;
      PrintWriter pw = null;
      try {
       sw = new StringWriter();
       pw = new PrintWriter(sw);
       e.printStackTrace(pw);
       retValue = sw.toString();
      } finally {
       try {
         if(pw != null)  pw.close();
         if(sw != null)  sw.close();
       } catch (IOException ignore) {}
      }
      return retValue;
    }
    
} // end class
