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
import com.metadot.book.connectr.client.MessagesService;
import com.metadot.book.connectr.shared.MessageDTO;

/**
 * MessagesServiceImpl is just a placeholder in this stage of the Connectr app.  
 * It communicates hardwired sample status items for Connectr's main 'activity' panel.  In the 
 * final version of Connectr, live data will be used instead.
 */
@SuppressWarnings("serial")
public class MessagesServiceImpl extends RemoteServiceServlet implements
    MessagesService {

  public MessagesServiceImpl() {
  }

  public List<MessageDTO> getMessages(Set<Long> friendIds) {
    List<MessageDTO> messageDTOs = new ArrayList<MessageDTO>();

    messageDTOs.add(new MessageDTO("I am at the airport",
        "I am at the airport. Just wanted to share that.",
        new Date()));
    
    messageDTOs.add(new MessageDTO(
        "I am at the BitBucket pub 6th @ Brazos, join me there", "",
        new Date()));
    
    messageDTOs.add(new MessageDTO("Driving in the rain...",
        "I should not be tweeting....", new Date()));
    
    messageDTOs
        .add(new MessageDTO(
            "Just watched SuperSize me: watch it too!",
            "Incredible: a guy almost died eating at McDonald 3 times a day for a month...", new Date()));
    
    messageDTOs.add(new MessageDTO("Strictly observing SysAdmin day: sleep(8).",
        "", new Date()));
    
    messageDTOs
        .add(new MessageDTO(
            "Sean Wrona wins Ultimate Typing Championship",
            "163 wpm , 100% accuracy", new Date()));
    
    messageDTOs.add(new MessageDTO(
        "Using WatchIt for screencast, pretty good so far...", "",
        new Date()));
    
    messageDTOs
        .add(new MessageDTO(
            "Cloudy, rainy, no TV. This is my last post...",
            "", new Date()));

    return messageDTOs;
  }

} // end class
