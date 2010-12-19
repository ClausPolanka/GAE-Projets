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
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.labs.taskqueue.QueueFactory;
import com.google.appengine.api.labs.taskqueue.Queue;
import static com.google.appengine.api.labs.taskqueue.TaskOptions.Builder.*;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.service.MessagesService;
import com.metadot.book.connectr.server.PMF;
import com.metadot.book.connectr.server.domain.FeedIndex;
import com.metadot.book.connectr.server.domain.StreamItem;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.server.utils.ListPartition;
import com.metadot.book.connectr.server.utils.cache.CacheSupport;
import com.metadot.book.connectr.shared.StreamItemDTO;
import com.metadot.book.connectr.shared.StreamItemSummaryDTO;

/**
 * MessagesServiceImpl provides the server-side part of MessagesService.
 * It provides the 'activity' stream data for Connectr's main panel, 
 * in the form of StreamItemSummaryDTOs, and does much of the query and cache
 * management necessary to support this. 
 */
@SuppressWarnings("serial")
public class MessagesServiceImpl extends RemoteServiceServlet implements
  MessagesService {

  private static Logger logger = Logger.getLogger(MessagesServiceImpl.class.getName());
  private static String XMPP_FEED = "xmpp";
  private static int MAXFLIST = 28;
  private static Properties props = System.getProperties();
  private static final int CACHE_EXPIR = 600;  // in seconds

  private Set<StreamItem> entries;
  private Set<StreamItemSummaryDTO> summaries;
  private List <StreamItemSummaryDTO> summaryList;
  private String feedids_nmspce;
  HttpSession session;

  public MessagesServiceImpl() {
    feedids_nmspce = props.getProperty("com.metadot.connectr.feedids-cache");
  }

  public List<StreamItemSummaryDTO> getNLastMessages(Set<String> friendIds, int numMsgs) {

    logger.info("in getNLastMessages; numMsgs: " + numMsgs + ", friendIds: " + friendIds);
    
    PersistenceManager pm = PMF.getNonTxnPm();
    Set<String> feedids = new HashSet<String>();

    try {
      boolean all = false;
      if (friendIds == null) {
        all = true;
      }
      feedids = getFeedIds(friendIds, pm);
      boolean fetchEntries = false;
      fetchPrior(null, feedids, pm, fetchEntries, numMsgs, all);

    } // end try
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
    finally {
      pm.close();
    }
    return summaryList;
  }

  public List<StreamItemSummaryDTO> getMessagesSince(Set<String> friendIds, Date since) {

    PersistenceManager pm = PMF.getNonTxnPm();
    Set<String> feedids = new HashSet<String>();

    try {
      boolean all = false;
      if (friendIds == null) {
        all = true;
      }
      feedids = getFeedIds(friendIds, pm);
      boolean fetchEntries = false;
      fetchSince(since, feedids, pm, fetchEntries, all);
    } // end try
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
    finally {
      pm.close();
    }

    return summaryList;
  }

  public List<StreamItemSummaryDTO> getMessagesPrior (Set<String> friendIds, Date prior, int numMsgs) {

    PersistenceManager pm = PMF.getNonTxnPm();
    Set<String> feedids = new HashSet<String>();

    try {
      boolean all = false;
      if (friendIds == null) {
        all = true;
      }
      feedids = getFeedIds(friendIds, pm);
      boolean fetchEntries = false;
      fetchPrior(prior, feedids, pm, fetchEntries, numMsgs, all);
    } // end try
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
    finally {
      pm.close();
    }

    return summaryList;
  }

  public StreamItemDTO getStreamItemDetails(String id) {

    PersistenceManager pm = PMF.getNonTxnPm();
    StreamItem streamItem = null;

    try {
      // first see if object is in cache-- it should be
      Object o = CacheSupport.cacheGet(StreamItem.class.getName(), id);
      if (o != null && o instanceof StreamItem) {
        streamItem = (StreamItem) o;
        // logger.fine("in getStreamItemDetails, got cache hit");
      }
      else {
        streamItem = pm.detachCopy(pm.getObjectById(StreamItem.class, id));
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
    finally {
      pm.close();
    }
    // can't just use the detached StreamItem as a DTO, 
    // as it has an App Engine Text field 
    // that needs to be converted to a String.
    return streamItem.toDTO();
  }

  // start a task to update the feeds for the current user.
  public Boolean initiateUserFeedUpdate() {

    PersistenceManager pm = PMF.getNonTxnPm();

    try {
      UserAccount user = LoginHelper.getLoggedInUser(getThreadLocalRequest().getSession(), pm);
      Queue queue = QueueFactory.getQueue("userfeedupdates");
      // add a task to update all feeds for the friends of the given user.
      // the 'notify' parameter will trigger channel API notification 
      // afterwards (if channel API is enabled)
      queue.add(url("/feedupdateuser").param("uid", user.getUniqueId()).param("notify", "true"));
    }
    catch (Exception e) {
      logger.warning(e.getMessage());
      return false;
    }
    finally {
      pm.close();
    }
    return true;
  }


  @SuppressWarnings("unchecked")
  private Set<String> getFeedIds(Set<String> friendIds, PersistenceManager pm) {

    HashSet<String> feedids = new HashSet<String>();

    if (friendIds != null) {
      // construct from set of friendIds
      Query q = pm.newQuery("select key from " + FeedIndex.class.getName() + " where friendKeys == :id");
      for (String friendid : friendIds) {
        // check for cached info
        Object finfo = CacheSupport.cacheGet(feedids_nmspce, friendid);
        if (finfo != null && finfo instanceof Set<?> ) {
          // logger.fine("got cache hit for " + friendid + ", adding: " + (Set)finfo);
          feedids.addAll((Set<String>)finfo);
        }
        else {
          // logger.fine("got cache miss for " + friendid);
          List<?> ids = (List<?>) q.execute(friendid);
          HashSet<String> cachedURLs = new HashSet<String>();
          Key k;
          for (Object o : ids) {
            k = (Key) o;
            feedids.add(k.getParent().getName());
            cachedURLs.add(k.getParent().getName());
          }
          if (cachedURLs.size() > 0) {
            // logger.fine("caching " + cachedURLs + " for " + friendid);
            CacheSupport.cachePutExp(feedids_nmspce, friendid, cachedURLs, CACHE_EXPIR);
          }
        }
      }
    }

    // add any xmpp news-gateway-derived items as a feed id source
    // (there may not be any)
    feedids.add(XMPP_FEED);
    return feedids;
  }

  private void fetchSince(Date sdate, Set<String> feedids, PersistenceManager pm, boolean fetchEntries, boolean all) {

    fetchStreamItems( sdate, feedids, pm, fetchEntries, false, -1, all);

    if (fetchEntries) {
      new ArrayList<StreamItem>(entries);
    }
    summaryList = new ArrayList<StreamItemSummaryDTO>(summaries);
  }

  private void fetchPrior(Date sdate, Set<String> feedids, PersistenceManager pm, boolean fetchEntries,
      int range, boolean all) {

    fetchStreamItems( sdate, feedids, pm, fetchEntries, true, range, all);

    List<StreamItem> t1;
    List<StreamItemSummaryDTO> t2;
    try {
      if (fetchEntries) {
        if (entries.size() > range) {
          t1 = (new ArrayList<StreamItem>(entries)).subList(0, range - 1);
          new ArrayList<StreamItem>(t1);
        }
        else {
          new ArrayList<StreamItem>(entries);
        }
      }
      if (summaries.size() > range) {
        t2 = (new ArrayList<StreamItemSummaryDTO>(summaries)).subList(0, range - 1);
        // necessary to avoid serialization errors
        summaryList = new ArrayList<StreamItemSummaryDTO>(t2);
      }
      else {
        summaryList = new ArrayList<StreamItemSummaryDTO>(summaries);
      }
    }
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
  }

  private void fetchStreamItems(Date sdate, Set<String> feedids, PersistenceManager pm, 
    boolean fetchEntries, boolean prior, int range, boolean all) {
      
    // create a sorted set for the entries
    // sort is by publication date
    if (fetchEntries) {
      entries = new TreeSet<StreamItem>(new Comparator<StreamItem>() {
        public int compare(StreamItem o1, StreamItem o2) {
          if (o1.getDate() != null) {
            if (o2.getDate() != null) {
              return o2.getDate().compareTo(o1.getDate());
            } else {
              return -1;
            }}
          else {
            return 1;
          }
        }});
    }
    else {
      entries = null;
    }

    summaries = new TreeSet<StreamItemSummaryDTO>(new Comparator<StreamItemSummaryDTO>() {
      public int compare(StreamItemSummaryDTO o1, StreamItemSummaryDTO o2) {
        if (o1.getDate() != null) {
          if (o2.getDate() != null) {
            return o2.getDate().compareTo(o1.getDate());
          } else {
            return -1;
          }}
        else {
          return 1;
        }
      }});
        
    if (all) {
      fetchBatch(sdate, pm, fetchEntries, prior, range);
      // pick up the xmpp feed
      PersistenceManager pm2 = PMF.getNonTxnPm();
      fetchForFeeds(sdate, feedids, pm2, fetchEntries, prior, range);
    }
    else {
      fetchForFeeds(sdate, feedids, pm, fetchEntries, prior, range);
    }
    
  }

  @SuppressWarnings("unchecked")
  private void fetchBatch(Date sdate, PersistenceManager pm, boolean fetchEntries, boolean prior, int range) {

    List<String> fetchlist = new ArrayList<String>();
    StreamItem entry = null;

    try {
      // do a query that fetches the stream items based on the UserAccount id.
      UserAccount user = LoginHelper.getLoggedInUser(getThreadLocalRequest().getSession(), pm);
      Long userid = user.getId();
      String qstring = null;
      if (sdate == null) {
        qstring = " where ukeys == :u1";
      }
      else if (prior) {
        qstring = " where date < :d1 && ukeys == :u1";
      }
      else {
        qstring = " where date >= :d1 && ukeys == :u1";
      }
      Query q = pm.newQuery("select id from " + StreamItem.class.getName() + qstring);
      q.setOrdering("date desc");
      if (prior) {
        q.setRange(0, range);
      }
      q.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");
      List<String> entryids;
      if (sdate != null) {
        entryids = (List<String>) q.execute(sdate, userid);
      }
      else {
        entryids = (List<String>) q.execute(userid);
      }
      // for each id, check for cached object(s) as appropriate; 
      // if not available, add to list of streamitem ids for which the objects 
      // need to be fetched
      // if available, add to list which will eventually need to be sorted.
      Object o = null, o2 = null;
      for (String eid : entryids) {
        if (fetchEntries) {
          o = CacheSupport.cacheGet(StreamItem.class.getName(), eid);
          if (o != null && o instanceof StreamItem) {
            entry = (StreamItem) o;
            entries.add(entry);
            // add summary to summaries list 
            o2 = CacheSupport.cacheGet(StreamItemSummaryDTO.class.getName(), eid);
            if (o2 != null && o2 instanceof StreamItemSummaryDTO) {
              summaries.add((StreamItemSummaryDTO) o2);
            }
            else {
              // this case should not come up unless item removed from cache by system
              summaries.add(entry.addSummaryToCache());
            }
          }
          else {
            fetchlist.add(eid);
          }
        }
        else {
          // fetch just summaries
          o2 = CacheSupport.cacheGet(StreamItemSummaryDTO.class.getName(), eid);
          if (o2 != null && o2 instanceof StreamItemSummaryDTO) {
            // logger.info("got cache hit");
            summaries.add((StreamItemSummaryDTO) o2);
          }
          else {
            // is the stream item cached?
            o = CacheSupport.cacheGet(StreamItem.class.getName(), eid);
            if (o != null && o instanceof StreamItem) {
              // logger.info("got cache hit on stream item " + StreamItem.class.getName() + ", \n" + eid);
              entry = (StreamItem) o;
              // add summary to summaries list 
              summaries.add(entry.addSummaryToCache());
            }
            else {
              fetchlist.add(eid);
            }
          }}
      } // end for
      if (fetchlist.size() > 0) {
        // now fetch the streamitem ids which weren't in the cache, using their list of ids.
        Query q2 = pm.newQuery("select from " + StreamItem.class.getName() + " where id == :keys");
        q2.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");
        List<StreamItem> entries2 = (List<StreamItem>) q2.execute(fetchlist);

        // add the fetched entries to the cache, and to the previous list of items fetched 
        // from the cache.
        for (StreamItem e2 : entries2) {
          summaries.add(e2.addToCacheGetSumm());
          if (fetchEntries) {
            entries.add(e2);
          }
        }
      }
    } // end try
    catch (Exception e) {
      e.printStackTrace();
    }
  }


  @SuppressWarnings("unchecked")
  private void fetchForFeeds(Date sdate, Set<String> feedids, PersistenceManager pm,
      boolean fetchEntries, boolean prior, int range) {

    try {
      List<String> fetchlist = new ArrayList<String>();
      StreamItem entry = null;
      Query dq = null, q2 = null;
      String qstring = null;
      if (sdate == null) {
        qstring = " where :f1.contains(feedUrl)";
      }
      else if (prior) {
        qstring = " where date < :d1 && :f1.contains(feedUrl)";
      }
      else {
        qstring = " where date >= :d1 && :f1.contains(feedUrl)";
      }

      // break feedids list into sublists so don't reach Datastore query max
      List<List<String>> partition = ListPartition.partition(new ArrayList<String>(feedids),
          MAXFLIST);

      // for each sublist of feed ids
      for (List<String> fsublist : partition) {
        dq = pm.newQuery("select id from " + StreamItem.class.getName() + qstring);
        dq.setOrdering("date desc");
        if (prior) {
          dq.setRange(0, range);
        }
        dq.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");

        List<String> entryids;
        if (sdate != null) {
          entryids = (List<String>) dq.execute(sdate, fsublist);
        }
        else {
          entryids = (List<String>) dq.execute(fsublist);
        }

        // for each id, check for cached object(s) as appropriate; 
        // if not available, add to list of streamitem ids for which the objects 
        // need to be fetched
        // if available, add to list which will eventually need to be sorted.
        Object o = null, o2 = null;
        for (String eid : entryids) {
          if (fetchEntries) {
            o = CacheSupport.cacheGet(StreamItem.class.getName(), eid);
            if (o != null && o instanceof StreamItem) {
              entry = (StreamItem) o;
              entries.add(entry);
              // add summary to summaries list 
              o2 = CacheSupport.cacheGet(StreamItemSummaryDTO.class.getName(), eid);
              if (o2 != null && o2 instanceof StreamItemSummaryDTO) {
                // cache hit
                summaries.add((StreamItemSummaryDTO) o2);
              }
              else {
                // this case should not come up unless item removed from cache by system
                summaries.add(entry.addSummaryToCache());
              }
            }
            else {
              fetchlist.add(eid);
            }
          }
          else {
            // fetch just summaries
            o2 = CacheSupport.cacheGet(StreamItemSummaryDTO.class.getName(), eid);
            if (o2 != null && o2 instanceof StreamItemSummaryDTO) {
              // cache hit
              summaries.add((StreamItemSummaryDTO) o2);
            }
            else {
              // is the stream item itself cached?
              o = CacheSupport.cacheGet(StreamItem.class.getName(), eid);
              if (o != null && o instanceof StreamItem) {
                // logger.info("got cache hit on stream item " + StreamItem.class.getName() + ", \n" + eid);
                entry = (StreamItem) o;
                // add summary to summaries list 
                summaries.add(entry.addSummaryToCache());
              }
              else {
                fetchlist.add(eid);
              }
            }
          }
        }
      }
      if (fetchlist.size() > 0) {
        // now fetch the streamitem ids which weren't in the cache, using their list of ids.
        q2 = pm.newQuery("select from " + StreamItem.class.getName() + " where id == :keys");
        q2.addExtension("datanucleus.appengine.datastoreReadConsistency", "EVENTUAL");
        List<StreamItem> entries2 = (List<StreamItem>) q2.execute(fetchlist);

        // add the fetched entries to the cache, and to the previous list of items fetched 
        // from the cache.
        for (StreamItem e2 : entries2) {
          summaries.add(e2.addToCacheGetSumm());
          if (fetchEntries) {
            entries.add(e2);
          }
        }
      }
    } // end try
    catch (Exception e) {
      // e.printStackTrace();
      logger.warning("exception: " + e.getMessage());
    }
  }

} // end class
