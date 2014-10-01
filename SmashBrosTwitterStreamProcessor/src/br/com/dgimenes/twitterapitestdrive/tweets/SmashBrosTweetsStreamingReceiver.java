/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
				String screenName = status.getUser().getScreenName();
				String tweet = status.getText();
				long rtId = status.getRetweetedStatus() == null ? 0 : status.getRetweetedStatus().getId();
				String lang = status.getLang();
				boolean isRT = status.isRetweet();
				System.out.println((isRT ? "RT " : "") + String.format("[%1$d] %2$s: %3$s", tweetId, userName, tweet));
				try {
					SmashBrosDatabaseServices.getInstance().persistTweet(tweetId, tweetTime, isRT, rtId, lang,
							userName, screenName, tweet);
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
