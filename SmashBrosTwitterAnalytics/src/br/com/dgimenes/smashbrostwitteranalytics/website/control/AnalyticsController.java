package br.com.dgimenes.smashbrostwitteranalytics.website.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.TweetCountPerHour;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.WordCount;

@Local
@Stateless
public class AnalyticsController {
	private static final SimpleDateFormat queryDateFormat = new SimpleDateFormat("yyyyMMddHH");

	@PersistenceContext
	private EntityManager em;

	public Long getTweetCount() {
		Query query = this.em.createNamedQuery("Tweet.count");
		Long tweetCount = (Long) query.getSingleResult();
		return tweetCount;
	}

	public List<TweetCountPerHour> getTweetCountPerHour(Date startTime, Date endTime) {
		List<TweetCountPerHour> tweetCountPerHour = new ArrayList<TweetCountPerHour>();
		Query query = this.em.createNamedQuery("Tweet.countPerHour");
		query.setParameter(1, startTime);
		query.setParameter(2, endTime);
		List<Object[]> countPerHour = query.getResultList();
		for (Object[] tweetCount : countPerHour) {
			try {
				tweetCountPerHour.add(new TweetCountPerHour(queryDateFormat.parse((String) tweetCount[0]),
						(Long) tweetCount[1]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return tweetCountPerHour;
	}

	public List<WordCount> getMostUsedWordsOnTweets() {
		List<WordCount> mostUsedWordsOnTweets = new ArrayList<WordCount>();
		// Query query = this.em.createNamedQuery("Tweet.mostUsedWords");
		// TODO
		mostUsedWordsOnTweets.add(new WordCount("Pikachu", 98));
		mostUsedWordsOnTweets.add(new WordCount("Mario", 120));
		mostUsedWordsOnTweets.add(new WordCount("awesome", 100));
		mostUsedWordsOnTweets.add(new WordCount("friday", 80));
		mostUsedWordsOnTweets.add(new WordCount("hello", 20));
		mostUsedWordsOnTweets.add(new WordCount("wait", 67));

		mostUsedWordsOnTweets.add(new WordCount("Luigi", 98));
		mostUsedWordsOnTweets.add(new WordCount("Damn", 120));
		mostUsedWordsOnTweets.add(new WordCount("time", 200));
		mostUsedWordsOnTweets.add(new WordCount("moment", 80));
		mostUsedWordsOnTweets.add(new WordCount("Brawl", 20));
		mostUsedWordsOnTweets.add(new WordCount("Nintendo", 207));

		mostUsedWordsOnTweets.add(new WordCount("Pikachu", 98 * 2));
		mostUsedWordsOnTweets.add(new WordCount("Mario", 120 * 2));
		mostUsedWordsOnTweets.add(new WordCount("awesome", 100 * 2));
		mostUsedWordsOnTweets.add(new WordCount("friday", 80 * 2));
		mostUsedWordsOnTweets.add(new WordCount("hello", 20 * 2));
		mostUsedWordsOnTweets.add(new WordCount("wait", 67 * 2));

		mostUsedWordsOnTweets.add(new WordCount("Luigi", 98 * 2));
		mostUsedWordsOnTweets.add(new WordCount("Damn", 120 * 2));
		mostUsedWordsOnTweets.add(new WordCount("time", 200 * 2));
		mostUsedWordsOnTweets.add(new WordCount("moment", 80 * 2));
		mostUsedWordsOnTweets.add(new WordCount("Brawl", 20 * 2));
		mostUsedWordsOnTweets.add(new WordCount("Nintendo", 207 * 2));
		return mostUsedWordsOnTweets;
	}
}
