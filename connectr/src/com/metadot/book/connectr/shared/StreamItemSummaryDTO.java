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
import java.util.Date;

@SuppressWarnings("serial")
public class StreamItemSummaryDTO implements Serializable {
  private String id;
  private String title;
  private String url;
  private String author;
  private String descrSummary;
  private String imageUrl;
  private Date date;

  public StreamItemSummaryDTO() {
  }

  public StreamItemSummaryDTO(String id, String title, String url, String descrSummary, Date date, String author,
      String imageUrl) {
    this.id = id;
    this.title = title;
    this.url = url;
    this.descrSummary = descrSummary;
    this.date = date;
    this.author = author;
    this.imageUrl = imageUrl;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDescrSummary() {
    return descrSummary;
  }

  public void setDescrSummary(String descrSummary) {
    this.descrSummary = descrSummary;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
  
  public String getImageUrl() {
    return imageUrl;
  }
  
  public String toString() {
    return "********summary: " + " title: " + title + ",\n descr: " + 
      descrSummary + ",\n date: " + date + "\n";
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getAuthor() {
    return author;
  }

}
