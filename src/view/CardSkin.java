package view;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

/**
 * Slouží jako základ pro vytváøení konkrétních karet. Lze také využít jako vizuální filler.
 * @author Martin Ryba
 *
 */
public class CardSkin extends StackPane{
	/** pasivní styl karty */
	private final String DEFAULT_STYLE = "-fx-border-color: black; -fx-background-color: white; -fx-border-radius:5; -fx-background-radius:6;";
	/* základní výška */
	private final double IMPL_HEIGHT = 75;
	/* základní šíøka */
	private final double IMPL_WIDTH = 50;
	
	/**
	 * Vytvoøí novou instanci podle základních hodnot
	 */
	public CardSkin(){
		this.setStyle(DEFAULT_STYLE);
		this.setPadding(new Insets(3));
		
		this.setMinHeight(IMPL_HEIGHT);
		this.setMinWidth(IMPL_WIDTH);
		
		this.setOnMouseEntered(e -> {
			// zvyrazni plochu za kartou
			this.setStyle(DEFAULT_STYLE+"-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);");
		});
		
		this.setOnMouseExited(e->{
			resetStyle();
		});
	}
	
	/**
	 * resetuje styl na základní
	 */
	public void resetStyle(){
		this.setStyle(DEFAULT_STYLE);
	}

}
