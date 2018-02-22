import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * Hlavn� t��da - spou�t� hlavn� okno aplikace
 * @author Martin Ryba
 *
 */
public class PokerHint extends Application{

	public static void main(String[] args){
		launch(args);
	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Poker hint");
		primaryStage.setScene(getScene());
		primaryStage.show();
		
	}
	/**
	 * vytvo�� z�kladn� sc�nu
	 * @return hlavn� sc�na
	 */
	private Scene getScene() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("view/MainWindow.fxml"));
		Pane root = null;
		
		try {
			root = (Pane)loader.load();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new Scene(root);
	}

}
