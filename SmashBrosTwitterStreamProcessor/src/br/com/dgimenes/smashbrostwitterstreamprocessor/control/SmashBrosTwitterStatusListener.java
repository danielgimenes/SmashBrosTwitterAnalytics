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

import java.util.ArrayList;
import java.util.List;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.TweetProcessor;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;

public class SmashBrosTwitterStatusListener implements StatusListener {
	private List<TweetProcessor> processors;

	public SmashBrosTwitterStatusListener() {
		this.processors = new ArrayList<TweetProcessor>();
	}

	public void addProcessor(TweetProcessor newProcessor) {
		processors.add(newProcessor);
	}

	public void removeProcessor(TweetProcessor processor) {
		processors.remove(processor);
	}

	@Override
	public void onStatus(Status status) {
		long rtId = status.getRetweetedStatus() == null ? 0 : status.getRetweetedStatus().getId();
		Tweet tweet = new Tweet(status.getId(), status.getCreatedAt(), status.getUser().getName(), status.getUser()
				.getScreenName(), status.getText(), rtId, status.getLang(), status.isRetweet());
		for (TweetProcessor tweetProcessor : processors) {
			tweetProcessor.process(tweet);
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