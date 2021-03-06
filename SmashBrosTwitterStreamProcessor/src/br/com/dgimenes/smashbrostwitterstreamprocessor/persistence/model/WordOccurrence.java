package br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model;

public class WordOccurrence {
	private String word;

	public WordOccurrence(String word) {
		super();
		this.word = word;
	}

	public String getWord() {
		return word;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((word == null) ? 0 : word.hashCode());
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
		WordOccurrence other = (WordOccurrence) obj;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WordOccurrence [word=" + word + "]";
	}

	
}
