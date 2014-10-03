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
package br.com.dgimenes.smashbrostwitterstreamprocessor.util.legacy_data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import br.com.dgimenes.smashbrostwitterstreamprocessor.control.SmashBrosTwitterStatusListener;
import br.com.dgimenes.smashbrostwitterstreamprocessor.control.configuration.DbAccessConfiguration;
import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.Tweet;

public class LegacyDbTweetsStreamer {
	private static final String SELECT_ALL_TWEETS_SQL = "SELECT id, time, rt, rtid, lang, username, screenname, tweet FROM tweets WHERE lang = 'en' OR lang = 'und';";
	private DbAccessConfiguration dbAccessConfig;

	public LegacyDbTweetsStreamer(DbAccessConfiguration dbAccessConfiguration) {
		this.dbAccessConfig = dbAccessConfiguration;
	}

	public void execute(SmashBrosTwitterStatusListener listener) throws SQLException {
		Connection conn = DriverManager.getConnection(dbAccessConfig.getConnectionString(), dbAccessConfig.getUser(),
				dbAccessConfig.getPassword());

		PreparedStatement statement = conn.prepareStatement(SELECT_ALL_TWEETS_SQL);
		ResultSet resultSet = statement.executeQuery();
		while (resultSet.next()) {
			long id = resultSet.getLong(1);
			Timestamp time = resultSet.getTimestamp(2);
			boolean isRT = resultSet.getBoolean(3);
			long rtId = resultSet.getLong(4);
			String lang = resultSet.getString(5);
			String userName = resultSet.getString(6);
			String screenName = resultSet.getString(7);
			String tweetText = resultSet.getString(8);
			Tweet tweet = new Tweet(id, new Date(time.getTime()), userName, screenName, tweetText, rtId, lang, isRT);
			listener.onStatus(new LegacyStatus(tweet));
		}
		conn.close();
	}

}
