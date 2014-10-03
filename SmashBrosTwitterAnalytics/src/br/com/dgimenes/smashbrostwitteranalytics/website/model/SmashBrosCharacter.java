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
package br.com.dgimenes.smashbrostwitteranalytics.website.model;

public enum SmashBrosCharacter {
	MARIO("Mario"), //
	DONKEY_KONG("Donkey Kong"), //
	LINK("Link"), //
	SAMUS("Samus"), //
	KIRBY("Kirby"), //
	FOX("Fox"), //
	PIKACHU("Pikachu"), //
	BOWSER("Bowser"), //
	PIT("Pit"), //
	VILLAGER("Villager"), //
	MEGA_MAN("Mega Man"), //
	WII_FIT_TRAINER("Wii Fit Trainer"), //
	PIKMIN("Pikmin"), //
	LUIGI("Luigi"), //
	PEACH("Peach"), //
	TOON_LINK("Toon Link"), //
	SONIC("Sonic"), //
	MARTH("mMarth"), //
	ROSALINA("Rosalina"), //
	ZELDA("Zelda"), //
	KING_DEDEDE("King Dedede"), //
	LUCARIO("Lucario"), //
	LITTLE_MAC("Little Mac"), //
	DIDDY_KONG("Diddy Kong"), //
	ZERO_SUIT_SAMUS("Zero Suit Samus"), //
	SHEIK("Sheik"), //
	YOSHI("Yoshi"), //
	CHARIZARD("Charizard"), //
	GRENINJA("Greninja"), //
	IKE("Ike"), //
	MII_FIGHTER("Mii Fighter"), //
	PALUTENA("Palutena"), //
	PAC_MAN("Pac-Man"), //
	CAPTAIN_FALCON("Captain Falcon"), //
	ROBIN("Robin"), //
	SHULK("Shulk"), //
	META_KNIGHT("Meta Knight"); //

	private String printableName;

	public String getPrintableName() {
		return printableName;
	}

	private SmashBrosCharacter(String printableName) {
		this.printableName = printableName;
	}

}
