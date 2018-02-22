package control;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import model.Deck;
import model.PokerCard;
import view.CardSkin;
import view.PokerCardSkin;
/**
 * Stará se o správné zobrazení zadaného balíèku karet a funkci drag and drop nad nimi
 * @author Martin Ryba
 *
 */
public class CardCollectionController extends Observable implements ListChangeListener<PokerCard>, Initializable{
	/** zpráva pro plný balíèek */
	private final String fullDeckMessage = "Card limit reached.";
	/** zpráva pro nedokonèené výpoèty */
	private final String cannotBeChangedMessage = "Calculations still running.";
	
	@FXML
	/** zde jsou zobrazeny dostupné karty */
	private FlowPane content;
	@FXML
	/** slouží pro oznamování napø. plného balíèku, pøekryje zbytek komponent */
	private Node announcer;
	@FXML
	/** text oznámení */
	private Label anouncementLB;
	@FXML
	/** root */
	private Node root;
	
	/** zobrazovaný balíèek */
	private Deck deck;
	/** prázdná karta - pro vizualizaci drag and dropu */
	private CardSkin emptyCard;
	
	/** spoleèné pro všechny balíèky karet - umožòuje hromadnì vypnout jejich editaci */
	public static SimpleBooleanProperty cannotBeChanged = new SimpleBooleanProperty(false);
	
	
	/**
	 * Vytvoøí novou instanci s prázdným balíèkem
	 */
	public CardCollectionController(){
		this.setDeck(new Deck());
		emptyCard = new CardSkin();
		emptyCard.setVisible(false);
	}
	
	/**
	 * vrací pøiøazený deck
	 * @return pøiøazený deck
	 */
	public Deck getDeck(){
		return deck;
	}
	
	/**
	 * Nastaví pøiøazený deck
	 * @param deck nový pøiøazený deck
	 */
	public void setDeck(Deck deck){
		this.deck = deck;
		
		deck.forEach(e  -> {
			content.getChildren().add(new PokerCardSkin(e));
		});
		//contentPane.getChildren().setAll(deck);
		deck.addListener(this);
		//deck.notify();
	}

	@Override
	public void onChanged(javafx.collections.ListChangeListener.Change<? extends PokerCard> c) {
		//contentPane.getChildren().setAll(c.getList());
		c.next();
		c.getRemoved().forEach(e -> {
			content.getChildren().removeIf(a -> {
					if(a instanceof PokerCardSkin){
						return ((PokerCardSkin)a).getCard().equals(e);
					}
					return false;
				});
		});
		
		//c.next();
		c.getAddedSubList().forEach(e  -> {
			if(content.getChildren().contains(emptyCard)){
				content.getChildren().add(content.getChildren().indexOf(emptyCard),new PokerCardSkin(e));
			}
			else{
				content.getChildren().add(new PokerCardSkin(e));
			}
		});
		
		setChanged();
		notifyObservers();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.setOnMouseEntered(e ->{
			if(cannotBeChanged.get()){
				anouncementLB.setText(cannotBeChangedMessage);
				announcer.setVisible(true);
			}
		});
		
		root.setOnMouseExited(e->{
			anouncementLB.setText(fullDeckMessage);
			announcer.setVisible(false);
		});
		
		//content.setId("contentPane"+this.hashCode());
		
		content.setOnDragDetected(e->{
			Node target = (Node) e.getTarget();
			//nalezne na kterou kartu jsme kliknuli
			while(!(target instanceof FlowPane)){
				if(target instanceof PokerCardSkin){
					break;
				}
				target = target.getParent();
			}
			
			if(target instanceof PokerCardSkin){
				Dragboard db = content.startDragAndDrop(TransferMode.MOVE);
				PokerCardSkin targetPCS = (PokerCardSkin)target;
				targetPCS.resetStyle();
				
				db.setDragView(target.snapshot(new SnapshotParameters(), null), targetPCS.getWidth()/2, targetPCS.getHeight()/2);
				
				ClipboardContent cb = new ClipboardContent();
				cb.put(PokerCard.dataFormat, targetPCS.getCard());
				db.setContent(cb);
				
				content.getChildren().add(content.getChildren().indexOf(targetPCS), emptyCard);
				deck.remove(targetPCS.getCard());
				
			}
		});
		
		root.setOnDragOver(e->{
			// kontrola typu dat !!!
			if(e.getDragboard().hasContent(PokerCard.dataFormat)){
				if(deck.isFull()){
					announcer.setVisible(true);
				}
				else{
					e.acceptTransferModes(TransferMode.MOVE);
				}
			}
		});
		
		root.setOnDragExited(e->{
			announcer.setVisible(false);
		});
		
		content.setOnDragDropped(e->{
			PokerCard card = (PokerCard)e.getDragboard().getContent(PokerCard.dataFormat);
			deck.add(card);
			content.getChildren().remove(emptyCard);
			e.setDropCompleted(true);
		});
		
		content.setOnDragDone(e ->{
			if(!e.isAccepted()){
				PokerCard card = (PokerCard)e.getDragboard().getContent(PokerCard.dataFormat);
				deck.add(card);
			}
			content.getChildren().remove(emptyCard);
		});
	}
}
