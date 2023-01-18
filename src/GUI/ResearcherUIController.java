package GUI;

import java.sql.SQLException;

import Core.Experiment;
import Core.Field;
import Core.NoMatchException;
import Core.Researcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class ResearcherUIController extends UIController {
	// General
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label idLabel;
	@FXML
	private Label specialtiesLabel;
	@FXML
	private Label statusLabel;

	private Researcher researcher;
	private Experiment assignment;
	private boolean isSupervisor;

	@Override
	public void setup(int id) {
		onReset(null);

		try {
			researcher = new Researcher(id);
		} catch (SQLException | NoMatchException e) {
			researcher = null;
			setStatus(Color.RED, "DB Error fetching researcher.", e);
			return;
		}

		welcomeLabel.setText("Welcome, " + researcher.getFullName() + "!");
		idLabel.setText("Researcher ID: " + researcher.getPersonnelID());

		int size = researcher.getSpecialties().size();
		if (size == 0) {
			specialtiesLabel.setText("None.");
		} else {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < size; i++) {
				Field field = researcher.getSpecialties().get(i);
				builder.append(field.getDesignation());
				if (i < size-1) {
					builder.append(", ");;
				}
			}
			builder.append(".");
			specialtiesLabel.setText(builder.toString());
		}

		setupAssignmentPane();
	}

	@FXML
	void onSignOut(ActionEvent event) {
		App.setScreen(AppScreen.LOGINSCREEN, 0);
	}

	// Assignment tab
	@FXML
	private BorderPane unassignedPane;
	@FXML
	private BorderPane assignedPane;
	@FXML
	private Label assignmentIDLabel;
	@FXML
	private Label assignmentCodeNameLabel;
	@FXML
	private Label supervisorLabel;
	@FXML
	private Label labLabel;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Button manageButton;

	private void setupAssignmentPane() {
		// if not assigned
		if (researcher.getExperimentID() == 0) {
			unassignedPane.setVisible(true);
			assignedPane.setVisible(false);
			suggestionTab.setDisable(false);
			suggestionTab.setTooltip(null);
			assignment = null;
			return;
		}

		// if assigned
		unassignedPane.setVisible(false);

		try {
			assignment = new Experiment(researcher.getExperimentID());
		} catch (SQLException e) {
			assignment = null;
			setStatus(Color.RED, "DB Error fetching assignment information.", e);
			return;
		}

		assignedPane.setVisible(true);

		assignmentIDLabel.setText(String.valueOf(assignment.getExperimentID()));
		assignmentCodeNameLabel.setText(assignment.getCodeName());
		descriptionLabel.setText(assignment.getDescription());

		// Supervisor information
		isSupervisor = assignment.getPersonnelID() == researcher.getPersonnelID();
		manageButton.setVisible(isSupervisor);
		suggestionTab.setDisable(isSupervisor);
		suggestionTab.setTooltip(isSupervisor ? (new Tooltip("You are already managing an experiment.")) : null);
		supervisorLabel.setText(isSupervisor ? "You." : assignment.getSupervisorText());

		// Lab information
		labLabel.setText(assignment.getLabText());
	}

	@FXML
	void onManageExperiment(ActionEvent event) {
		App.setScreen(AppScreen.EXPERIMENTSCREEN, assignment.getExperimentID());
	}

	// Suggestion tab
	@FXML
	private Tab suggestionTab;
	@FXML
	private TextField suggestionCodeNameField;
	@FXML
	private TextArea suggestionDescriptionField;

	@FXML
	void onSubmit(ActionEvent event) {
		String codeName = suggestionCodeNameField.getText();
		String descripton = suggestionDescriptionField.getText();

		if (codeName.isEmpty() || descripton.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please provide a code name and a description.");
			return;
		}

		try {
			researcher.suggestExperiment(codeName, descripton);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error inserting suggestion.", e);
			return;
		}

		setStatus(Color.GREEN, "✔ Suggestion submitted.");
	}

	@FXML
	void onReset(ActionEvent event) {
		suggestionCodeNameField.clear();
		suggestionDescriptionField.clear();
		statusLabel.setText("");
	}

}
