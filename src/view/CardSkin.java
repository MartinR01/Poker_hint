package view;

import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;

/**
 * Slou�� jako z�klad pro vytv��en� konkr�tn�ch karet. Lze tak� vyu��t jako vizu�ln� filler.
 * @author Martin Ryba
 *
 */
public class CardSkin extends StackPane{
	/** pasivn� styl karty */
	private final String DEFAULT_STYLE = "-fx-border-color: black; -fx-background-color: white; -fx-border-radius:5; -fx-background-radius:6;";
	/* z�kladn� v��ka */
	private final double IMPL_HEIGHT = 75;
	/* z�kladn� ���ka */
	private final double IMPL_WIDTH = 50;
	
	/**
	 * Vytvo�� novou instanci podle z�kladn�ch hodnot
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
	 * resetuje styl na z�kladn�
	 */
	public void resetStyle(){
		this.setStyle(DEFAULT_STYLE);
	}

}
