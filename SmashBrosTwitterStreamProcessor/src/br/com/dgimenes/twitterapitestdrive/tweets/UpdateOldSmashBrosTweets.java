package br.com.dgimenes.twitterapitestdrive.tweets;

import java.sql.SQLException;
import java.util.List;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class UpdateOldSmashBrosTweets {

	public static void main(String[] args) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(AuthenticationData.API_KEY)
				.setOAuthConsumerSecret(AuthenticationData.API_SECRET)
				.setOAuthAccessToken(AuthenticationData.ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(AuthenticationData.ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		try {
			List<Long> ids = SmashBrosDatabaseServices.getInstance().getAllTweetIdsWhereLangIsNull();
			for (Long tweetId : ids) {
				try {
					Status status = twitter.showStatus(tweetId);
					long rtId = status.getRetweetedStatus() == null ? 0 : status.getRetweetedStatus().getId();
					String lang = status.getLang();
					boolean isRT = status.isRetweet();
					SmashBrosDatabaseServices.getInstance().updateTweetRTandLang(tweetId, isRT, rtId, lang);
				} catch (TwitterException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("finished");
	}
}
