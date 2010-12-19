
THE GAE CHANNEL API 
-------------------

In Connectr, we use App Engine's new Channel API to push messages from the server to the client, triggering an update of the 'activity stream' contents of Connectr's main panel.  

The Channel API is unsupported in GAE 1.3.8, and only works in local development mode-- its classes are included in the App Engine libraries, so it can be used locally, but as of GAE 1.3.8, it is not yet officially supported for deployed appspot.com apps.  
So, this feature is disabled by default. This file describes how to enable it.

If the Channel API is not enabled, then a user of the app must click Connectr's "Refresh" button manually to have the stream content in Connectr's main panel updated.
So, in a deployed version of Connectr, with GAE 1.3.8, you must click "Refresh" to see new content.

The Channel API will be supported in GAE 1.4.0, which as this book goes to press is not yet released. When GAE 1.4.0 is released, you should be able to deploy a Channel-enabled version of Connectr.

ENABLING THE CHANNEL API 
------------------------

To enable use of the Channel API in local development mode, do the following:

 - edit war/ConnectrApp.html, and uncomment the line:
  <script src="/_ah/channel/jsapi"></script> 
	
 - in war/WEB-INF/appengine-web.xml, change the "com.metadot.connectr.enable-channelapi" system property from "false" to "true"

Do this next step only if you are using a GAE version prior to GAE 1.4.0:
 - copy the stagingfor_ah/_ah directory to under the war directory (so, you should end up with a war/_ah directory)
You do not need to create this directory with GAE 1.4.0.

To deploy the app under GAE 1.3.8, you will need to undo these actions.  [Note that the war/_ah directory will not deploy successfully to appspot.com-- you will see an error if it exists when you initiate deployment].

When you start up the app in your development environment with the Channel API enabled, you may sometimes see the following message in the console on first load of the app:

  [WARN] /_ah/channel/dev
  com.google.appengine.api.channel.dev.LocalChannelFailureException: Channel for application key null not found.
    at com.google.appengine.api.channel.dev.ChannelManager.getChannel(ChannelManager.java:58)
    ...
Watch for this message.  If you see it, just restart the app, and the second startup should be unproblematic. The channel API will not work properly if the message above was displayed on startup.
