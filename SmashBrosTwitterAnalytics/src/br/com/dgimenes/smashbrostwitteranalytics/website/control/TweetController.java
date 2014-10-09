/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the ("Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED ("AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package br.com.dgimenes.smashbrostwitteranalytics.website.control;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import br.com.dgimenes.smashbrostwitteranalytics.website.exception.TweetNotFoundException;
import br.com.dgimenes.smashbrostwitteranalytics.website.model.Tweet;

@Local
@Stateless
public class TweetController {
	private static final int MIN_NUM_OF_LATEST_TWEETS = 1;
	private static final int MAX_NUM_OF_LATEST_TWEETS = 5;
	@PersistenceContext
	private EntityManager em;

	public List<Tweet> getLatestTweets(int numberOfTweets) {
		List<Tweet> latestTweets = new ArrayList<Tweet>();
		if (numberOfTweets >= MIN_NUM_OF_LATEST_TWEETS && numberOfTweets <= MAX_NUM_OF_LATEST_TWEETS) {
			TypedQuery<Tweet> query = this.em.createNamedQuery("Tweet.latest", Tweet.class);
			query.setMaxResults(numberOfTweets);
			latestTweets = query.getResultList();
		}
		return latestTweets;
	}

	public Long getTweet(Long id) throws TweetNotFoundException {
		Tweet tweet = this.em.find(Tweet.class, id);
		if (tweet == null) {
			throw new TweetNotFoundException(id);
		}
		return tweet.getId();
	}
}
