package com.swagswap.common;

import com.google.apphosting.api.ApiProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * From Docs.  See http://code.google.com/intl/xx-elmer/appengine/docs/java/howto/unittesting.html
 *
 */
class TestEnvironment implements ApiProxy.Environment {
  public String getAppId() {
    return "test";
  }

  public String getVersionId() {
    return "1.0";
  }

  //assume they're logged in
  public String getEmail() {
    return "test@test.com";
  }

//assume they're logged in
  public boolean isLoggedIn() {
    return true;
  }

  public boolean isAdmin() {
    return false;
  }

  public String getAuthDomain() {
    return "";
  }

  public String getRequestNamespace() {
    return "";
  }

  public Map<String, Object> getAttributes() {
    return new HashMap<String, Object>();
  }
}
