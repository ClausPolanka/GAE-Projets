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
package com.metadot.book.connectr.server.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * This class is used only in processing incoming XMPP messages sent using 
 * the same schema
 */
@SuppressWarnings("serial")
public class SimpleItem implements Serializable {

  private String id;
  private String ttext;
  private String source;
  private String tname;
  private Long tid;
  private Date tdate;

  public String getTtext() {
    return ttext;
  }

  public void setTtext(String ttext) {
    this.ttext = ttext;
  }
  
  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }
  
  public String getTname() {
    return tname;
  }

  public void setTname(String tname) {
    this.tname = tname;
  }

  public Long getTid() {
    return tid;
  }

  public void setTid(Long tid) {
    this.tid = tid;
  }

  public Date getTdate() {
    return tdate;
  }

  public void setTdate(Date tdate) {
    this.tdate = tdate;
  }

  public String getId() {
    return id;
  }

  public String toString() {
    return "\n*** " + ttext + "\nsource: " + source + ", date: " + tdate + ", " + tid;
  }

}

