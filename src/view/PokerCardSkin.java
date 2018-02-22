package view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import model.PokerCard;

/**
 * Instance t�to t��dy p�edstavuj� pokerov� karty
 * @author Martin Ryba
 *
 */
public class PokerCardSkin extends CardSkin{
	/** odkaz na kartu, kterou tato instance p�edstavuje */
	private final PokerCard pokerCard;
	
	/**
	 * Vytvo�� instanci na z�klad� p�edan� karty 
	 * @param pokerCard karta, kteoru chceme vizualizovat
	 */
	public PokerCardSkin(PokerCard pokerCard){
		super();
		this.pokerCard = pokerCard;
		
		Label suitLB = new Label(pokerCard.getSuit().toString());
		suitLB.setFont(new Font(30));
		
		switch(pokerCard.getSuit()){
		case HEARTS:
		case DIAMONDS:
			suitLB.setStyle("-fx-text-fill:red");
			break;
		default:
			suitLB.setStyle("-fx-text-fill:black");
		}
		
		Label rankLB = new Label(pokerCard.getRank().toString());
		//Label rankRotateLB = new Label(pokerCard.getRank().toString());
		//rankRotateLB.setRotate(180);
		this.getChildren().addAll(suitLB, rankLB /*, rankRotateLB*/);
		setAlignment(rankLB, Pos.TOP_LEFT);
		//setAlignment(rankRotateLB, Pos.BOTTOM_RIGHT);
		
		Tooltip t = new Tooltip(pokerCard.getCardName());
		Tooltip.install(this, t);
	}
	
	/**
	 * Vr�t� kartu, kterou tato vizualizace p�edstavuje
	 * @return vizualizovan� karta
	 */
	public PokerCard getCard(){
		return pokerCard;
	}

}
