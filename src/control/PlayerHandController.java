package control;


import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import model.Calculator;
import model.Deck;

/**
 * PlayerHand slou�� jako wrapper nad CardCollection - nav�c um� zobrazit procenta
 * @author Martin Ryba
 *
 */
public class PlayerHandController implements Initializable, Observer{
	@FXML
	/** popisek �ance na v�hru */
	private Label oddsLB;
	@FXML
	/** odkaz na zabalenou kolekci karet */
	private CardCollectionController cardCollectionController;
	@FXML
	/** ur�eno k naps�n� obsluhy v controlleru parenta */
	public Button deleteBT;
	@FXML
	/** root*/
	private StackPane root;
	
	/**
	 * Vrac� p�i�azen� deck
	 * @return p�i�azen� deck
	 */
	public Deck getDeck(){
		return cardCollectionController.getDeck();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.setOnMouseEntered(e -> deleteBT.setVisible(true));
		root.setOnMouseExited( e -> {
			deleteBT.setVisible(false);
		});
		Calculator.getInstance().addObserver(this);
		cardCollectionController.getDeck().setCapacity(2);
		
		cardCollectionController.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		
		if(o instanceof Calculator){
			Double d = ((Calculator)o).getOdds(getDeck());
			if(d == null){
				oddsLB.setText(null);
			}
			else{
				int odds = (int) (d.doubleValue() * 1000);
				oddsLB.setText(odds/10 +","+odds%10+ " %");
			}
		}
		else if(o instanceof CardCollectionController){
			oddsLB.setText(null);
		}
		
	}

}
