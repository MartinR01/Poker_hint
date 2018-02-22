package model;

import java.io.Serializable;

import javafx.scene.input.DataFormat;

/**
 * P�edstavuje pokerov� karty - maj� barvu a hodnotu
 * @author Martin Ryba
 *
 */
public class PokerCard implements Serializable{
	private static final long serialVersionUID = 1L;
	/** datov� form�t pro pou�it� v drag and dropu */
	public static final DataFormat dataFormat = new DataFormat("pokerCard");
	/** barva karty */
	private final Suit suit;
	/** hodnota karty */
	private final Rank rank;	
	
	/**
	 * Vytvo�� novou kartu
	 * @param suit zvolen� barva karty
	 * @param rank zvolen� hodnota karty
	 */
	public PokerCard(Suit suit, Rank rank){
		super();
		this.suit = suit;
		this.rank = rank;	
	}
	
	/**
	 * Vrac� barvu karty
	 * @return barva karty
	 */
	public Suit getSuit() {
		return suit;
	}
	
	/**
	 * Vr�t� hodnotu karty
	 * @return hodnota karty
	 */
	public Rank getRank() {
		return rank;
	}

	/**
	 * Vr�t� jm�no karty - oproti toStringu pou��v� slova a ne symboly
	 * @return jm�no karty
	 */
	public String getCardName(){
		return rank.name() + " of "+suit.name();
	}
	
	@Override
	public String toString() {
		return suit +" "+rank;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((suit == null) ? 0 : suit.hashCode());
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
		PokerCard other = (PokerCard) obj;
		if (rank != other.rank)
			return false;
		if (suit != other.suit)
			return false;
		return true;
	}

}
