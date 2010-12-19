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

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.shared.messages.Message;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;


/**
 * Wraps the ChannelService up in our application-specific push-messaging
 * infrastructure.
 * 
 * @author Toby Reyelts @ Google Modified by @author Amy Unruh & @author Daniel
 *         Guermeur
 * 
 */
public class ChannelServer {
  // set whether to use Channel API or not
  private static Properties props = System.getProperties();
  private static final boolean USE_CHANNEL_API = setChannelAPIEnabled();
  private static final Logger logger = 
    Logger.getLogger(ChannelServer.class.getName());


  private static final Method dummyMethod = getDummyMethod();
  private static final String APP_KEY = "Connectr-";

  private static SerializationPolicy serializationPolicy = createPushSerializationPolicy();
  
  private static boolean setChannelAPIEnabled() {
    String skey = props.getProperty("com.metadot.connectr.enable-channelapi");
    if (skey != null) {
      if (skey.equalsIgnoreCase("true")) {
        //logger.info("channel API is enabled");
        return true;
      }
      if (skey.equalsIgnoreCase("false")) {
        return false;
      }
    }
    return false;
  }
  
  public static boolean channelAPIEnabled() {
    return USE_CHANNEL_API;
  }

  /**
   * Sends a message to all specified users.
   * 
   * @param userUniqueIds
   *            The users to send the message to.
   * @param msg
   *            The message to be sent.
   */
  private static void pushMessageById(List<String> userUniqueIds, Message msg) {
    String encodedMessage = encodeMessage(msg);
    for (String userUniqueId : userUniqueIds) {
      String key = getAppKeyForUser(userUniqueId);
      logger.info("Pushing msg to " + key);
      try {
        getChannelService().sendMessage(
            new ChannelMessage(key, encodedMessage));
      } catch (Exception e) {
        // [The original google example code notes here: 
        // A bug in the dev_appserver causes an exception to be
        // thrown when no users are connected yet.]
        logger.log(Level.SEVERE, "Failed to push the message " + msg
            + " to client " + key, e);
      }
    }
  }

  /**
   * Sends a message to one specific user.
   * 
   * @param user
   *            The user to send the message to.
   * @param msg
   *            The message to be sent.
   */
  public static void pushMessage(UserAccount user, Message msg) {
    if(user.getChannelId() == null){
      logger.log(Level.SEVERE, "Can't push a message to a null channeID. Maybe the Channel API is disabled in Connectr.");
      return;
      
    }
    
    pushMessageById(Arrays.asList(user.getUniqueId()), msg);
  }

  /**
   * Create a channel for a user. Returns the channel id that the client must
   * use to connect for receiving push messages.
   * 
   * @param userUniqueId
   * @return the client channel id
   */
  public static String createChannel(String userUniqueId) {
    if(!USE_CHANNEL_API){
      logger.log(Level.WARNING, "Channel API disabled in appengine-web.xml. Not creating.");      
      return null;
    }
    
    String channelId = getChannelService().createChannel(
        APP_KEY + userUniqueId);
    logger.info("Created new channel: " + channelId);
    return channelId;
  }

  /**
   * Creates a new SerializationPolicy for push RPC.
   */
  private static SerializationPolicy createPushSerializationPolicy() {

    File[] files = new File("connectr").listFiles(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".gwt.rpc");
      }
    });

    List<SerializationPolicy> policies = new ArrayList<SerializationPolicy>();

    for (File f : files) {
      try {
        BufferedInputStream input = new BufferedInputStream(
            new FileInputStream(f));
        policies.add(SerializationPolicyLoader.loadFromStream(input,
            null));
      } catch (Exception e) {
        throw new RuntimeException("Unable to load a policy file: "
            + f.getAbsolutePath());
      }
    }

    return new MergedSerializationPolicy(policies);
  }

  private static String getAppKeyForUser(String userUniqueId) {
    return APP_KEY + userUniqueId;
  }

  private static String encodeMessage(Message msg) {
    try {
      return RPC.encodeResponseForSuccess(dummyMethod, msg,
          serializationPolicy);
    } catch (SerializationException e) {
      throw new RuntimeException("Unable to encode a message for push.\n"
          + msg, e);
    }
  }

  /**
   * This method exists to make GWT RPC happy.
   * <p>
   * {@link RPC#encodeResponseForSuccess(java.lang.reflect.Method, Object)}
   * insists that we pass it a Method that has a return type equal to the
   * object we're encoding. What we really want to use is
   * {@link RPC#encodeResponse(Class, Object, boolean, int, com.google.gwt.user.server.rpc.SerializationPolicy)}
   * , but it is unfortunately private.
   */
  @SuppressWarnings("unused") private Message dummyMethod() {
    throw new UnsupportedOperationException("This should never be called.");
  }

  private static Method getDummyMethod() {
    try {
      return ChannelServer.class.getDeclaredMethod("dummyMethod");
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Unable to find the dummy RPC method.");
    }
  }

  private static ChannelService getChannelService() {
    return ChannelServiceFactory.getChannelService();
  }

  private static class MergedSerializationPolicy extends SerializationPolicy {
    List<SerializationPolicy> policies;

    MergedSerializationPolicy(List<SerializationPolicy> policies) {
      this.policies = policies;
    }


    @Override
    public boolean shouldDeserializeFields(Class<?> clazz) {
      for (SerializationPolicy p : policies) {
        if (p.shouldDeserializeFields(clazz)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public boolean shouldSerializeFields(Class<?> clazz) {
      for (SerializationPolicy p : policies) {
        if (p.shouldSerializeFields(clazz)) {
          return true;
        }
      }
      return false;
    }

    @Override
    public void validateDeserialize(Class<?> clazz)
        throws SerializationException {
      SerializationException se = null;
      for (SerializationPolicy p : policies) {
        try {
          p.validateDeserialize(clazz);
          return;
        } catch (SerializationException e) {
          se = e;
        }
      }
      throw se;
    }

    @Override
    public void validateSerialize(Class<?> clazz)
        throws SerializationException {
      SerializationException se = null;
      for (SerializationPolicy p : policies) {
        try {
          p.validateSerialize(clazz);
          return;
        } catch (SerializationException e) {
          se = e;
        }
      }
      throw se;
    }
  }

}
