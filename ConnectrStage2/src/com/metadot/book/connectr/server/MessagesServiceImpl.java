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
package com.metadot.book.connectr.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.service.MessagesService;
import com.metadot.book.connectr.shared.StreamItemSummaryDTO;

@SuppressWarnings("serial")
public class MessagesServiceImpl extends RemoteServiceServlet implements
    MessagesService {

/**
 * for this early-stage version of the app, this service just generates a 
 * static set of hardwired 'status items'.  
 * In the full app, the items will be drawn from feed content instead.
 */
  public MessagesServiceImpl() {
  }

  public List<StreamItemSummaryDTO> getMessages(Set<String> friendIds) {
    List<StreamItemSummaryDTO> StreamItemSummaryDTOs = new ArrayList<StreamItemSummaryDTO>();

    StreamItemSummaryDTOs.add(new StreamItemSummaryDTO("I am at the airport",
        "I am at the airport. Just wanted to share that.",
        new Date()));
    
    StreamItemSummaryDTOs.add(new StreamItemSummaryDTO(
        "I am at the BitBucket pub 6th @ Brazos, join me there", "",
        new Date()));
    
    StreamItemSummaryDTOs.add(new StreamItemSummaryDTO("Driving in the rain...",
        "I should not be tweeting....", new Date()));
    
    StreamItemSummaryDTOs
        .add(new StreamItemSummaryDTO(
            "Just watched SuperSize me: watch it too!",
            "Incredible: a guy almost died eating at McDonald 3 times a day for a month...", new Date()));
    
    StreamItemSummaryDTOs.add(new StreamItemSummaryDTO("Strictly observing SysAdmin day: sleep(8).",
        "", new Date()));
    
    StreamItemSummaryDTOs
        .add(new StreamItemSummaryDTO(
            "Sean Wrona wins Ultimate Typing Championship",
            "163 wpm , 100% accuracy", new Date()));
    
    StreamItemSummaryDTOs.add(new StreamItemSummaryDTO(
        "Using WatchIt for screencast, pretty good so far...", "",
        new Date()));
    
    StreamItemSummaryDTOs
        .add(new StreamItemSummaryDTO(
            "Cloudy, rainy, no TV. This is my last post...",
            "", new Date()));

    return StreamItemSummaryDTOs;
  }

} // end class
