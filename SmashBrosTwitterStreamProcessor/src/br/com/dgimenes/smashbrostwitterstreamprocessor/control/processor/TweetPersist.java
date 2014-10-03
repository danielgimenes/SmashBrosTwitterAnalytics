package br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.TweetDatabaseDelayedPersistManager;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;

public class TweetPersist implements TweetProcessor {
	private TweetDatabaseDelayedPersistManager persistManager;

	public TweetPersist(DbAccessConfiguration dbAccessConfig) {
		persistManager = TweetDatabaseDelayedPersistManager.getInstance(dbAccessConfig);
	}

	@Override
	public void process(Tweet tweet) {
		persistManager.addTweet(tweet);
	}

}
