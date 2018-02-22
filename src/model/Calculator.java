package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Slouží pro výpoèet pravdìpodobností jednotlivých hráèù
 * @author Martin Ryba
 *
 */
public class Calculator extends Observable{
	/** instance kalkulaèky */
	private static Calculator INSTANCE = new Calculator();
	/** odkaz na hlavní balíèek */
	private Deck deck;
	/** odkaz na dealera */
	private Deck dealer;
	/** seznam hráèských balíèkù, které byly použity v posledním výpoètu */
	private ArrayList<Deck> playerHands;
	/** poèet pøípadù, ve kterých hráè vyhrál */
	private int[] outs;
	/** mapa hráè-šance na výhru */
	private Map<Deck, Double> odds;
	
	/**
	 * Vrátí šanci na výhru zadaného hráèe
	 * @param player balíèek zadaného hráèe
	 * @return šance na výhru ve formì 0 až 1
	 */
	public Double getOdds(Deck player){
		//System.out.println(odds);
		return odds.get(player);
	}
	
	/**
	 * Vypoèítá pravdìpodobnosti výher pro zadanou skupinu hráèù
	 * @param players seznam hráèù, které mezi sebou porovnáváme
	 * @return pozorovatelný Task výpoètu
	 */
	public Task<Void> calculateOdds(ArrayList<Deck> players){
		return new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				this.updateProgress(0, 1);
				playerHands = players;
				int nPlayers = playerHands.size();
				if(nPlayers == 0){
					return null;
				}
				
				odds = new HashMap<Deck, Double>(nPlayers);
				
				if(nPlayers == 1){
					odds.put(playerHands.get(0), 1.0);
					finishWork();
					return null;
				}
				outs = new int[nPlayers];
				
				if(dealer.size() == 5){
					Deck winner = determineWinner(dealer);
					outs[playerHands.indexOf(winner)]++;
				}
				else{
					// kopie, ze které mùžeme vyškrtávat
					Deck tempDeck = new Deck();
					tempDeck.addAll(deck);
					while(tempDeck.size() > 0 && !this.isCancelled()){
						Deck tempDealer = new Deck(tempDeck.get(0));
						tempDealer.addAll(dealer);
						tempDeck.remove(0);
						//System.out.println("deck size: "+tempDeck.size());
						calculateOdds(tempDealer, tempDeck);
						this.updateProgress(deck.size()-tempDeck.size(), deck.size());
						//System.out.println(this.getProgress());
					}
				}
				
//				for(Deck player : playerHands){
//					System.out.println(outs[playerHands.indexOf(player)] + " outs for "+player);
//				}
				
				if(!this.isCancelled()){
					double soucet = 0;
					for(int i : outs)
						soucet += i;
					for(Deck player : playerHands){
						odds.put(player, outs[playerHands.indexOf(player)]/soucet);
					}
					finishWork();
				}
//				setChanged();
//				notifyObservers();
				return null;
			}
		};
	}
	
	/**
	 * Zavolá pozorovatele kalkulaèky v hlavním vláknì aplikace
	 */
	private void finishWork(){
		Platform.runLater(()->{
			setChanged();
			notifyObservers();
		});
	}
	
	/**
	 * Vypoèítá outy pro jednotlivé hráèe ve všech možných pøípadech
	 * @param dealer aktuálnì zvolená kombinace karet dealera
	 * @param deck kopie hlavního balíèku, ze které mùžeme vyškrtávat
	 */
	private void calculateOdds(Deck dealer, Deck deck){
		if(dealer.size() == 5){
			Deck winner = determineWinner(dealer);
			outs[playerHands.indexOf(winner)]++;
		}
		else{
			// kopie, ze které mùžeme vyškrtávat
			Deck tempDeck = new Deck();
			tempDeck.addAll(deck);
			while(tempDeck.size() > 0){
				Deck tempDealer = new Deck(tempDeck.get(0));
				tempDealer.addAll(dealer);
				tempDeck.remove(0);
				calculateOdds(tempDealer, tempDeck);
				//System.out.println(this.getProgress());
			}
		}
		return ;		
	}
	
	/**
	 * Vyhodnotí vítìze v konkrétním pøípadì
	 * @param fullDealer dealer s 5 kartami - tj. konkrétní pøípad
	 * @return balíèek, hráèe, který tento pøípad vyhrál
	 */
	private Deck determineWinner(Deck fullDealer){
		Deck winner = playerHands.get(0);
		VictoryCondition winnerCondi = VictoryCondition.HIGH_CARD;
		int winnerValue = 0;
		int winnerSecondPairValue = 0;
		int winnerHighCardValue = 0;
		
		
		for(Deck player : playerHands){
			//pokud hráè nemá plný poèet karet, pøeskoèíme ho
			if(!player.isFull()){
				
				continue;
			}
			Deck curDeck = new Deck();
			curDeck.addAll(player);
			curDeck.addAll(fullDealer);
			
			VictoryCondition vicCondi = VictoryCondition.HIGH_CARD;
			int value = 0;
			
			//straight
			int tempValue = 0;
			curDeck.sort((card1, card2) ->{
				return card1.getRank().compareTo(card2.getRank());
			});
			int counter = 1;
			for(int i = 0 ; i < curDeck.size()-1; i++){
				int rankDif = curDeck.get(i).getRank().compareTo(curDeck.get(i+1).getRank());
				if(rankDif == -1){
					counter++;
					tempValue = curDeck.get(i+1).getRank().ordinal();
				}
				else if(rankDif == 0){
					continue;
				}
				else{
					if(counter >= 5)
						break;
					else
						counter = 1;
				}
			}
			
			if(counter >= 5){
				vicCondi = VictoryCondition.STRAIGHT;
				value = tempValue;
			}
			
			//flush
			boolean flush = false;
			curDeck.sort((card1, card2) ->{
				return card1.getSuit().compareTo(card2.getSuit());
			});
			for(int i = 0; i < curDeck.size()-4 ;i++){
				if(curDeck.get(i).getSuit().equals(curDeck.get(i+4).getSuit())){
					flush = true;
					tempValue = 0;
					
					for (int j = i; j < curDeck.size(); j++){
						if(curDeck.get(i).getSuit().equals(curDeck.get(j).getSuit())){
							if(curDeck.get(j).getRank().ordinal() > tempValue){
								tempValue = curDeck.get(j).getRank().ordinal();
							}
						}
					}
				}
			}
			if(flush){
				if(vicCondi == VictoryCondition.STRAIGHT && tempValue == value){
					vicCondi = VictoryCondition.STRAIGHT_FLUSH;
				}
				else{
					vicCondi = VictoryCondition.FLUSH;
					value = tempValue;
				}
			}
			
			
			// jiné kombinace
			int pairValue = 0;
			int highCardValue = 0;
			
			if(vicCondi != VictoryCondition.STRAIGHT_FLUSH){
				// hodnoty karet v ruce
				int[] ranks = new int[Rank.values().length];
				VictoryCondition tempWin = VictoryCondition.HIGH_CARD;
				
				highCardValue = Math.max(player.get(0).getRank().ordinal(), player.get(1).getRank().ordinal());
				tempValue = highCardValue;
				for(PokerCard card : curDeck){
					ranks[card.getRank().ordinal()]++;
				}
				
				boolean pairExists = false;
				boolean tripleExiste = false;
				int tempPairValue = 0;
				//int tripleValue = 0;
				
				for(int i = 0; i < ranks.length; i++){
					if(ranks[i] == 2){
						if(pairExists){
							tempWin = VictoryCondition.TWO_PAIRS;
							tempValue = i; // Procházíme od spodu, takže bude vyšší než pùvodní hodnota
						}
						else if(tripleExiste){
							tempWin = VictoryCondition.FULL_HOUSE;
							tempPairValue = i;
							break;
						}
						else{
							tempWin = VictoryCondition.PAIR;
							tempPairValue = i;
							tempValue = i;
							pairExists = true;
						}
					}
					else if(ranks[i] == 3){
						if(pairExists){
							tempWin = VictoryCondition.FULL_HOUSE;
							//tripleValue = i;
							tempValue = i; // Procházíme od spodu, takže bude vyšší než pùvodní hodnota
							break;
						}
						else{
							tempWin = VictoryCondition.TRIPLE;
							//tripleValue = i;
							tempValue = i;
							tripleExiste = true;
						}
					}
					else if(ranks[i] == 4){
						tempWin = VictoryCondition.QUAD;
						tempValue = i;
						break;
					}
				}
				int newSituation = tempWin.compareTo(vicCondi);
				if(newSituation > 0){
					vicCondi = tempWin;
					value = tempValue;
				}
				else if(newSituation == 0){
					if(tempValue > value){
						value = tempValue;
					}
					else if (tempWin == VictoryCondition.FULL_HOUSE){
						if(tempPairValue > pairValue){
							pairValue = tempPairValue;
						}
					}
				}
			}
			// vítìzíme ?
			int ourSituation = vicCondi.compareTo(winnerCondi);
			// we are winning !
			if(ourSituation > 0){
				winner = player;
				winnerCondi = vicCondi;
				winnerValue = value;
				winnerSecondPairValue = pairValue;
				winnerHighCardValue = highCardValue;
			}
			// je to shoda
			else if(ourSituation == 0){
				if(value > winnerValue){
					winner = player;
					winnerCondi = vicCondi;
					winnerValue = value;
					winnerSecondPairValue = pairValue;
					winnerHighCardValue = highCardValue;
				}
				else if(value == winnerValue){
					switch(vicCondi){
					case FULL_HOUSE:
					case TWO_PAIRS:
						if(pairValue > winnerSecondPairValue){
							winner = player;
							winnerCondi = vicCondi;
							winnerValue = value;
							winnerSecondPairValue = pairValue;
							winnerHighCardValue = highCardValue;
							break;
						}
					default:
						//System.out.println("shoda pro "+fullDealer+" mezi "+player+" a "+winner);
						if(highCardValue > winnerHighCardValue){
							winner = player;
							winnerCondi = vicCondi;
							winnerValue = value;
							winnerSecondPairValue = pairValue;
							winnerHighCardValue = highCardValue;
							break;
						}
					}
					
				}
			}
			//System.out.println("player "+player+" has "+vicCondi.toString()+" value of "+value + " flush "+flush+" straight "+straight);
		}
		return winner;
	}
	
	/**
	 * Vrátí instanci kalkulaèky
	 * @return instance kalkulaèky
	 */
	public static Calculator getInstance(){
		return INSTANCE;
	}

	/**
	 * Vrátí hlavní balíèek, ze kterého hráèi tahají karty
	 * @return hlavní balíèek
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Vrací balíèek dealera
	 * @return balíèek dealera
	 */
	public Deck getDealer() {
		return dealer;
	}

	/**
	 * Vrací seznam balíèkù, pro které byly šance vypoèteny
	 * @return seznam použitých balíèkù
	 */
	public ArrayList<Deck> getPlayerHands() {
		return playerHands;
	}

	/**
	 * Nastaví hlavní balíèek, ze kterého hráèi tahají karty
	 * @param deck nový hlavní balíèek
	 */
	public void setDeck(Deck deck) {
		//System.out.println(deck);
		this.deck = deck;
	}

	/**
	 * Nastaví dealera
	 * @param dealer nový dealer
	 */
	public void setDealer(Deck dealer) {
		this.dealer = dealer;
	}
	
	
}