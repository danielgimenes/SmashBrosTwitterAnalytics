package br.com.dgimenes.smashbrostwitterstreamprocessor.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
	private ConcurrentLinkedQueue<CharacterReference> charReferencesToPersist;
	private DbAccessConfiguration dbAccessConfig;

	private TweetDatabaseDelayedPersistManager(DbAccessConfiguration dbAccessConfig) {
		this.dbAccessConfig = dbAccessConfig;
		this.persistExecuter = new BatchAsyncPersistExecuter();
		this.tweetsToPersist = new ConcurrentLinkedQueue<Tweet>();
		this.wordOccurencesToPersist = new ConcurrentLinkedQueue<WordOccurrence>();
		this.charReferencesToPersist = new ConcurrentLinkedQueue<CharacterReference>();
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

	public void persistCharReference(CharacterReference reference) {
		this.charReferencesToPersist.add(reference);
	}

	public static void shutdown() {
		TweetDatabaseDelayedPersistManager.shutdownProcess = true;
	}

	// ONLY ONE OF THIS MUST RUN AT A TIME
	private class BatchAsyncPersistExecuter implements Runnable {
		private static final String SELECT_COUNT_OF_WORD_SQL = "SELECT count FROM wordcounts WHERE word = ?";
		private static final String INSERT_TWEET_SQL = "INSERT INTO tweets(id, time, rt, rtid, lang, username, screenname, tweet) VALUES (?,?,?,?,?,?,?,?);";
		private static final String INSERT_WORDCOUNT_SQL = "INSERT INTO wordcounts(word, count) VALUES (?, ?)";
		private static final String UPDATE_WORDCOUNT_SQL = "UPDATE wordcounts SET count = ? WHERE word = ?;";
		private static final String SELECT_REFS_OF_CHAR_SQL = "SELECT refs FROM charactersrank WHERE character = ?";
		private static final String INSERT_CHARREF_SQL = "INSERT INTO charactersrank(character, refs) VALUES (?, ?);";
		private static final String UPDATE_CHARREF_SQL = "UPDATE charactersrank SET refs = ? WHERE character = ?;";
		private Connection conn;
		private List<Tweet> tweetsOnTransaction;
		private List<WordOccurrence> wordCountsOnTransaction;
		private List<CharacterReference> charReferencesOnTransaction;

		public BatchAsyncPersistExecuter() {
			tweetsOnTransaction = new ArrayList<Tweet>();
			wordCountsOnTransaction = new ArrayList<WordOccurrence>();
			charReferencesOnTransaction = new ArrayList<CharacterReference>();
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
				} finally {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
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
				Logger.info(
						"Persisted [" + tweet.getId() + "] "
								+ tweetStr.substring(0, tweetStr.length() > 80 ? 80 : tweetStr.length()),
						BatchAsyncPersistExecuter.class);
			}
		}

		private void rollbackTweets() {
			tweetsToPersist.addAll(tweetsOnTransaction);
		}

		// ASSUMES THAT NO ONE IS GOING TO ALTER THE charactersrank TABLE DURING
		// ITS EXCECUTION
		private void updateCharRanks() throws SQLException {
			charReferencesOnTransaction.clear();
			// we know it can't get lower than this
			int snapshotSize = charReferencesToPersist.size();
			for (int i = 0; i < snapshotSize; i++) {
				charReferencesOnTransaction.add(charReferencesToPersist.poll());
			}
			HashMap<SmashBrosCharacter, Long> charsOfThisBlock = new HashMap<SmashBrosCharacter, Long>();
			for (CharacterReference reference : charReferencesOnTransaction) {
				Long currCount = charsOfThisBlock.get(reference.getCharacter());
				charsOfThisBlock.put(reference.getCharacter(), currCount != null ? currCount + 1 : 1);
			}
			HashMap<SmashBrosCharacter, Long> charsToUpdate = new HashMap<SmashBrosCharacter, Long>();
			HashMap<SmashBrosCharacter, Long> charsToInsert = new HashMap<SmashBrosCharacter, Long>();
			for (SmashBrosCharacter character : charsOfThisBlock.keySet()) {
				PreparedStatement statement = conn.prepareStatement(SELECT_REFS_OF_CHAR_SQL);
				statement.setString(1, character.name());
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					long currValueOnDb = resultSet.getLong(1);
					charsToUpdate.put(character, currValueOnDb + charsOfThisBlock.get(character));
				} else {
					charsToInsert.put(character, charsOfThisBlock.get(character));
				}
			}
			for (SmashBrosCharacter character : charsToInsert.keySet()) {
				PreparedStatement statement = conn.prepareStatement(INSERT_CHARREF_SQL);
				statement.setString(1, character.name());
				long updatedRef = charsToInsert.get(character);
				statement.setLong(2, updatedRef);
				statement.executeUpdate();
				Logger.debug("Inserted charRef [" + character.name() + ", " + updatedRef + "] ",
						BatchAsyncPersistExecuter.class);
			}
			for (SmashBrosCharacter character : charsToUpdate.keySet()) {
				PreparedStatement statement = conn.prepareStatement(UPDATE_CHARREF_SQL);
				long updatedRef = charsToUpdate.get(character);
				statement.setLong(1, updatedRef);
				statement.setString(2, character.name());
				statement.executeUpdate();
				Logger.debug("Updated charRef [" + character.name() + ", " + updatedRef + "] ",
						BatchAsyncPersistExecuter.class);
			}
		}

		// ASSUMES THAT NO ONE IS GOING TO ALTER THE wordcounts TABLE DURING ITS
		// EXCECUTION
		private void updateWordCounts() throws SQLException {
			wordCountsOnTransaction.clear();
			// we know it can't get lower than this
			int snapshotSize = wordOccurencesToPersist.size();
			for (int i = 0; i < snapshotSize; i++) {
				wordCountsOnTransaction.add(wordOccurencesToPersist.poll());
			}
			HashMap<String, Long> countsOfThisBlock = new HashMap<String, Long>();
			for (WordOccurrence occurrence : wordCountsOnTransaction) {
				Long currCount = countsOfThisBlock.get(occurrence.getWord());
				countsOfThisBlock.put(occurrence.getWord(), currCount != null ? currCount + 1 : 1);
			}
			HashMap<String, Long> countsToUpdate = new HashMap<String, Long>();
			HashMap<String, Long> countsToInsert = new HashMap<String, Long>();
			for (String word : countsOfThisBlock.keySet()) {
				PreparedStatement statement = conn.prepareStatement(SELECT_COUNT_OF_WORD_SQL);
				statement.setString(1, word);
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					long currValueOnDb = resultSet.getLong(1);
					countsToUpdate.put(word, currValueOnDb + countsOfThisBlock.get(word));
				} else {
					countsToInsert.put(word, countsOfThisBlock.get(word));
				}
			}
			for (String word : countsToInsert.keySet()) {
				PreparedStatement statement = conn.prepareStatement(INSERT_WORDCOUNT_SQL);
				statement.setString(1, word);
				long updatedCount = countsToInsert.get(word);
				statement.setLong(2, updatedCount);
				statement.executeUpdate();
				Logger.debug("Inserted wordCount [" + word + ", " + updatedCount + "] ",
						BatchAsyncPersistExecuter.class);
			}
			for (String word : countsToUpdate.keySet()) {
				PreparedStatement statement = conn.prepareStatement(UPDATE_WORDCOUNT_SQL);
				long updatedCount = countsToUpdate.get(word);
				statement.setLong(1, updatedCount);
				statement.setString(2, word);
				statement.executeUpdate();
				Logger.debug("Updated wordCount [" + word + ", " + updatedCount + "] ", BatchAsyncPersistExecuter.class);
			}
		}

		private void rollbackCharRank() {
			charReferencesToPersist.addAll(charReferencesOnTransaction);
		}

		private void rollbackWordCount() {
			wordOccurencesToPersist.addAll(wordCountsOnTransaction);
		}
	}
}
