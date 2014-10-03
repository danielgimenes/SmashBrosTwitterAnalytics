package br.com.dgimenes.smashbrostwitterstreamprocessor.control.debug;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor.TweetProcessor;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;

public class DebugProcessor implements TweetProcessor {

	@Override
	public void process(Tweet tweet) {
		System.out.println(String.format("%1$tc [%2$d] %3$s: %4$s", tweet.getTweetTime(), tweet.getId(),
				tweet.getUserName(), tweet.getTweet()));
	}
}
