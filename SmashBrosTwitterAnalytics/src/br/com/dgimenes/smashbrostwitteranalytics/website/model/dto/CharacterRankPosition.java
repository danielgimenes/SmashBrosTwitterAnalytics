package br.com.dgimenes.smashbrostwitteranalytics.website.model.dto;

import br.com.dgimenes.smashbrostwitteranalytics.website.model.SmashBrosCharacter;

public class CharacterRankPosition {
	private SmashBrosCharacter character;
	private Long refs;

	public CharacterRankPosition(SmashBrosCharacter character, Long refs) {
		super();
		this.character = character;
		this.refs = refs;
	}

	public SmashBrosCharacter getCharacter() {
		return character;
	}

	public Long getRefs() {
		return refs;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((character == null) ? 0 : character.hashCode());
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
		CharacterRankPosition other = (CharacterRankPosition) obj;
		if (character != other.character)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CharacterRankPosition [character=" + character + ", refs=" + refs + "]";
	}
}
