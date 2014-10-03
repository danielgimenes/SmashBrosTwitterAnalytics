package br.com.dgimenes.smashbrostwitterstreamprocessor.control.processor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.CharacterReference;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.TweetDatabaseDelayedPersistManager;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.SmashBrosCharacter;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Logger;

public class CharacterReferenceIdentifier implements TweetProcessor {
	private TweetDatabaseDelayedPersistManager persistManager;
	private static final Pattern symbolsToIgnore = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");

	public CharacterReferenceIdentifier(DbAccessConfiguration dbAccessConfiguration) {
		this.persistManager = TweetDatabaseDelayedPersistManager.getInstance(dbAccessConfiguration);
	}

	@Override
	public void process(Tweet tweet) {
		String tweetText = tweet.getTweet().toLowerCase();
		tweetText = removeRetweetMetadata(tweetText);
		String[] words = tweetText.split(" ");
		List<String> tweetWords = new ArrayList<String>();
		for (String word : words) {
			word = removeSymbols(word);
			tweetWords.add(word);
		}
		Set<SmashBrosCharacter> referencedCharacters = new HashSet<SmashBrosCharacter>();
		for (SmashBrosCharacter character : SmashBrosCharacter.values()) {
			// there are several strings that mean the same character
			for (String characterReferenceString : character.getReferenceStrings()) {
				String[] wordsOfCharRefString = characterReferenceString.split(" ");
				String firstWordOfCharRefString = wordsOfCharRefString[0];
				if (tweetWords.contains(firstWordOfCharRefString)) {
					if (wordsOfCharRefString.length == 1) {
						referencedCharacters.add(character);
						break;
					} else {
						if (tweetText.contains(characterReferenceString)) {
							referencedCharacters.add(character);
							break;
						}
					}
					if (character == SmashBrosCharacter.IKE) {
						Logger.debug("[" + characterReferenceString + "] found in " + tweetText,
								CharacterReferenceIdentifier.class);
					}
					// found a reference, let's go to the next character
				}
			}
		}
		// "Toon Link" would trigger LINK and TOON_LINK
		if (referencedCharacters.contains(SmashBrosCharacter.LINK)
				&& referencedCharacters.contains(SmashBrosCharacter.TOON_LINK)) {
			boolean referencesToBothCharacters = false;
			for (String characterReferenceString : SmashBrosCharacter.TOON_LINK.getReferenceStrings()) {
				// first find the characterReferenceString that is on the text
				if (tweetText.contains(characterReferenceString)) {
					String tweetTextWithoutReferenceStr = tweetText.replaceAll(characterReferenceString, "");
					for (String character2ReferenceString : SmashBrosCharacter.LINK.getReferenceStrings()) {
						// if the string still contains a reference to the
						// character
						// with the smaller name
						if (tweetTextWithoutReferenceStr.contains(character2ReferenceString)) {
							referencesToBothCharacters = true;
							break;
						}
					}
					break; // already found.. no need to check other
							// characterReferenceStrings
				}

			}
			if (!referencesToBothCharacters) {
				referencedCharacters.remove(SmashBrosCharacter.LINK);
			}
		}
		// "Zero Suit Samus" would trigger SAMUS and ZERO_SUIT_SAMUS
		if (referencedCharacters.contains(SmashBrosCharacter.SAMUS)
				&& referencedCharacters.contains(SmashBrosCharacter.ZERO_SUIT_SAMUS)) {
			boolean referencesToBothCharacters = false;
			for (String characterReferenceString : SmashBrosCharacter.ZERO_SUIT_SAMUS.getReferenceStrings()) {
				// first find the characterReferenceString that is on the text
				if (tweetText.contains(characterReferenceString)) {
					String tweetTextWithoutReferenceStr = tweetText.replaceAll(characterReferenceString, "");
					for (String character2ReferenceString : SmashBrosCharacter.SAMUS.getReferenceStrings()) {
						if (tweetTextWithoutReferenceStr.contains(character2ReferenceString)) {
							referencesToBothCharacters = true;
							break;
						}
					}
					break;
				}
			}
			if (!referencesToBothCharacters) {
				referencedCharacters.remove(SmashBrosCharacter.SAMUS);
			}
		}
		for (SmashBrosCharacter character : referencedCharacters) {
			this.persistManager.persistCharReference(new CharacterReference(character));
			Logger.debug("FOUND charRefs [" + character + "]", CharacterReferenceIdentifier.class);
		}
	}
	
	private String removeSymbols(String word) {
		return symbolsToIgnore.matcher(word).replaceAll("");
	}

	private String removeRetweetMetadata(String tweetText) {
		if (tweetText.startsWith("rt @")) {
			int endOfRTUserName = tweetText.indexOf(' ', 4);
			return tweetText.substring(endOfRTUserName + 1);
		}
		return tweetText;
	}
}
