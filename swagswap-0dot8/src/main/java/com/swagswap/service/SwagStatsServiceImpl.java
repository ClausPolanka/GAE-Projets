package com.swagswap.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.swagswap.dao.ItemDao;
import com.swagswap.dao.cache.SwagCacheManager;
import com.swagswap.domain.SwagItem;
import com.swagswap.domain.SwagItemComment;
import com.swagswap.domain.SwagStats;
import com.swagswap.domain.SwagSwapUser;
import com.swagswap.domain.SwagSwapUserStats;

@SuppressWarnings("unchecked")
public class SwagStatsServiceImpl implements SwagStatsService {

	private static final Logger log = Logger
			.getLogger(SwagStatsServiceImpl.class);

	@Autowired
	private ItemService itemService;

	@Autowired
	private SwagSwapUserService swagSwapUserService;

	private static final AverageRatingComparator AVERAGE_RATING_COMPARATOR = new AverageRatingComparator();
	private static final MostRatedComparator MOST_RATED_COMPARATOR = new MostRatedComparator();
	private static final MostCommentedComparator MOST_COMMENTED_COMPARATOR = new MostCommentedComparator();
	private static final UserCreatedComparator USER_CREATED_COMPARATOR = new UserCreatedComparator();
	private static final UserRatedComparator USER_RATED_COMPARATOR = new UserRatedComparator();
	private static final UserCommentedComparator USER_COMMENTED_COMPARATOR = new UserCommentedComparator();

	public SwagStatsServiceImpl() {
		super();
	}
	
	// for unit tests
	protected SwagStatsServiceImpl(ItemService itemService,
			SwagSwapUserService swagSwapUserService) {
		this();
		this.itemService = itemService;
		this.swagSwapUserService = swagSwapUserService;
	}

	public SwagStats getSwagStats() {
		SwagStats swagStats = new SwagStats();

		// Item Stats
		List<SwagItem> swagList = itemService.getAll();
		swagStats.setTotalItems(swagList.size());
		swagStats.setTopRated(getTopRatedSwag(swagList));
		swagStats.setMostRated(getMostRatedSwag(swagList));
		swagStats.setMostCommented(getMostCommentedSwag(swagList));

		// User Stats
		List<SwagSwapUser> userList = swagSwapUserService.getAll();
		swagStats.setTotalUsers(userList.size());
		List<SwagSwapUserStats> userStatsList = populateAllUserStats(userList,
				swagList, swagStats);
		swagStats.setUserCreated(getItemsCreatedPerUser(userStatsList));
		swagStats.setUserRated(getItemsRatedPerUser(userStatsList));
		swagStats.setUserCommented(getItemsCommentedPerUser(userStatsList));

		return swagStats;
	}

	private List<SwagSwapUserStats> getItemsRatedPerUser(
			List<SwagSwapUserStats> allUserStats) {
		// Sort the stats list by userCreated
		Collections.sort(allUserStats, USER_RATED_COMPARATOR);
		// Get top 5 and return as new List
		return new ArrayList<SwagSwapUserStats>(getTop5(allUserStats));
	}

	private List<SwagSwapUserStats> getItemsCreatedPerUser(
			List<SwagSwapUserStats> allUserStats) {
		// Sort the stats list by userCreated
		Collections.sort(allUserStats, USER_CREATED_COMPARATOR);
		// Get top 5 and return as new List
		return new ArrayList<SwagSwapUserStats>(getTop5(allUserStats));
	}

	private List<SwagSwapUserStats> getItemsCommentedPerUser(
			List<SwagSwapUserStats> allUserStats) {
		// Sort the stats list by userCreated
		Collections.sort(allUserStats, USER_COMMENTED_COMPARATOR);
		// Get top 5 and return as new List
		return new ArrayList<SwagSwapUserStats>(getTop5(allUserStats));
	}

	private List<SwagSwapUserStats> populateAllUserStats(
			List<SwagSwapUser> userList, List<SwagItem> swagList,
			SwagStats swagStats) {

		List<SwagSwapUserStats> statsList = new ArrayList<SwagSwapUserStats>();

		String userID = null;

		int totalComments = 0;
		int totalRatings = 0;
		for (SwagSwapUser user : userList) {
			SwagSwapUserStats userStats = new SwagSwapUserStats(user);
			statsList.add(userStats);
			int createdItems = 0;
			int commentedItems = 0;
			int ratedItems = 0;
			userID = user.getGoogleID();
			// Need to find all items with that ID. Hmmm...
			for (SwagItem swagItem : swagList) {
				if (swagItem.getOwnerGoogleID() != null
						&& swagItem.getOwnerGoogleID().equals(userID)) {
					createdItems++;
				}
				// Is this the most efficient way to do this? Probably not.
				for (SwagItemComment swagItemComment : swagItem.getComments()) {
					boolean commentFound = false;
					if (swagItemComment.getItemOwnerGoogleID() != null
							&& swagItemComment.getItemOwnerGoogleID().equals(
									userID)) {
						// Only increment comment once for a user. User may have
						// multiple comments for item. Total comments needs
						// complete count
						if (!commentFound) {
							commentedItems++;
							commentFound = true;
						}
						totalComments++;
					}
				}
				if (user.getSwagItemRating(swagItem.getKey()) != null) {
					// User has rated item
					ratedItems++;
					totalRatings++;
				}

			}

			// We now have total created
			userStats.setItemsCreated(createdItems);
			// Total commented
			userStats.setItemsCommented(commentedItems);
			// And total rated
			userStats.setItemsRated(ratedItems);
		}
		swagStats.setTotalRatings(totalRatings);
		swagStats.setTotalComments(totalComments);

		return statsList;
	}


	private List getTop5(List bigList) {
		return bigList.subList(0, bigList.size() > 5 ? 5 : bigList.size());
	}

	private List<SwagItem> getTopRatedSwag(List<SwagItem> swagList) {
		List<SwagItem> topRated = new ArrayList<SwagItem>();
		for (SwagItem swagItem : swagList) {
			//  Don't count items with < 2 ratings
			if (swagItem.getNumberOfRatings() > 1) {
				topRated.add(swagItem);
			}
			
		}
		Collections.sort(topRated, AVERAGE_RATING_COMPARATOR);
			
		return getTop5(topRated);
	}

	private List<SwagItem> getMostRatedSwag(List<SwagItem> swagList) {
		List<SwagItem> mostRated = new ArrayList<SwagItem>(swagList);
		Collections.sort(mostRated, MOST_RATED_COMPARATOR);
		return getTop5(mostRated);
	}

	private List<SwagItem> getMostCommentedSwag(List<SwagItem> swagList) {
		List<SwagItem> mostCommented = new ArrayList<SwagItem>(swagList);
		Collections.sort(mostCommented, MOST_COMMENTED_COMPARATOR);
		return getTop5(mostCommented);
	}

	private static class AverageRatingComparator implements
			Comparator<SwagItem> {

		public int compare(SwagItem item1, SwagItem item2) {
			if (item1.getAverageRating().equals(item2.getAverageRating())) {
				// Compare by name if ratings are equal
				return item1.getName().compareToIgnoreCase(item2.getName());
			}
			// Descending
			return item2.getAverageRating().compareTo(item1.getAverageRating());
		}
	}

	private static class MostRatedComparator implements Comparator<SwagItem> {

		public int compare(SwagItem item1, SwagItem item2) {
			if (item1.getNumberOfRatings().equals(item2.getNumberOfRatings())) {
				// Compare by name if ratings are equal
				return item1.getName().compareToIgnoreCase(item2.getName());
			}
			// Descending
			return item2.getNumberOfRatings().compareTo(
					item1.getNumberOfRatings());
		}
	}

	private static class MostCommentedComparator implements
			Comparator<SwagItem> {

		public int compare(SwagItem item1, SwagItem item2) {
			if (item1.getNumberOfComments().equals(item2.getNumberOfComments())) {
				// Compare by name if comments are equal
				return item1.getName().compareToIgnoreCase(item2.getName());
			}
			// Descending
			return item2.getNumberOfComments().compareTo(
					item1.getNumberOfComments());
		}
	}

	private static class UserCreatedComparator implements
			Comparator<SwagSwapUserStats> {

		public int compare(SwagSwapUserStats userStats1,
				SwagSwapUserStats userStats2) {
			if (userStats1.getItemsCreated().equals(
					userStats2.getItemsCreated())) {
				// Compare by nickName if stats are equal
				return userStats1.getSwagSwapUser().getNickName()
						.compareToIgnoreCase(
								userStats1.getSwagSwapUser().getNickName());
			}
			// Descending
			return userStats2.getItemsCreated().compareTo(
					userStats1.getItemsCreated());
		}
	}

	private static class UserRatedComparator implements
			Comparator<SwagSwapUserStats> {

		public int compare(SwagSwapUserStats userStats1,
				SwagSwapUserStats userStats2) {
			if (userStats1.getItemsRated().equals(userStats2.getItemsRated())) {
				// Compare by nickName if stats are equal
				return userStats1.getSwagSwapUser().getNickName()
						.compareToIgnoreCase(
								userStats1.getSwagSwapUser().getNickName());
			}
			// Descending
			return userStats2.getItemsRated().compareTo(
					userStats1.getItemsRated());
		}
	}

	private static class UserCommentedComparator implements
			Comparator<SwagSwapUserStats> {

		public int compare(SwagSwapUserStats userStats1,
				SwagSwapUserStats userStats2) {
			if (userStats1.getItemsCommented().equals(
					userStats2.getItemsCommented())) {
				// Compare by nickName if stats are equal
				return userStats1.getSwagSwapUser().getNickName()
						.compareToIgnoreCase(
								userStats1.getSwagSwapUser().getNickName());
			}
			// Descending
			return userStats2.getItemsCommented().compareTo(
					userStats1.getItemsCommented());
		}
	}
}
