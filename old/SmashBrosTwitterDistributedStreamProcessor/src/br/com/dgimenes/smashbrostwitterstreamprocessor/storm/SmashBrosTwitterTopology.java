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
package br.com.dgimenes.smashbrostwitterstreamprocessor.storm;

import storm.trident.Stream;
import storm.trident.TridentState;
import storm.trident.TridentTopology;
import storm.trident.operation.builtin.Count;
import storm.trident.testing.MemoryMapState;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.LocalDRPC;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;
import br.com.dgimenes.smashbrostwitterstreamprocessor.model.TwitterDebugAuthenticationData;
import br.com.dgimenes.smashbrostwitterstreamprocessor.storm.function.CharactersReferencesIdentifier;
import br.com.dgimenes.smashbrostwitterstreamprocessor.storm.function.TweetTextExtractor;
import br.com.dgimenes.smashbrostwitterstreamprocessor.storm.function.TweetWordsFilterAndSplit;
import br.com.dgimenes.smashbrostwitterstreamprocessor.storm.spout.SmashBrosTweetsSpout;

/*
 * THIS CODE IS NOT WORKING CORRECTLY. FOR PROCESSING THE TWEETS USE 
 * br.com.dgimenes.smashbrostwitterstreamprocessor.javase.SmashBrosTweetsStreamingReceiver 
 * INSTEAD.
 * 
 * THIS IS AN UNFINISHED ATTEMPT ON USING STORM (TRIDENT) TO PROCESS
 * THE TWEETS IN A DISTRIBUTED FASHION. IT WAS DROPPED OUT BECAUSE OF:
 * 		1. not enough time to work on this;
 * 		2. finding that the number of tweets does not characterize the problem as a Big Data one.
 * 
 */
public class SmashBrosTwitterTopology {
	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);

			StormSubmitter.submitTopologyWithProgressBar(args[0], conf, buildTopology(null));
		} else {
			conf.setMaxTaskParallelism(3);
			LocalDRPC drpc = new LocalDRPC();
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(SmashBrosTwitterTopology.class.getSimpleName(), conf, buildTopology(drpc));

			Utils.sleep(10 * 60 * 1000); // 10 minutes
			cluster.shutdown();
		}
	}

	private static StormTopology buildTopology(LocalDRPC drpc) {
		TwitterDeveloperAccount twitterDeveloperAccount = new TwitterDeveloperAccount(
				TwitterDebugAuthenticationData.ACCESS_TOKEN, TwitterDebugAuthenticationData.ACCESS_TOKEN_SECRET,
				TwitterDebugAuthenticationData.API_KEY, TwitterDebugAuthenticationData.API_SECRET);
		TridentTopology topology = new TridentTopology();

		SmashBrosTweetsSpout smashBrosTweetsSpout = new SmashBrosTweetsSpout(twitterDeveloperAccount);

		Stream tweetsStream = topology.newStream("smashbros-tweets-spout", smashBrosTweetsSpout);

		// TridentState persistedTweets = tweetsStream.partitionPersist(new
		// SmashBrosTweetsDatabaseState.Factory(),
		// new Fields("tweet"), new
		// BaseStateUpdater<SmashBrosTweetsDatabaseState>() {
		// private static final long serialVersionUID = -2160953537837069611L;
		//
		// @Override
		// public void updateState(SmashBrosTweetsDatabaseState state,
		// List<TridentTuple> tuples,
		// TridentCollector collector) {
		// List<Object> tweetIds = new ArrayList<Object>();
		// List<Object> tweets = new ArrayList<Object>();
		// for (TridentTuple t : tuples) {
		// tweetIds.add(((Tweet)t.get(0)).getId());
		// tweets.add(t.get(0));
		// }
		// state.multiUpdate(tweetIds, tweets);
		// }
		// });

		Stream tweetsTextStream = tweetsStream.each(new Fields("tweet"), new TweetTextExtractor(), new Fields(
				"tweet-text"));

		TridentState wordCounts = tweetsTextStream
				.each(new Fields("tweet-text"), new TweetWordsFilterAndSplit(), new Fields("word")) //
				.groupBy(new Fields("word")) //
				.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count")) //
				.parallelismHint(6);

		TridentState charactersRank = tweetsTextStream
				.each(new Fields("tweet-text"), new CharactersReferencesIdentifier(), new Fields("charRef")) //
				.groupBy(new Fields("charRef")) //
				.persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count")) //
				.parallelismHint(6);

		// wordCounts.newValuesStream().each(new Fields("count"), new Debug());
		// charactersRank.newValuesStream().each(new Fields("count"), new
		// Debug());

		return topology.build();
	}

	// public static void main(String[] args) throws AlreadyAliveException,
	// InvalidTopologyException {
	// TopologyBuilder builder = new TopologyBuilder();
	// TwitterDeveloperAccount twitterDeveloperAccount = new
	// TwitterDeveloperAccount(
	// TwitterDebugAuthenticationData.ACCESS_TOKEN,
	// TwitterDebugAuthenticationData.ACCESS_TOKEN_SECRET,
	// TwitterDebugAuthenticationData.API_KEY,
	// TwitterDebugAuthenticationData.API_SECRET);
	// builder.setSpout("spout", new
	// SmashBrosTweetsSpout(twitterDeveloperAccount));
	// builder.setBolt("split", new SplitSentenceBolt(),
	// 8).shuffleGrouping("spout", "tweet-text-only");
	// builder.setBolt("count", new WordCountBold(), 12).fieldsGrouping("split",
	// new Fields("word"));
	//
	// Config conf = new Config();
	// conf.setDebug(true);
	//
	// if (args != null && args.length > 0) {
	// conf.setNumWorkers(3);
	//
	// StormSubmitter.submitTopologyWithProgressBar(args[0], conf,
	// builder.createTopology());
	// } else {
	// conf.setMaxTaskParallelism(3);
	//
	// LocalCluster cluster = new LocalCluster();
	// cluster.submitTopology(SmashBrosTwitterTopology.class.getSimpleName(),
	// conf, builder.createTopology());
	// Utils.sleep(10 * 60 * 1000); // 10 minutes
	// cluster.shutdown();
	// }
	// }
}
