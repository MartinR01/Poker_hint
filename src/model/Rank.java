package model;

/**
 * Hodnoty pokerových karet, v poøadí podle hodnosti
 * @author Martin Ryba
 *
 */
public enum Rank {
	TWO, THREE, FOUR, FIVE, SIX, SEVEN,
    EIGHT, NINE, TEN, JACK, QUEEN, KING, ACE;

	@Override
	public String toString(){
		switch(this){
		case KING:
			return "K";
		case QUEEN:
			return "Q";
		case JACK:
			return "J";
		case ACE:
			return "A";
		default:
			return ((Integer)(this.ordinal()+2)).toString();
		}
	}
}
