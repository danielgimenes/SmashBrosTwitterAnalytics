package br.com.dgimenes.smashbrostwitteranalytics.website.control;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import br.com.dgimenes.smashbrostwitteranalytics.website.exception.TweetNotFoundException;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.Tweet;

@Local
@Stateless
public class TweetController {
	private static final int MIN_NUM_OF_LATEST_TWEETS = 1;
	private static final int MAX_NUM_OF_LATEST_TWEETS = 5;
	@PersistenceContext
	private EntityManager em;

	public List<Tweet> getLatestTweets(int numberOfTweets) {
		List<Tweet> latestTweets = new ArrayList<Tweet>();
		if (numberOfTweets >= MIN_NUM_OF_LATEST_TWEETS && numberOfTweets <= MAX_NUM_OF_LATEST_TWEETS) {
			TypedQuery<Tweet> query = this.em.createNamedQuery("Tweet.latest", Tweet.class);
			query.setMaxResults(numberOfTweets);
			latestTweets = query.getResultList();
		}
		return latestTweets;
	}

	public Long getTweet(Long id) throws TweetNotFoundException {
		Tweet tweet = this.em.find(Tweet.class, id);
		if (tweet == null) {
			throw new TweetNotFoundException(id);
		}
		return tweet.getId();
	}
}
