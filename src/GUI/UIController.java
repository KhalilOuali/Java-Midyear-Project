package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public abstract class UIController {
	@FXML
	protected Label statusLabel;

	public abstract void setup(int id);

	protected void setStatus(Color color, String msg) {
		statusLabel.setTextFill(color);
		statusLabel.setText(msg);
	}

	protected void setStatus(Color color, String msg, Exception e) {
		setStatus(color, "⚠ " + msg);
		System.err.println("\n" + msg + "\n" + e);
	}

}
