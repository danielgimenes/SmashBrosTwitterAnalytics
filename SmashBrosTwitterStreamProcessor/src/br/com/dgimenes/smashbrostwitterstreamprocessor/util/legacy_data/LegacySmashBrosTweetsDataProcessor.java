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
package br.com.dgimenes.smashbrostwitterstreamprocessor.util.legacy_data;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import twitter4j.TwitterException;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.SmashBrosTwitterStatusListener;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.Configuration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.debug.DebugProcessor;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.CharacterReferenceIdentifier;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.TweetPersist;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.WordCounter;
import br.com.dgimenes.smashbrostwitterstreamprocessor.exception.InvalidConfigurationFileException;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.TweetDatabaseDelayedPersistManager;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Utils;

public class LegacySmashBrosTweetsDataProcessor {
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

		SmashBrosTwitterStatusListener listener = new SmashBrosTwitterStatusListener();
		listener.addProcessor(new DebugProcessor());
		listener.addProcessor(new TweetPersist(config.getDbAccessConfiguration()));
		listener.addProcessor(new CharacterReferenceIdentifier(config.getDbAccessConfiguration()));
		listener.addProcessor(new WordCounter(config.getDbAccessConfiguration()));

		LegacyDbTweetsStreamer streamer = new LegacyDbTweetsStreamer(new DbAccessConfiguration(
				"jdbc:postgresql://localhost:5432/smashbrostweets_db", "postgres", "")); // LEGACY DATABASE
		boolean timeToShutdown = false;
		try {
			streamer.execute(listener);
		} catch (SQLException e) {
			e.printStackTrace();
			timeToShutdown = true;
			return;
		}
		while (!timeToShutdown) {
			Utils.sleepSeconds(1);
			timeToShutdown = new File(EXIT_FLAG_FILE).exists();
		}
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
