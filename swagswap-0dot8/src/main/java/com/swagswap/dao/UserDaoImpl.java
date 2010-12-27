package com.swagswap.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.springframework.orm.jdo.support.JdoDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.apphosting.api.UserServicePb.GetOAuthUserRequest;
import com.swagswap.domain.BlackListedUser;
import com.swagswap.domain.SwagSwapUser;

/**
 * Persistence of Users. Note: Authentication is taken care of by the 
 * Google UserService. 
 * 
 */
@SuppressWarnings("unchecked")
public class UserDaoImpl extends JdoDaoSupport implements UserDao {

	private static final Logger log = Logger.getLogger(UserDaoImpl.class);

	
	public List<SwagSwapUser> getAll() {
		
		PersistenceManager pm = getPersistenceManager();
		String query = "select from " + SwagSwapUser.class.getName() + " order by nickName";
		List<SwagSwapUser> users = (List<SwagSwapUser>) pm.newQuery(query).execute();
		return users;
	}
	
	/* (non-Javadoc)
	 * @see com.swagswap.dao.UserDao#get(java.lang.Long)
	 */
	public SwagSwapUser get(Long id) {
		SwagSwapUser swagSwapUser = getPersistenceManager().getObjectById(SwagSwapUser.class, id);
		return swagSwapUser;
	}
	
	/* 
	 * I learned how to do this here: http://speedo.ow2.org/doc/jdo2qr.html
	 * @returns User if found or null if not
	 */
	public SwagSwapUser findByEmail(String email) {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagSwapUser.class.getName()
				+ " where email==p1 parameters String p1");
		query.setUnique(true);
		return (SwagSwapUser) query.execute(email); 
	}
	
	public SwagSwapUser findByGoogleID(String googleID) {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagSwapUser.class.getName()
				+ " where googleID==p1 parameters String p1");
		query.setUnique(true);
		return (SwagSwapUser) query.execute(googleID); 
	}

	//Can't delete users right now (design decision :)
	// public void delete(Long id) {}

	public void update(SwagSwapUser updatedUser) {
		SwagSwapUser orig = get(updatedUser.getKey());
		orig.setNickName(updatedUser.getNickName());
		orig.setOptOut(updatedUser.getOptOut());
	}
	
	public void optOut(String googleID, boolean optOut) {
		SwagSwapUser orig = findByGoogleID(googleID);
		orig.setOptOut(optOut);
	}

	public void insert(SwagSwapUser swagSwapUser) {
		Date now = new Date();
		swagSwapUser.setJoined(now);
		getPersistenceManager().makePersistent(swagSwapUser);
	}
	
	public void blackListUser(String email) {
		BlackListedUser user = new BlackListedUser(email);
		getPersistenceManager().makePersistent(user);
	}
	
	public boolean isBlackListed(String email) {
		Query query = getPersistenceManager().newQuery(
				"select from " + BlackListedUser.class.getName()
						+ " where email==p1 parameters String p1");
		query.setUnique(true);
		BlackListedUser user = (BlackListedUser) query.execute(email);
		;
		return user != null; // if there's a result they're blacklisted
	}
	
	public List<BlackListedUser> getBlackListedUsers() {
		Query query = getPersistenceManager().newQuery(
				"select from " + SwagSwapUser.class.getName());
		return  (List<BlackListedUser>)query.execute(); 
	}

	
}