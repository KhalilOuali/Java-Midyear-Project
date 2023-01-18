package GUI;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

/**
 * An enum holding the information for each app interface
 */
enum AppScreen {
	LOGINSCREEN("LoginUI.fxml", "Authentication"),
	RESEARCHERSCREEN("ResearcherUI.fxml", "Researcher Dashboard"),
	EXPERIMENTSCREEN("ExperimentUI.fxml", "Experiment Dashboard"),
	DIRECTORSCREEN("DirectorUI.fxml", "Director Dashboard"),
	INVENTORYSCREEN("InventoryUI.fxml", "Inventory Dashboard");

	private String fileName;
	private Scene scene;
	private UIController uiController;
	private String windowTitle;

	private AppScreen(String fileName, String windowTitle) {
		this.fileName = fileName;
		this.windowTitle = windowTitle;
	}

	public void setup() throws IOException {
		FXMLLoader loader;
		loader = new FXMLLoader(getClass().getResource(fileName));
		scene = new Scene(loader.load());
		uiController = loader.getController();
	}

	public String getFileName() {
		return fileName;
	}
	public Scene getScene() {
		return scene;
	}
	public UIController getUIController() {
		return uiController;
	}
	public String getWindowTitle() {
		return windowTitle;
	}
	
}
