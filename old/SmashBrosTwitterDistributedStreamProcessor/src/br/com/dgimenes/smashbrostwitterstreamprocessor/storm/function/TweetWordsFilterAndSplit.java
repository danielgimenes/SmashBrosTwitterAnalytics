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
package br.com.dgimenes.smashbrostwitterstreamprocessor.storm.function;

import java.util.HashSet;
import java.util.Set;

import backtype.storm.tuple.Values;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

public class TweetWordsFilterAndSplit extends BaseFunction {
	private static final long serialVersionUID = 2951514259713806935L;
	private static final Set<String> wordsToIgnore;
	static {
		String[] ignore = new String[] { "rt", //
				"and", //
				"for", //
				"the", //
				"he", //
				"she", //
				"i", //
				"our", //
				"a", //
				"an", //
				"they", //
				"we", //
				"you", //
		};
		wordsToIgnore = new HashSet<String>();
		for (String word : ignore) {
			wordsToIgnore.add(word);
		}
	}

	// TODO CAN BE IMPROVED FOR PERFORMANCE
	@Override
	public void execute(TridentTuple tuple, TridentCollector collector) {
		String tweetText = tuple.getString(0);
		String[] tweetWords = tweetText.split(" ");
		for (String tweetWord : tweetWords) {
			if (!wordsToIgnore.contains(tweetWord)) {
				collector.emit(new Values(tweetWord));
			}
		}
	}
}
