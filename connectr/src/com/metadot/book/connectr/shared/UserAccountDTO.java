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
package com.metadot.book.connectr.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserAccountDTO implements Serializable {

  private String id;
  private String name;
  private String emailAddress;
  private String channelId;
  private String uniqueId;

  public UserAccountDTO() {
  
  }

  public UserAccountDTO(String email, String name, String uniqueId) {
    this();
    this.setEmailAddress(email);
    this.setName(name);
    this.setUniqueId(uniqueId);
  }


  public String getUniqueId() {
    return uniqueId;
  }

  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setChannelId(String channelId) {
    this.channelId = channelId;
  }

  public String getChannelId() {
    return channelId;
  }

}
