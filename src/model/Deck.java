package model;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

/**
 * P�edstavuje bal��ek karet s voliteln� nastavenou kapacitou
 * @author Martin Ryba
 *
 */
public class Deck extends SimpleListProperty<PokerCard>{
	/** kapacita - nefunguje jako limit karet, ale sp�e jako doporu�en� pro u�ivatele*/
	private int capacity;
	
	/**
	 * Vytvo�� bal��ek obsahuj�c� vybran� karty
	 * @param cards karty, kter� m� bal��ek obsahovat
	 */
	public Deck(PokerCard...cards){
		this(Integer.MAX_VALUE, cards);
	}
	
	/**
	 * Vytvo�� bal��ek obsahuj�c� vybran� karty a se zadanou kapacitou
	 * @param capacity kapacita - nefunguje jako limit karet, ale sp�e jako doporu�en� pro u�ivatele
	 * @param cards karty, kter� m� bal��ek obsahovat
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
	 * Vytvo�� pr�zdn� bal��ek s neomezenou kapacitou
	 */
	public Deck(){
		super(FXCollections.observableArrayList());
		this.capacity = Integer.MAX_VALUE;
	}
	
	/**
	 * Ur�� jestli bal��ek dos�hl maxim�ln� doporu�en� kapacity
	 * @return true pokud ano
	 */
	public boolean isFull(){
		return (this.size() >= capacity);
	}
	
	/**
	 * Nastav� doporu�enou kapacitu
	 * @param capacity nov� doporu�en� kapacita
	 * @return true pokud se akce poda�ila, false pokud do�lo k chyb�
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
	 * Vr�t� dooporu�enou kapacitu
	 * @return doporu�en� kapacita
	 */
	public int getCapacity(){
		return capacity;
	}
	
	/**
	 * Vytvo�� pokerov� deck. Slou�� jako alternativa ke konstruktoru
	 * @return nov� pokerov� deck
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
