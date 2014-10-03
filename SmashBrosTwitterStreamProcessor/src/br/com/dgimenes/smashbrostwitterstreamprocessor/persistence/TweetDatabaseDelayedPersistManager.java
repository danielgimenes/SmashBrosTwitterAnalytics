package br.com.dgimenes.smashbrostwitterstreamprocessor.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.WordOccurrence;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Logger;
import br.com.dgimenes.smashbrostwitterstreamprocessor.util.Utils;

public class TweetDatabaseDelayedPersistManager {
	public static final long SECONDS_BETWEEN_PERSIST = 3;
	private BatchAsyncPersistExecuter persistExecuter;
	private static TweetDatabaseDelayedPersistManager instance;
	private static boolean shutdownProcess = false;
	private ConcurrentLinkedQueue<Tweet> tweetsToPersist;
	private ConcurrentLinkedQueue<WordOccurrence> wordOccurencesToPersist;
	private ConcurrentLinkedQueue<CharReference> charReferencesToPersist;
	private DbAccessConfiguration dbAccessConfig;

	private TweetDatabaseDelayedPersistManager(DbAccessConfiguration dbAccessConfig) {
		this.dbAccessConfig = dbAccessConfig;
		this.persistExecuter = new BatchAsyncPersistExecuter();
		this.tweetsToPersist = new ConcurrentLinkedQueue<Tweet>();
		new Thread(this.persistExecuter).start();
	}

	public static TweetDatabaseDelayedPersistManager getInstance(DbAccessConfiguration dbAccessConfig) {
		if (instance == null) {
			instance = new TweetDatabaseDelayedPersistManager(dbAccessConfig);
		}
		return instance;
	}

	public void addTweet(Tweet tweet) {
		this.tweetsToPersist.add(tweet);
	}

	public void persistWordOccurrence(WordOccurrence occurrence) {
		this.wordOccurencesToPersist.add(occurrence);
	}

	public void persistCharReference(CharReference reference) {
		this.charReferencesToPersist.add(reference);
	}

	public static void shutdown() {
		TweetDatabaseDelayedPersistManager.shutdownProcess = true;
	}

	private class BatchAsyncPersistExecuter implements Runnable {
		private static final String INSERT_TWEET_SQL = "INSERT INTO tweets(id, time, rt, rtid, lang, username, screenname, tweet) VALUES (?,?,?,?,?,?,?,?);";
		private Connection conn;
		private List<Tweet> tweetsOnTransaction;
		private List<WordOccurrence> wordCountsOnTransaction;
		private List<CharReference> charReferencesOnTransaction;

		public BatchAsyncPersistExecuter() {
			tweetsOnTransaction = new ArrayList<Tweet>();
			wordCountsOnTransaction = new ArrayList<WordOccurrence>();
			charReferencesOnTransaction = new ArrayList<CharReference>();
		}

		@Override
		public void run() {
			Logger.info("BatchAsyncPersistExecuter STARTED", BatchAsyncPersistExecuter.class);
			while (!TweetDatabaseDelayedPersistManager.shutdownProcess) {
				Utils.sleepSeconds(SECONDS_BETWEEN_PERSIST);
				try {
					openDbConnection();
					persistTweets();
					updateWordCounts();
					updateCharRanks();
					commitAndCloseDbConnection();
				} catch (SQLException e) {
					e.printStackTrace();
					try {
						conn.rollback();
					} catch (SQLException e1) {
						e1.printStackTrace();
					}
					rollbackTweets();
					rollbackWordCount();
					rollbackCharRank();
				}
			}
			Logger.info("BatchAsyncPersistExecuter STOPPED", BatchAsyncPersistExecuter.class);
		}

		private void openDbConnection() throws SQLException {
			conn = DriverManager.getConnection(dbAccessConfig.getConnectionString(), dbAccessConfig.getUser(),
					dbAccessConfig.getPassword());
			conn.setAutoCommit(false);
		}

		private void commitAndCloseDbConnection() throws SQLException {
			if (conn != null) {
				conn.commit();
				conn.close();
			}
		}

		private void persistTweets() throws SQLException {
			tweetsOnTransaction.clear();
			// we know it cant get lower than this
			int snapshotSize = tweetsToPersist.size();
			for (int i = 0; i < snapshotSize; i++) {
				tweetsOnTransaction.add(tweetsToPersist.poll());
			}
			for (Tweet tweet : tweetsOnTransaction) {
				PreparedStatement statement = conn.prepareStatement(INSERT_TWEET_SQL);
				statement.setLong(1, tweet.getId());
				statement.setTimestamp(2, new java.sql.Timestamp(tweet.getTweetTime().getTime()));
				statement.setBoolean(3, tweet.isRT());
				statement.setLong(4, tweet.getRtId());
				statement.setString(5, tweet.getLang());
				statement.setString(6, tweet.getUserName());
				statement.setString(7, tweet.getScreenName());
				statement.setString(8, tweet.getTweet());
				statement.executeUpdate();
				String tweetStr = tweet.getTweet();
				Logger.info("Persisted [" + tweet.getId() + "] "
						+ tweetStr.substring(0, tweetStr.length() > 80 ? 80 : tweetStr.length()), BatchAsyncPersistExecuter.class);
			}
		}

		private void rollbackTweets() {
			tweetsToPersist.addAll(tweetsOnTransaction);
		}

		private void updateCharRanks() throws SQLException {
			// TODO Auto-generated method stub

		}

		private void updateWordCounts() throws SQLException {
			// TODO Auto-generated method stub

		}

		private void rollbackCharRank() {
			// TODO Auto-generated method stub
		}

		private void rollbackWordCount() {
			// TODO Auto-generated method stub

		}
	}
}
