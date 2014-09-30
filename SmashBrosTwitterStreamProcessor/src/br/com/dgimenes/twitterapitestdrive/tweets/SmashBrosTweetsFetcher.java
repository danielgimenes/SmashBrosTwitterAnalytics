package br.com.dgimenes.twitterapitestdrive.tweets;

import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SmashBrosTweetsFetcher {
	public static void main(String[] args) {
		System.out.println(SmashBrosTweetsFetcher.class.getSimpleName());
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(AuthenticationData.API_KEY)
				.setOAuthConsumerSecret(AuthenticationData.API_SECRET)
				.setOAuthAccessToken(AuthenticationData.ACCESS_TOKEN)
				.setOAuthAccessTokenSecret(AuthenticationData.ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		Query query = new Query();
		query.setCount(5);
		query.setQuery("#SmashBros");
		query.setResultType(ResultType.recent);
		QueryResult result;
		try {
			result = twitter.search(query);
			for (Status status : result.getTweets()) {
				System.out.println("@" + status.getUser().getScreenName() + ":\n\t" + status.getText() + "\n");
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
}
