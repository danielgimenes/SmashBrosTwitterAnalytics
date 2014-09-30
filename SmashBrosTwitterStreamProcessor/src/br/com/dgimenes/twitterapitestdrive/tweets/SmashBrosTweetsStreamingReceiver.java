package br.com.dgimenes.twitterapitestdrive.tweets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SmashBrosTweetsStreamingReceiver {

	public static void main(String[] args) throws TwitterException, IOException {
		StatusListener listener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				long tweetId = status.getId();
				Date tweetTime = status.getCreatedAt();
				String userName = status.getUser().getName();
				String tweet = status.getText();
				long rtId = status.getRetweetedStatus() == null ? 0 : status.getRetweetedStatus().getId();
				String lang = status.getLang();
				boolean isRT = status.isRetweet();
				System.out.println((isRT ? "RT " : "") + String.format("[%1$d] %2$s: %3$s", tweetId, userName, tweet));
				try {
					SmashBrosDatabaseServices.getInstance().persistTweet(tweetId, tweetTime, isRT, rtId, lang,
							userName, tweet);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.err.println("StatusDeletionNotice");
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.err.println("ERROR: MISSED " + numberOfLimitedStatuses + " TWEETS");
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}

			@Override
			public void onScrubGeo(long arg0, long arg1) {
				System.err.println("onScrubGeo");

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
				System.err.println("onStallWarning");

			}
		};
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(AuthenticationData.API_KEY)
				.setOAuthConsumerSecret(AuthenticationData.API_SECRET)
				.setOAuthAccessToken(AuthenticationData.ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(AuthenticationData.ACCESS_TOKEN_SECRET);
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.track(new String[] { "#SmashBros" });
		twitterStream.filter(query);
	}
}
