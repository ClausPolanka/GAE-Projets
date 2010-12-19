package com.metadot.book.connectr.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.metadot.book.connectr.client.FriendsService;
import com.metadot.book.connectr.server.domain.Friend;
import com.metadot.book.connectr.server.domain.UserAccount;
import com.metadot.book.connectr.shared.FriendDTO;
import com.metadot.book.connectr.shared.FriendSummaryDTO;

/**
 * The FriendsService communicates Friend data via RPC between client and
 * server.
 */
@SuppressWarnings("serial")
public class FriendsServiceImpl extends RemoteServiceServlet implements FriendsService {

	public FriendsServiceImpl() {
		AppMisc.populateDataStoreOnce();
	}

	@Override
	public FriendDTO updateFriend(FriendDTO friendDTO) {
		if (friendDTO.getId() == null) { // create new
			Friend newFriend = addFriend(friendDTO);
			return newFriend.toDTO();
		}

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Friend friend = null;
		try {
			friend = pm.getObjectById(Friend.class, friendDTO.getId());
			friend.updateFromDTO(friendDTO);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return friendDTO;
	}

	@Override
	public Boolean deleteFriend(String id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Friend friend = pm.getObjectById(Friend.class, id);
			if (friend != null) {
				pm.deletePersistent(friend);
				// then delete from associated user's friend list
				UserAccount uinfo = UserAccount.getDefaultUser(pm);
				List<String> fidList = uinfo.getFriendsList();
				fidList.remove(id);
			}
		} finally {
			pm.close();
		}
		return true;
	}

	// create new Friend object in Datastore
	private Friend addFriend(FriendDTO friendDTO) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Friend friend = null;
		try {
			// for this version of the app, just get hardwired 'default' user
			UserAccount currentUser = UserAccount.getDefaultUser(); // detached
																	// object
			currentUser = pm.makePersistent(currentUser); // attach
			friend = new Friend(friendDTO);
			pm.makePersistent(friend);
			currentUser.addFriendKey(friend.getId());
		} finally {
			pm.close();
		}
		return friend;
	}

	@Override
	public ArrayList<FriendSummaryDTO> getFriendSummaries() {

		ArrayList<FriendSummaryDTO> friendsSummaries = new ArrayList<FriendSummaryDTO>();
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			UserAccount user = UserAccount.getDefaultUser(pm);
			List<String> fidList = user.getFriendsList();
			// if the list contains at least one friend ID
			if (fidList.size() > 0) {
				Query q = pm.newQuery("select from " + Friend.class.getName() + " where id == :keys");
				@SuppressWarnings("unchecked")
				List<Friend> friends = (List<Friend>) q.execute(fidList);
				for (Friend friend : friends) {
					friendsSummaries.add(friend.toLightWeightDTO());
				}
			}
		} finally {
			pm.close();
		}

		return friendsSummaries;
	}

	@Override
	public FriendDTO getFriend(String id) {

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Friend dsFriend, detached;

		try {
			dsFriend = pm.getObjectById(Friend.class, id);
			dsFriend.getDetails();
			detached = pm.detachCopy(dsFriend);
		} finally {
			pm.close();
		}

		return detached.toDTO();
	}

} // end class
