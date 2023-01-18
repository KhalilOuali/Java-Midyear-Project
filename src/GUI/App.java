package GUI;

import java.io.IOException;
import Core.DBManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class App extends Application {
	private static Stage stage;

	public static void main(String[] args) throws Exception {
		if (DBManager.initConnection())
			launch(args);
		Platform.exit();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;

		try {
			for (AppScreen appScreen : AppScreen.values()) {
				appScreen.setup();
			}
		} catch (IOException e) {
			System.err.println("\n! Critical Error: Couldn't load UIs\n" + e);
			Platform.exit();
			return;
		}

		setScreen(AppScreen.LOGINSCREEN, 0);
		stage.show();
	}

	static void setScreen(AppScreen screen, int id) {
		screen.getUIController().setup(id);
		stage.setScene(screen.getScene());
		stage.setTitle(screen.getWindowTitle());
	}

	@Override
	public void stop() throws Exception {
		DBManager.closeConnection();
		super.stop();
	}
	
}
