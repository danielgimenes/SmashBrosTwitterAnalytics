package br.com.dgimenes.smashbrostwitteranalytics.website.control;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import br.com.dgimenes.smashbrostwitteranalytics.website.model.SmashBrosCharacter;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.CharacterRankPosition;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.TweetCountPerDay;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.TweetCountPerHour;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.dto.WordCount;

@Local
@Stateless
public class AnalyticsController {
	private static final SimpleDateFormat queryHourFormat = new SimpleDateFormat("yyyyMMddHH");
	private static final SimpleDateFormat queryDayFormat = new SimpleDateFormat("yyyyMMdd");

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
				tweetCountPerHour.add(new TweetCountPerHour(queryHourFormat.parse((String) tweetCount[0]),
						(Long) tweetCount[1]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return tweetCountPerHour;
	}

	public List<TweetCountPerDay> getTweetCountPerDay(Date startTime, Date endTime) {
		List<TweetCountPerDay> tweetCountPerDay = new ArrayList<TweetCountPerDay>();
		Query query = this.em.createNamedQuery("Tweet.countPerDay");
		query.setParameter(1, startTime);
		query.setParameter(2, endTime);
		List<Object[]> countPerDay = query.getResultList();
		for (Object[] tweetCount : countPerDay) {
			try {
				tweetCountPerDay.add(new TweetCountPerDay(queryDayFormat.parse((String) tweetCount[0]),
						(Long) tweetCount[1]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return tweetCountPerDay;
	}

	public List<WordCount> getMostUsedWordsOnTweets() {
		List<WordCount> mostUsedWordsOnTweets = new ArrayList<WordCount>();
		Query query = this.em.createNamedQuery("Tweet.mostUsedWords");
		List<Object[]> wordCountsPlain = query.getResultList();
		for (Object[] counts : wordCountsPlain) {
			mostUsedWordsOnTweets.add(new WordCount((String) counts[0], (Long) counts[1]));
		}
		return mostUsedWordsOnTweets;
	}

	public List<CharacterRankPosition> getCharactersRank() {
		List<CharacterRankPosition> rank = new LinkedList<CharacterRankPosition>();
		Query query = this.em.createNamedQuery("Tweet.charRank");
		List<Object[]> rankPlain = query.getResultList();
		for (Object[] characterRefs : rankPlain) {
			rank.add(new CharacterRankPosition(SmashBrosCharacter.valueOf((String) characterRefs[0]),
					(Long) characterRefs[1]));
		}
		// reverse sort order
		rank.sort(new Comparator<CharacterRankPosition>() {
			@Override
			public int compare(CharacterRankPosition o1, CharacterRankPosition o2) {
				return (int) (o1.getRefs() - o2.getRefs());
			}
		});
		return rank;
	}
}
