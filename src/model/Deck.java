package model;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * Pøedstavuje balíèek karet s volitelnì nastavenou kapacitou
 * @author Martin Ryba
 *
 */
public class Deck extends SimpleListProperty<PokerCard>{
	/** kapacita - nefunguje jako limit karet, ale spíše jako doporuèení pro uživatele*/
	private int capacity;
	
	/**
	 * Vytvoøí balíèek obsahující vybrané karty
	 * @param cards karty, které má balíèek obsahovat
	 */
	public Deck(PokerCard...cards){
		this(Integer.MAX_VALUE, cards);
	}
	
	/**
	 * Vytvoøí balíèek obsahující vybrané karty a se zadanou kapacitou
	 * @param capacity kapacita - nefunguje jako limit karet, ale spíše jako doporuèení pro uživatele
	 * @param cards karty, které má balíèek obsahovat
	 */
	public Deck(int capacity, PokerCard...cards){
		this();
		this.capacity = capacity;
		
		if(cards.length > capacity){
			System.out.println("Exceeding deck limit! Make sure to delete some cards! ");
		}
		else{
			this.setAll(cards);
		}
	}

	/**
	 * Vytvoøí prázdný balíèek s neomezenou kapacitou
	 */
	public Deck(){
		super(FXCollections.observableArrayList());
		this.capacity = Integer.MAX_VALUE;
	}
	
	/**
	 * Urèí jestli balíèek dosáhl maximální doporuèené kapacity
	 * @return true pokud ano
	 */
	public boolean isFull(){
		return (this.size() >= capacity);
	}
	
	/**
	 * Nastaví doporuèenou kapacitu
	 * @param capacity nová doporuèená kapacita
	 * @return true pokud se akce podaøila, false pokud došlo k chybì
	 */
	public boolean setCapacity(int capacity){
		if(this.size() > capacity){
			return false;
		}
		else{
			this.capacity = capacity;
			return true;
		}
	}
	
	/**
	 * Vrátí dooporuèenou kapacitu
	 * @return doporuèená kapacita
	 */
	public int getCapacity(){
		return capacity;
	}
	
	/**
	 * Vytvoøí pokerový deck. Slouží jako alternativa ke konstruktoru
	 * @return nový pokerový deck
	 */
	public static Deck getFullPokerDeck(){
		Deck fullDeck = new Deck();
		for(Rank r: Rank.values()){
			for(Suit s: Suit.values()){
				fullDeck.add(new PokerCard(s, r));
			}
		}
		fullDeck.setCapacity(52);
		return fullDeck;
	}

}
