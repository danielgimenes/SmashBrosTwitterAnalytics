package br.com.dgimenes.smashbrostwitteranalytics.website.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "tweets")
@NamedQueries({ @NamedQuery(name = "Tweet.count", query = "SELECT COUNT(t.id) FROM Tweet t"),
	@NamedQuery(name = "Tweet.latest", query = "SELECT t FROM Tweet t WHERE t.lang = \"en\" OR t.lang = \"und\" ORDER BY t.tweetTime DESC"),
		@NamedQuery(name = "Tweet.mostUsedWords", query = "SELECT COUNT(t.id) FROM Tweet t") })
@NamedNativeQueries({ @NamedNativeQuery(name = "Tweet.countPerHour", query = "SELECT TO_CHAR(time, 'YYYYMMDDHH24') AS start, count(id) FROM tweets WHERE time BETWEEN ? AND ? GROUP BY start ORDER BY start;"), })
public class Tweet {
	@Id
	private Long id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "time", nullable = false)
	private Date tweetTime;

	@Column(nullable = false)
	private String userName;
	
	@Column(nullable = false)
	private String screenName;

	@Column(nullable = false)
	private String tweet;

	@Column(nullable = false)
	private Long rtId = 0L;

	@Column(nullable = false)
	private String lang;

	@Column(name = "rt", nullable = false)
	private Boolean isRT;

	public Tweet() {
	}

	public Tweet(long id, Date tweetTime, String userName, String screenName, String tweet, Long rtId, String lang, boolean isRT) {
		super();
		this.id = id;
		this.tweetTime = tweetTime;
		this.userName = userName;
		this.screenName = screenName;
		this.tweet = tweet;
		this.rtId = rtId == null ? 0L : rtId;
		this.lang = lang;
		this.isRT = isRT;
	}

	public Long getId() {
		return id;
	}

	public Date getTweetTime() {
		return tweetTime;
	}

	public String getUserName() {
		return userName;
	}
	
	public String getScreenName() {
		return screenName;
	}

	public String getTweet() {
		return tweet;
	}

	public Long getRtId() {
		return rtId;
	}

	public String getLang() {
		return lang;
	}

	public Boolean isRT() {
		return isRT;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tweet other = (Tweet) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Tweet [id=" + id + ", userName=" + userName + ", tweet=" + tweet + "]";
	}
}
