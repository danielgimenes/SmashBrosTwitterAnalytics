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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import br.com.dgimenes.smashbrostwitterstreamprocessor.exception.InvalidConfigurationFileException;
import br.com.dgimenes.smashbrostwitterstreamprocessor.model.TwitterAppAccount;

public class Configuration {
	private static String[] itemNames = new String[] { "Twitter.apiKey", //
			"Twitter.apiKeySecret", //
			"Twitter.accessToken", //
			"Twitter.accessTokenSecret", //
			"Twitter.tagsToTrack" //
	};
	private TwitterAppAccount twitterAppAccount;
	private String[] tweetTagsToTrack;

	public static Configuration loadConfigFromFile(String filePath) throws InvalidConfigurationFileException {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(filePath));
		} catch (IOException e) {
			throw new InvalidConfigurationFileException();
		}
		return new Configuration(properties);
	}

	private Configuration(Properties properties) throws InvalidConfigurationFileException {
		checkForAllItems(properties);
		this.twitterAppAccount = new TwitterAppAccount(properties.getProperty("Twitter.accessToken"),
				properties.getProperty("Twitter.accessTokenSecret"), properties.getProperty("Twitter.apiKey"),
				properties.getProperty("Twitter.apiKeySecret"));
		this.tweetTagsToTrack = properties.getProperty("Twitter.tagsToTrack").split(",");
	}

	private void checkForAllItems(Properties properties) throws InvalidConfigurationFileException {
		for (String itemName : itemNames) {
			String property = properties.getProperty(itemName);
			if (property == null) {
				throw new InvalidConfigurationFileException();
			}
		}
	}

	public static String[] getConfigurationItemNames() {
		return Configuration.itemNames;
	}

	public TwitterAppAccount getTwitterAppAccount() {
		return this.twitterAppAccount;
	}

	public String[] getTweetTagsToTrack() {
		return this.tweetTagsToTrack;
	}

}
