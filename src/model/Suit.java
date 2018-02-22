package model;

/**
 * Barvy pokerových karet
 * @author Martin Ryba
 *
 */
public enum Suit {
	DIAMONDS, CLUBS, HEARTS, SPADES;
	
	@Override
	public String toString(){
		switch(this){
		case DIAMONDS:
			return "\u2666";
		case CLUBS:
			return "\u2663";
		case SPADES:
			return "\u2660";
		case HEARTS:
			return "\u2665";
		default:
			return "Error";
		}
	}
}
