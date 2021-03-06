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
package br.com.dgimenes.smashbrostwitterstreamprocessor.control;

import java.io.File;
import java.io.IOException;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.Configuration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.debug.DebugProcessor;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.CharacterReferenceIdentifier;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.TweetPersist;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.WordCounter;
import br.com.dgimenes.smashbrostwitterstreamprocessor.exception.InvalidConfigurationFileException;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.TweetDatabaseDelayedPersistManager;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.TwitterAppAccount;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Logger;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Utils;

public class SmashBrosTweetsStreamingReceiver {
	private static final String EXIT_FLAG_FILE = "/tmp/exitsmash";

	public static void main(String[] args) throws TwitterException, IOException {
		Configuration config;
		if (args != null && args.length == 1) {
			try {
				config = Configuration.loadConfigFromFile(args[0]);
			} catch (InvalidConfigurationFileException e) {
				System.err.println("Invalid configuration file.\n");
				printUsageMessage();
				return;
			}
		} else {
			printUsageMessage();
			return;
		}
		Logger.info(SmashBrosTweetsStreamingReceiver.class.getSimpleName() + " starting!",
				SmashBrosTweetsStreamingReceiver.class);
		TwitterAppAccount twitterAppAccount = config.getTwitterAppAccount();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(twitterAppAccount.getApiKey())
				.setOAuthConsumerSecret(twitterAppAccount.getApiKeySecret())
				.setOAuthAccessToken(twitterAppAccount.getAccessToken())
				.setOAuthAccessTokenSecret(twitterAppAccount.getAccessTokenSecret());
		SmashBrosTwitterStatusListener listener = new SmashBrosTwitterStatusListener();
		listener.addProcessor(new DebugProcessor());
		listener.addProcessor(new TweetPersist(config.getDbAccessConfiguration()));
		listener.addProcessor(new CharacterReferenceIdentifier(config.getDbAccessConfiguration()));
		listener.addProcessor(new WordCounter(config.getDbAccessConfiguration()));
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.track(config.getTweetTagsToTrack());
		twitterStream.filter(query);

		// CharacterReferenceIdentifier identifier = new
		// CharacterReferenceIdentifier(config.getDbAccessConfiguration());
		// identifier.process(new Tweet(1, null, null, null,
		// "One reference to toon LiNK", 0L, null, false));
		// identifier.process(new Tweet(2, null, null, null,
		// "One reference to samus", 0L, null, false));
		// identifier.process(new Tweet(3, null, null, null,
		// "One reference to zero suit saMUS", 0L, null, false));
		// identifier.process(new Tweet(4, null, null, null,
		// "One reference to pac-man", 0L, null, false));
		// identifier.process(new Tweet(5, null, null, null,
		// "One reference to no one", 0L, null, false));
		// identifier.process(new Tweet(6, null, null, null,
		// "One reference to Mario", 0L, null, false));
		// identifier.process(new Tweet(7, null, null, null,
		// "One reference to Link", 0L, null, false));
		// identifier.process(new Tweet(8, null, null, null,
		// "One reference to toon LiNK", 0L, null, false));
		// identifier.process(new Tweet(9, null, null, null,
		// "One reference to Link", 0L, null, false));
		// identifier.process(new Tweet(10, null, null, null,
		// "One reference to Link", 0L, null, false));
		// identifier.process(new Tweet(11, null, null, null,
		// "One reference to Link", 0L, null, false));
		// identifier.process(new Tweet(12, null, null, null,
		// "One reference to pac-man", 0L, null, false));
		// identifier.process(new Tweet(13, null, null, null,
		// "One reference to no one", 0L, null, false));
		// identifier.process(new Tweet(14, null, null, null,
		// "One reference to mega man", 0L, null, false));

		boolean timeToShutdown = false;
		while (!timeToShutdown) {
			Utils.sleepSeconds(1);
			timeToShutdown = new File(EXIT_FLAG_FILE).exists();
		}
		twitterStream.shutdown();
		TweetDatabaseDelayedPersistManager.shutdown();
		new File(EXIT_FLAG_FILE).delete();
	}

	private static void printUsageMessage() {
		System.err
				.println("Usage:\n\n\tjava -jar SmashBrosTwitterStreamProcessor.jar <configuration_file_path>\n\nConfiguration file should have the following data in Java Properties format:\n");
		for (String configItemName : Configuration.getConfigurationItemNames()) {
			System.err.println("\t" + configItemName);
		}
		System.err.println();
	}
}
