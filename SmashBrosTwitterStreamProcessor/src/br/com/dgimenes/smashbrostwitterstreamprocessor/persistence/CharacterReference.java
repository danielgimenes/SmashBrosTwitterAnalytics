package br.com.dgimenes.smashbrostwitterstreamprocessor.persistence;

import br.com.dgimenes.smashbrostwitterstreamprocessor.persistence.model.SmashBrosCharacter;

public class CharacterReference {
	private SmashBrosCharacter character;

	public CharacterReference(SmashBrosCharacter character) {
		super();
		this.character = character;
	}

	public SmashBrosCharacter getCharacter() {
		return character;
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
		CharacterReference other = (CharacterReference) obj;
		if (character != other.character)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CharReference [character=" + character + "]";
	}

}
