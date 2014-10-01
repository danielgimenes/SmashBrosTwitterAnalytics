package br.com.dgimenes.smashbrostwitteranalytics.website.model.dto;

import java.util.Date;

public class TweetCountPerHour {
	private Date hour;
	private Long count;

	public TweetCountPerHour(Date hour, Long count) {
		super();
		this.hour = hour;
		this.count = count;
	}

	public Date getHour() {
		return hour;
	}

	public Long getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hour == null) ? 0 : hour.hashCode());
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
		TweetCountPerHour other = (TweetCountPerHour) obj;
		if (hour == null) {
			if (other.hour != null)
				return false;
		} else if (!hour.equals(other.hour))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TweetCountPerHour [hour=" + hour + ", count=" + count + "]";
	}
}
