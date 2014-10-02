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

import java.io.IOException;

import twitter4j.FilterQuery;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import br.com.dgimenes.smashbrostwitterstreamprocessor.exception.InvalidConfigurationFileException;
import br.com.dgimenes.smashbrostwitterstreamprocessor.model.TwitterAppAccount;

public class SmashBrosTweetsStreamingReceiver {
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
		TwitterAppAccount twitterAppAccount = config.getTwitterAppAccount();
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(twitterAppAccount.getApiKey())
				.setOAuthConsumerSecret(twitterAppAccount.getApiKeySecret())
				.setOAuthAccessToken(twitterAppAccount.getAccessToken())
				.setOAuthAccessTokenSecret(twitterAppAccount.getAccessTokenSecret());
//		StatusListener listener = new SmashBrosTwitterStatusListener();
		StatusListener listener = new DebugStatusListener();
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		twitterStream.addListener(listener);
		FilterQuery query = new FilterQuery();
		query.track(config.getTweetTagsToTrack());
		twitterStream.filter(query);
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
