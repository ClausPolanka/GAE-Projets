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

/**
 * StreamItemDTO provides a 'full' DTO for a StreamItem.  
 */
@SuppressWarnings("serial")
public class StreamItemDTO implements Serializable {

  private String id;

  private String url;
  private String title;
  private String description;
  private String descrSummary;
  private String feedDescription;
  private String feedUrl;
  private String imageUrl;
  private String feedTitle;
  private String author;
  private Date date;
  
  public StreamItemDTO() {
  }
  
  public StreamItemDTO(String id, String url, String title, String description,
    String descrSummary, String feedDescription, String feedUrl,
    String imageUrl, String feedTitle, String author, Date date) {
      
    this.id = id;
    this.url = url;
    this.title = title;
    this.description = description;
    this.descrSummary = descrSummary;
    this.feedDescription = feedDescription;
    this.feedUrl = feedUrl;
    this.imageUrl = imageUrl;
    this.feedTitle = feedTitle;
    this.author = author;
    this.date = date;
  }


  @Override
  public String toString() {
  return "StreamItemDTO [author=" + author + ", date=" + date
      + ", descrSummary=" + descrSummary + ", description=" + description
      + ", feedDescription=" + feedDescription + ", feedTitle="
      + feedTitle + ", feedUrl=" + feedUrl + ", id=" + id + ", imageUrl="
      + imageUrl + ", title=" + title + ", url=" + url + "]";
  }

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public String getDescrSummary() {
    return descrSummary;
  }
  public void setDescrSummary(String descrSummary) {
    this.descrSummary = descrSummary;
  }
  public String getFeedDescription() {
    return feedDescription;
  }
  public void setFeedDescription(String feedDescription) {
    this.feedDescription = feedDescription;
  }
  public String getFeedUrl() {
    return feedUrl;
  }
  public void setFeedUrl(String feedUrl) {
    this.feedUrl = feedUrl;
  }
  public String getImageUrl() {
    return imageUrl;
  }
  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
  public String getFeedTitle() {
    return feedTitle;
  }
  public void setFeedTitle(String feedTitle) {
    this.feedTitle = feedTitle;
  }
  public String getAuthor() {
    return author;
  }
  public void setAuthor(String author) {
    this.author = author;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }

}
