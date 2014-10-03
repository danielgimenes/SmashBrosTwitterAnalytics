/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014 Daniel Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the ("Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED ("AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package br.com.dgimenes.smashbrostwitterstreamprocessor.persistence;

public enum SmashBrosCharacter {
	MARIO("mario"), //
	DONKEY_KONG("donkey kong"), //
	LINK("link"), //
	SAMUS("samus"), //
	KIRBY("kirby"), //
	FOX("fox"), //
	PIKACHU("pikachu"), //
	BOWSER("bowser"), //
	PIT("pit"), //
	VILLAGER("villager"), //
	MEGA_MAN("mega man"), //
	WII_FIT_TRAINER("wii fit trainer", "trainer", "wii fit"), //
	PIKMIN("pikmin"), //
	LUIGI("luigi"), //
	PEACH("peach"), //
	TOON_LINK("toon link", "toon-link"), //
	SONIC("sonic"), //
	MARTH("marth"), //
	ROSALINA("rosalina"), //
	ZELDA("zelda"), //
	KING_DEDEDE("king dedede", "dedede"), //
	LUCARIO("lucario"), //
	LITTLE_MAC("little mac", "mac"), //
	DIDDY_KONG("diddy kong"), //
	ZERO_SUIT_SAMUS("zero suit samus", "suit samus", "zero samus"), //
	SHEIK("sheik"), //
	YOSHI("yoshi"), //
	CHARIZARD("charizard"), //
	GRENINJA("greninja"), //
	IKE("ike"), //
	MII_FIGHTER("mii fighter", "mii"), //
	PALUTENA("palutena"), //
	PAC_MAN("pac-man", "pac man"), //
	CAPTAIN_FALCON("captain falcon", "falcon"), //
	ROBIN("robin"), //
	SHULK("shulk"), //
	META_KNIGHT("meta knight"); //
	
	private String[] referenceStrings;

	private SmashBrosCharacter(String... referenceStrings) {
		this.referenceStrings = referenceStrings;
	}

	public String[] getReferenceStrings() {
		return referenceStrings;
	}
}
