package control;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import model.Calculator;
import model.Deck;
import model.PokerCard;

/**
 * Spravuje hlavní okno a slouží pro hromadné ovládání hráèù
 * @author Martin Ryba
 *
 */
public class MainWindowController implements Initializable {
	/** maximální poèet hráèù */
	private final int PLAYER_LIMIT = 10;

	/** odkaz na kalkulaèku */
	private Calculator calculator;
	
	@FXML
	/** odkaz na kontroler hlavního balíèku */
	private CardCollectionController deckController;
	@FXML
	/** odkaz na kontroler dealera */
	private CardCollectionController dealerController;
	
	@FXML
	/** odkaz na samotnou komponentu dealera */
	private Node dealer;
	
	@FXML
	/** Pane, do kterého se pøidávají hráèi */
	private FlowPane players;
	
	@FXML
	/** progress bar pøi výpoètu šance na výhru */
	private ProgressBar progressBar;
	
	@FXML
	/** cancel button pøi výpoètu šance na výhru */
	private Button cancelButton;
	
	@FXML
	/** obaluje progressBar a cancelButton - odkaz pro jejich hromadné skrývání/zobrazování */
	private HBox progressDisplayHBox;
	
	@FXML
	/** tlaèítko pro pøidání hráèe */
	private Button addPlayerBT;
	
	/** seznam balíèkù hráèù */
	private ArrayList<Deck> playerDecks;
	
	/** úkol výpoètu pravdìpodobností - po vìtšinu èasu null*/
	private Task<Void> oddsTask;
	
	@FXML
	/**
	 * zruší výpoèet pravdìpodobnosti
	 */
	private void cancelCalculation(){
		if(oddsTask != null && oddsTask.isRunning())
			oddsTask.cancel();
	}
	

	@FXML
	/**
	 * vrátí všechny karty na pùvodní místo. Ponechá poèet hráèù
	 */
	private void resetCards(){
		resetDealer();
		
		for(Deck player : playerDecks){
			player.retainAll();
		}
		
		setupMainDeck();
	}
	
	@FXML
	/**
	 * Restartuje celou hru do pùvodního stavu - tj. vrátí karty na pùvodní místo a smaže všechny pøidané hráèe
	 */
	private void resetGame(){
		resetDealer();
		
		players.getChildren().retainAll();
		players.getChildren().add(addPlayerBT);
		
		setupMainDeck();
		addPlayer();
	}
	
	/**
	 * Nastaví dealera do pùvodního stavu
	 */
	private void resetDealer(){
		//remove from dealer
		Deck dealerDeck = dealerController.getDeck();
		Deck mainDeck = deckController.getDeck();
		
		mainDeck.addAll(dealerDeck);
		dealerDeck.retainAll();
	}
	
	/**
	 * Nastaví hlavní balíèek do základního stavu
	 */
	private void setupMainDeck() {
		deckController.getDeck().retainAll(); //vycisti
		deckController.setDeck(Deck.getFullPokerDeck());
		calculator.setDeck(deckController.getDeck());
	}
	
	@FXML
	/**
	 * Pøidá nového hráèe do hry, pokud nebylo dosažneo limitu
	 */
	private void addPlayer(){
		//playersList.getItems().add(new CardCollectionController());
		if(players.getChildren().size() < PLAYER_LIMIT+1){
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PlayerHand.fxml"));
			try {
				Node newPlayer = loader.load();
				PlayerHandController newPlayerControl = (PlayerHandController)loader.getController();
				
				players.getChildren().add(players.getChildren().size()-1,newPlayer);
				playerDecks.add(newPlayerControl.getDeck());
				
				newPlayerControl.deleteBT.setOnAction(e ->{
					if(!CardCollectionController.cannotBeChanged.getValue()){
						Deck playerDeck = newPlayerControl.getDeck();
						Deck mainDeck = deckController.getDeck();
						playerDecks.remove(playerDeck);
						players.getChildren().remove(newPlayer);
						
						for(PokerCard card : playerDeck){
							mainDeck.add(card);
						}
						
						if(!players.getChildren().contains(addPlayerBT)){
							players.getChildren().add(addPlayerBT);
						}
					}
				});
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(players.getChildren().size() == PLAYER_LIMIT+1){
				players.getChildren().remove(addPlayerBT);
			}
		}
		else{
			Alert maxPlayersAlert = new Alert(AlertType.ERROR);
			maxPlayersAlert.setTitle("Maximum players");
			maxPlayersAlert.setHeaderText("Reached maximum number of players");
			maxPlayersAlert.setContentText("You can only have "+PLAYER_LIMIT+ " players in standard Poker!");
			maxPlayersAlert.show();
		}
		
		
		
		//calculator.getPlayerHands().add(((CardCollectionController)players.getChildren().get(players.getChildren().size()-1)).getDeck());
		
	}
	
	@FXML
	/**
	 * Zadá kalkulaèce úkol na výpoèet pravdìpodobností na výhru a zobrazuje ho na progressBaru
	 */
	private void determineOdds(){
		
		ArrayList<Deck> activePlayers = new ArrayList<Deck>();
		for(Deck p : playerDecks){
			if(p.isFull()){
				activePlayers.add(p);
			}
		}
		if(activePlayers.size() < playerDecks.size()){
			Alert inActivePlayerAlert = new Alert(AlertType.INFORMATION);
			inActivePlayerAlert.setTitle("Inactive player");
			inActivePlayerAlert.setHeaderText("Some players have less than 2 cards in hand.");
			inActivePlayerAlert.setContentText("Please note that some player/s do not have 2 cards in hand and thus are not included in calculations.");
			inActivePlayerAlert.show();
		}
		
		oddsTask = calculator.calculateOdds(activePlayers);
		progressBar.progressProperty().unbind();
		progressBar.setProgress(0);
		progressBar.progressProperty().bind(oddsTask.progressProperty());
		
		oddsTask.setOnRunning(e->{
			progressDisplayHBox.setVisible(true);
			CardCollectionController.cannotBeChanged.set(true);
		});
		
		oddsTask.setOnSucceeded(e ->{
			finishWork();
		});
		
		oddsTask.setOnCancelled(e ->{
			finishWork();
		});
		
		oddsTask.setOnFailed(e -> {
			finishWork();
		});
		
		final Thread thread = new Thread(oddsTask, "odds calculations");
		thread.setDaemon(true);
		thread.start();
	}
	/**
	 * Dokonèovací úkony výpoètu pravdìpodobnosti
	 */
	private void finishWork(){
		progressDisplayHBox.setVisible(false);
		CardCollectionController.cannotBeChanged.set(false);
		oddsTask = null;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		playerDecks = new ArrayList<Deck>();
		calculator = Calculator.getInstance();
		calculator.setDealer(dealerController.getDeck());
		
		
		dealerController.getDeck().setCapacity(5);
		
		//dealerController.getDeck().
		//playersList.setCellFactory(e -> {return new PlayerCell();});
		// double max value nefungoval -> chceme vyplnit max prostoru
		//playersList.setPrefHeight(4000);
		//dealer.setStyle(dealer.getStyle()+"-fx-border-color: black;");
		
		setupMainDeck();
		addPlayer();
	}
}
