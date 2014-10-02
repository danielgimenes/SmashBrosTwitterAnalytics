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
package br.com.dgimenes.smashbrostwitterstreamprocessor.storm.spout;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import storm.trident.operation.TridentCollector;
import storm.trident.spout.IBatchSpout;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import backtype.storm.Config;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import br.com.dgimenes.smashbrostwitterstreamprocessor.model.Tweet;
import br.com.dgimenes.smashbrostwitterstreamprocessor.storm.TwitterDeveloperAccount;

public class SmashBrosTweetsSpout implements IBatchSpout {
	private static final long serialVersionUID = -5263338238881705785L;
	private static final String[] SMASH_BROS_TWITTER_TAGS = new String[] { "#SmashBros" };
	private static final int TWEETS_TO_EMIT_QUEUE_SIZE = 1000;
	private static final int MAX_TWEETS_PER_BATCH = 100;
	private TwitterDeveloperAccount twitterDeveloperAccount;
	private LinkedBlockingQueue<Tweet> tweetsToEmitQueue = null;
	private TwitterStream twitterStream;
	private StatusListener twitterStreamingListener;

	public SmashBrosTweetsSpout(TwitterDeveloperAccount twitterDeveloperAccount) {
		this.twitterDeveloperAccount = twitterDeveloperAccount;
	}

	@Override
	public void close() {
		twitterStream.shutdown();
	}

	@Override
	public void ack(long batchId) {
	}

	@Override
	public void emitBatch(long batchId, TridentCollector collector) {
		int batchSize = Math.min(tweetsToEmitQueue.size(), MAX_TWEETS_PER_BATCH);
		for (int i = 0; i < batchSize; i++) {
			Tweet tweet = tweetsToEmitQueue.poll();
			if (tweet != null) {
				collector.emit(new Values(tweet));
			}
		}
	}

	@Override
	public void open(Map conf, TopologyContext context) {
		this.tweetsToEmitQueue = new LinkedBlockingQueue<Tweet>(TWEETS_TO_EMIT_QUEUE_SIZE);
		configureTwitterStreamingListener();
		startTwitterStream();
	}

	private void configureTwitterStreamingListener() {
		twitterStreamingListener = new StatusListener() {
			@Override
			public void onStatus(Status status) {
				long rtId = status.getRetweetedStatus() == null ? 0 : status.getRetweetedStatus().getId();
				Tweet tweet = new Tweet(status.getId(), status.getCreatedAt(), status.getUser().getName(), status
						.getUser().getScreenName(), status.getText(), rtId, status.getLang(), status.isRetweet());
				System.out.println(String.format("Received Tweet [%1$d] %2$s: %3$s", tweet.getId(),
						tweet.getUserName(), tweet.getTweet()));
				tweetsToEmitQueue.offer(tweet);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
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

			}

			@Override
			public void onStallWarning(StallWarning arg0) {
			}
		};
	}

	private void startTwitterStream() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(twitterDeveloperAccount.getApiKey())
				.setOAuthConsumerSecret(twitterDeveloperAccount.getApiKeySecret())
				.setOAuthAccessToken(twitterDeveloperAccount.getAccessToken())
				.setOAuthAccessTokenSecret(twitterDeveloperAccount.getAccessTokenSecret());
		twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(twitterStreamingListener);
		FilterQuery query = new FilterQuery();
		query.track(SMASH_BROS_TWITTER_TAGS);
		twitterStream.filter(query);
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		Config ret = new Config();
		ret.setMaxTaskParallelism(1); // Cannot have more than 1 Twitter
										// Streaming connection with the same
										// Credentials
		return ret;
	}

	@Override
	public Fields getOutputFields() {
		return new Fields("tweet");
	}
}
