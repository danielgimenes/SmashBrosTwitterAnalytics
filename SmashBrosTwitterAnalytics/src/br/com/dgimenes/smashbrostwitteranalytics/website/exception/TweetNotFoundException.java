package br.com.dgimenes.smashbrostwitteranalytics.website.exception;

public class TweetNotFoundException extends Exception {
	private static final long serialVersionUID = 4844243043866193979L;

	private Long id;

	public TweetNotFoundException(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	@Override
	public String getMessage() {
		return "Tweet of id " + this.id + " was not found.";
	}

}
