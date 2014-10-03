package br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.TweetDatabaseDelayedPersistManager;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.WordOccurrence;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Logger;

public class WordCounter implements TweetProcessor {
	private TweetDatabaseDelayedPersistManager persistManager;
	private static final List<String> wordsToIgnore;
	private static final Pattern symbolsToIgnore = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");

	static {
		wordsToIgnore = new ArrayList<String>();
		wordsToIgnore.add("i");
		wordsToIgnore.add("me");
		wordsToIgnore.add("you");
		wordsToIgnore.add("he");
		wordsToIgnore.add("she");
		wordsToIgnore.add("it");
		wordsToIgnore.add("we");
		wordsToIgnore.add("us");
		wordsToIgnore.add("myself");
		wordsToIgnore.add("yourself");
		wordsToIgnore.add("they");
		wordsToIgnore.add("them");
		wordsToIgnore.add("their");
		wordsToIgnore.add("our");
		wordsToIgnore.add("your");
		wordsToIgnore.add("yours");
		wordsToIgnore.add("her");
		wordsToIgnore.add("him");
		wordsToIgnore.add("and");
		wordsToIgnore.add("not");
		wordsToIgnore.add("so");
		wordsToIgnore.add("with");
		wordsToIgnore.add("in");
		wordsToIgnore.add("on");
		wordsToIgnore.add("too");
		wordsToIgnore.add("have");
		wordsToIgnore.add("has");
		wordsToIgnore.add("the");
		wordsToIgnore.add("for");
		wordsToIgnore.add("a");
		wordsToIgnore.add("an");
		wordsToIgnore.add("until");
		wordsToIgnore.add("go");
		wordsToIgnore.add("goes");
		wordsToIgnore.add("gone");
		wordsToIgnore.add("to");
		wordsToIgnore.add("about");
		wordsToIgnore.add("left");
		wordsToIgnore.add("can");
		wordsToIgnore.add("cant");
		wordsToIgnore.add("can't");
		wordsToIgnore.add("can’t");
		wordsToIgnore.add("fuck");
		wordsToIgnore.add("bitch");
		wordsToIgnore.add("damn");
		wordsToIgnore.add("is");
		wordsToIgnore.add("are");
		wordsToIgnore.add("this");
		wordsToIgnore.add("just");
		wordsToIgnore.add("his");
		wordsToIgnore.add("its");
		wordsToIgnore.add("it's");
		wordsToIgnore.add("it’s");
		wordsToIgnore.add("few");
		wordsToIgnore.add("out");
		wordsToIgnore.add("that");
		wordsToIgnore.add("i'm");
		wordsToIgnore.add("i’m");
		wordsToIgnore.add("how");
	}

	public WordCounter(DbAccessConfiguration dbAccessConfiguration) {
		this.persistManager = TweetDatabaseDelayedPersistManager.getInstance(dbAccessConfiguration);
	}

	@Override
	public void process(Tweet tweet) {
		String tweetText = tweet.getTweet().toLowerCase();
		tweetText = removeRetweetMetadata(tweetText);
		String[] words = tweetText.split(" ");
		for (String word : words) {
			if (word.length() > 2 && !isWordAnURL(word)) {
				word = removeSymbols(word);
				if (word.length() > 2 && !wordsToIgnore.contains(word) && !isWordAHashtag(word)
						&& !isWordAUserReference(word)) {
					Logger.debug("persisted " + word, WordCounter.class);
					this.persistManager.persistWordOccurrence(new WordOccurrence(word));
				} else {
					Logger.debug("ignored " + word, WordCounter.class);
				}
			} else {
				Logger.debug("ignored " + word, WordCounter.class);
			}
		}
	}

	private String removeSymbols(String word) {
		return symbolsToIgnore.matcher(word).replaceAll("");
	}

	private boolean isWordAUserReference(String word) {
		return word.startsWith("@");
	}

	private boolean isWordAHashtag(String word) {
		return word.startsWith("#");
	}

	private boolean isWordAnURL(String word) {
		return word.startsWith("http");
	}

	private String removeRetweetMetadata(String tweetText) {
		if (tweetText.startsWith("rt @")) {
			int endOfRTUserName = tweetText.indexOf(' ', 4);
			return tweetText.substring(endOfRTUserName + 1);
		}
		return tweetText;
	}
}
