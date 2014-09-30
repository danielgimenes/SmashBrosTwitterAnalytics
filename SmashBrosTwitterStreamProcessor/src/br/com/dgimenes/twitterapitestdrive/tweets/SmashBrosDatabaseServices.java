package br.com.dgimenes.twitterapitestdrive.tweets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmashBrosDatabaseServices {
	// create table tweets(id bigint constraint tweetuniqueid primary key, time
	// timestamp not null, rt boolean, rtid bigint, lang text, username text not
	// null, tweet text not null);
	private static final String SERVER_NAME = "localhost";
	private static final String PORT_NUMBER = "5432";
	private static final String USER = "postgres";
	private static final String PASSWD = "cogitoR341";
	private static final String DB_NAME = "smashbrostweets_db";
	private static final String INSERT_TWEET_SQL = "INSERT INTO tweets(id, time, rt, rtid, lang, username, tweet) VALUES (?,?,?,?,?,?,?);";
	private static final String ALL_TWEET_IDS_WHERE_LANG_NULL_SQL = "SELECT id FROM tweets WHERE lang IS NULL;";
	private static final String UPDATE_TWEET_RT_AND_LANG_SQL = "UPDATE tweets SET rt = ?, rtid = ?, lang = ? WHERE id = ?;";
	private static Connection conn;

	private static SmashBrosDatabaseServices instance;

	private SmashBrosDatabaseServices() {
	}

	public static SmashBrosDatabaseServices getInstance() {
		if (instance == null) {
			instance = new SmashBrosDatabaseServices();
		}
		return instance;
	}

	private static void createConnection() throws SQLException {
		conn = DriverManager.getConnection("jdbc:postgresql://" + SERVER_NAME + ":" + PORT_NUMBER + "/" + DB_NAME,
				USER, PASSWD);
	}

	public List<Long> getAllTweetIdsWhereLangIsNull() throws SQLException {
		if (conn == null || conn.isClosed()) {
			createConnection();
		}
		PreparedStatement statement = conn.prepareStatement(ALL_TWEET_IDS_WHERE_LANG_NULL_SQL);
		ResultSet rs = statement.executeQuery();
		List<Long> ids = new ArrayList<Long>();
		while (rs.next()) {
			ids.add(rs.getLong("id"));
		}
		return ids;
	}
	
	public synchronized void updateTweetRTandLang(long tweetId, boolean isRT, long rtId, String lang) throws SQLException {
		if (conn == null || conn.isClosed()) {
			createConnection();
		}
		conn.setAutoCommit(false);
		PreparedStatement statement = conn.prepareStatement(UPDATE_TWEET_RT_AND_LANG_SQL);
		statement.setBoolean(1, isRT);
		statement.setLong(2, rtId);
		statement.setString(3, lang);
		statement.setLong(4, tweetId);
		statement.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
		System.out.println(String.format("tweet [%d] updated", tweetId));
	}

	public synchronized void persistTweet(long tweetId, Date tweetTime, boolean isRT, long rtId, String lang,
			String userName, String tweet) throws SQLException {
		if (conn == null || conn.isClosed()) {
			createConnection();
		}
		conn.setAutoCommit(false);
		PreparedStatement statement = conn.prepareStatement(INSERT_TWEET_SQL);
		statement.setLong(1, tweetId);
		statement.setTimestamp(2, new java.sql.Timestamp(tweetTime.getTime()));
		statement.setBoolean(3, isRT);
		statement.setLong(4, rtId);
		statement.setString(5, lang);
		statement.setString(6, userName);
		statement.setString(7, tweet);
		statement.executeUpdate();
		conn.commit();
		conn.setAutoCommit(true);
		System.out.println(String.format("tweet [%d] persisted", tweetId));
	}
}