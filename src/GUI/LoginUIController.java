package GUI;

import java.sql.ResultSet;
import java.sql.SQLException;

import Core.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class LoginUIController extends UIController {
	@FXML
	private TextField loginField;
	@FXML
	private PasswordField passwordField;

	@Override
	public void setup(int id) {
		onReset(null);
	}

	@FXML
	void onSubmit(ActionEvent event) {
		String login = loginField.getText();
		String password = passwordField.getText();

		if (login.isEmpty() || password.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Incomplete login information.");
			return;
		}

		String query = "SELECT personnel_id, role FROM personnel WHERE login = ? AND password = ?;";

		try {
			ResultSet result = DBManager.executeQuery(query, login, password);

			if (!result.next()) {
				setStatus(Color.PURPLE, "❌ Invalid login information.");
				return;
			}

			int id = result.getInt("personnel_id");
			String role = result.getString("role");

			switch (role) {
				case "Researcher":
					App.setScreen(AppScreen.RESEARCHERSCREEN, id);
					break;

				case "Director":
					App.setScreen(AppScreen.DIRECTORSCREEN, id);
					break;

				case "Inventory Manager":
					App.setScreen(AppScreen.INVENTORYSCREEN, id);
					break;

				default:
					setStatus(Color.HOTPINK, "❌ Personnel role unrecognized.");
					break;
			}
		} catch (SQLException e) {
			System.err.println("\n! Database Error: Couldn't verify login.\n" + e);
			setStatus(Color.RED, "Error accessing database.");
			return;
		}
	}

	@FXML
	void onReset(ActionEvent event) {
		loginField.clear();
		passwordField.clear();
		statusLabel.setText("");
	}

}
