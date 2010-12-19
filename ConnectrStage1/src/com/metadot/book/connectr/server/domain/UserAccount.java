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

import java.util.HashSet;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.shared.UserAccountDTO;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class UserAccount {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  @Persistent
  private String name;
  
  @Persistent
  private String emailAddress;

  @Persistent(mappedBy = "userAccount") 
  @Element(dependent = "true")
  // if 'friends' were a list, the following could be used to impose 
  // a Datastore-friendly sort on the list.  For a set, this is not
  // necessary.
  // @Order(extensions = @Extension(vendorName="datanucleus",
  // key="list-ordering", value="id asc"))
  private Set<Friend> friends = new HashSet<Friend>();


  public UserAccount() {
  }

  // for this initial v. of the app, the 'current user' is always the same
  // (the default user), so this query is hardwired
  public static UserAccount getDefaultUser() {
    
    String defaultEmail = "default@default.com";
    
    PersistenceManager pm = PMF.get().getPersistenceManager();
    UserAccount oneResult = null, detached = null;
    Query q = pm.newQuery(UserAccount.class, "emailAddress == :email");
    q.setUnique(true);
    try {
      oneResult = (UserAccount) q.execute(defaultEmail);
      if (oneResult != null) {
      // fetch friends list before detaching
        oneResult.getFriends();
        detached = pm.detachCopy(oneResult);
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pm.close();
      q.closeAll();
    }
    return detached;
  }

  public static UserAccount getDefaultUser(PersistenceManager pm) {
    
    String defaultEmail = "default@default.com";
    
    UserAccount oneResult = null;
    Query q = pm.newQuery(UserAccount.class, "emailAddress == :email");
    q.setUnique(true);
    try {
      oneResult = (UserAccount) q.execute(defaultEmail);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      q.closeAll();
    }
    return oneResult;
  }

  public void setBasicInfo(String name, String emailAddress) {
    this.name = name;
    this.emailAddress = emailAddress;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public void addFriend(Friend fr) {
    friends.add(fr);
  }

  public Set<Friend> getFriends() {
    return friends;
  }

  public static UserAccountDTO toDTO(UserAccount user) {
    if (user == null) {
      return null;
    }
    return new UserAccountDTO(user.getEmailAddress(), user.getName());
  }

}
