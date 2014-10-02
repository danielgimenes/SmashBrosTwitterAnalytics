package br.com.dgimenes.smashbrostwitterstreamprocessor.control;

import java.sql.SQLException;
import java.util.Date;

import br.com.dgimenes.smashbrostwitterstreamprocessor.datamapping.SmashBrosDatabaseServices;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

public class SmashBrosTwitterStatusListener implements StatusListener {
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
			SmashBrosDatabaseServices.getInstance().persistTweet(tweetId, tweetTime, isRT, rtId, lang, userName,
					screenName, tweet);
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