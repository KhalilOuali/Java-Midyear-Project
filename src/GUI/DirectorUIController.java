package GUI;

import java.sql.SQLException;
import java.util.List;

import Core.Experiment;
import Core.Field;
import Core.Lab;
import Core.NoMatchException;
import Core.Personnel;
import Core.Request;
import Core.Researcher;
import Core.ResearcherRequest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class DirectorUIController extends UIController {
	// General
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label idLabel;

	private Personnel director;

	@FXML
	private void initialize() {
		//experiments
		experimentIDColumn.setCellValueFactory(new PropertyValueFactory<>("experimentID"));
		experimentCodeNameColumn.setCellValueFactory(new PropertyValueFactory<>("codeName"));
		experimentSupervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisorText"));
	
		experimentStateToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				setupExperimentTable();
				setupSelectedExperiment(null);
			}
		});

		experimentTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Experiment>() {
			@Override
			public void changed(ObservableValue<? extends Experiment> observable, Experiment oldValue, Experiment newValue) {
				setupSelectedExperiment(newValue);
			}
		});

		// labs
		labIDColumn.setCellValueFactory(new PropertyValueFactory<>("labID"));
		labLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
		labExperimentColumn.setCellValueFactory(new PropertyValueFactory<>("experimentText"));

		// researchers
		researcherIDColumn.setCellValueFactory(new PropertyValueFactory<>("personnelID"));
		researcherFullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

		researcherTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Researcher>() {
			@Override
			public void changed(ObservableValue<? extends Researcher> observable, Researcher oldValue, Researcher newValue) {
				setupSelectedResearcher(newValue);
			}
		});
		
		researcherSpecialtiesColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));

		// requests
		requestIDColumn.setCellValueFactory(new PropertyValueFactory<>("requestID"));
		requestExperimentColumn.setCellValueFactory(new PropertyValueFactory<>("experimentText"));
		requestSupervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisorText"));

		requestTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ResearcherRequest>() {
			@Override
			public void changed(ObservableValue<? extends ResearcherRequest> observable, ResearcherRequest oldValue, ResearcherRequest newValue) {
				setupSelectedReqeust(newValue);
			}
		});
		
	}

	@Override
	public void setup(int id) {
		statusLabel.setText("");
		
		try {
			director = new Personnel(id);
		} catch (SQLException e) {
			director = null;
			setStatus(Color.RED, "DB Error fetching director.", e);
			return;
		}

		welcomeLabel.setText("Welcome, " + director.getFullName() + "!");
		idLabel.setText("Director ID: " + director.getPersonnelID());

		onRefresh(null);
		experimentStateToggleGroup.selectToggle(ongoingToggle);
	}

	@FXML
	void onRefresh(ActionEvent event) {
		try {
			experiments = Experiment.fetchExperiments();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching experiments.", e);
		}

		try {
			researchers = Researcher.fetchResearchers();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching researchers.", e);
		}

		try {
			requests = ResearcherRequest.fetchRequests(Request.State.Pending);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching requests.", e);
		}

		setupLabTable();
		setupResearcherTable();
		setupRequestTable();
	}

	@FXML
	void onSignOut(ActionEvent event) {
		App.setScreen(AppScreen.LOGINSCREEN, 0);
	}

	// Experiments tab
	@FXML
	private ToggleGroup experimentStateToggleGroup;
	@FXML
	private RadioButton suggestedToggle;
	@FXML
	private RadioButton ongoingToggle;
	@FXML
	private RadioButton concludedToggle;
	@FXML
	private RadioButton deniedToggle;
	@FXML
	private TableView<Experiment> experimentTable;
	@FXML
	private TableColumn<Experiment, Integer> experimentIDColumn;
	@FXML
	private TableColumn<Experiment, String> experimentCodeNameColumn;
	@FXML
	private TableColumn<Experiment, String> experimentSupervisorColumn;

	private List<Experiment> experiments;
	private Experiment selectedExperiment;

	private void setupExperimentTable() {
		experimentTable.getItems().clear();
		Toggle selectedToggle = experimentStateToggleGroup.getSelectedToggle();

		if (selectedToggle == null) {
			return;
		}

		String stateString = ((RadioButton)selectedToggle).getText();
		Experiment.State state = Experiment.State.valueOf(stateString);

		for (Experiment experiment : experiments) {
			if (experiment.getState().equals(state)) {
				experimentTable.getItems().add(experiment);
			}
		}
	}

	private void setupSelectedExperiment(Experiment selectedExperiment) {
		this.selectedExperiment = selectedExperiment;
		suggestionPane.setVisible(false);
		experimentPane.setVisible(false);

		if (selectedExperiment == null) {
			return;
		}

		if (selectedExperiment.getState().equals(Experiment.State.Suggested)) {
			setupSuggestionPane();
		} else {
			setupExperimentPane();
		}
	}

	// Suggestion pane
	@FXML
	private VBox suggestionPane;
	@FXML
	private Label suggestionIDLabel;
	@FXML
	private TextField suggestionCodeNameField;
	@FXML
	private Label suggestionSupervisorLabel;
	@FXML
	private TextField suggestionLabField;
	@FXML
	private TextArea suggestionDescriptionField;

	@FXML
	void onApproveSuggestion(ActionEvent event) {
		String codeName = suggestionCodeNameField.getText();
		String labIDString = suggestionLabField.getText();
		String description = suggestionDescriptionField.getText();

		if (codeName.isEmpty() || labIDString.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a code name and a lab.");
			return;
		}

		// Lab verificaiton
		int labID;
		Lab lab;
		try {
			labID = Integer.parseInt(labIDString);
			lab = new Lab(labID);

			if (!lab.getExperimentText().equals("None")) {
				setStatus(Color.PURPLE, "❌ Lab occupied. Please specify a vacant lab.");
				return;
			}
		} catch (NumberFormatException e) {
			setStatus(Color.PURPLE, "❌ Please specify a valid lab ID.");
			return;
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error verifying lab ID.", e);
			return;
		} catch (NoMatchException e) {
			setStatus(Color.PURPLE, "❌ Lab doesn't exist. Please specify a valid lab ID.");
			return;
		}

		// Approval
		try {
			selectedExperiment.approveSuggestion(codeName, labID, description);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error approving experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Experiment approved.");
		setupExperimentTable();
	}

	@FXML
	void onDenySuggestion(ActionEvent event) {
		String codeName = suggestionCodeNameField.getText();
		String description = suggestionDescriptionField.getText();
		
		if (codeName.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a code name.");
			return;
		}

		try {
			selectedExperiment.denySuggestion(codeName, description);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error denying experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Experiment denied.");
		setupExperimentTable();
	}

	@FXML
	void onResetSuggestion(ActionEvent event) {
		statusLabel.setText("");
		setupSuggestionPane();
	}

	private void setupSuggestionPane() {
		suggestionPane.setVisible(true);
		suggestionIDLabel.setText(String.valueOf(selectedExperiment.getExperimentID()));
		suggestionCodeNameField.setText(selectedExperiment.getCodeName());
		suggestionSupervisorLabel.setText(selectedExperiment.getSupervisorText());
		suggestionLabField.clear();
		suggestionDescriptionField.setText(selectedExperiment.getDescription());
	}

	// Experiment pane
	@FXML
	private VBox experimentPane;
	@FXML
	private Label experimentIDLabel;
	@FXML
	private Label experimentCodeNameLabel;
	@FXML
	private Label experimentSupervisorLabel;
	@FXML
	private Label experimentLabLabel;
	@FXML
	private Label experimentDescriptionLabel;

	private void setupExperimentPane() {
		experimentPane.setVisible(true);
		experimentIDLabel.setText(String.valueOf(selectedExperiment.getExperimentID()));
		experimentCodeNameLabel.setText(selectedExperiment.getCodeName());
		experimentSupervisorLabel.setText(selectedExperiment.getSupervisorText());
		experimentLabLabel.setText(selectedExperiment.getLabText());
		experimentDescriptionLabel.setText(selectedExperiment.getDescription());
	}

	// Labs tab
	@FXML
	private TableView<Lab> labTable;
	@FXML
	private TableColumn<Lab, Integer> labIDColumn;
	@FXML
	private TableColumn<Lab, String> labLocationColumn;
	@FXML
	private TableColumn<Lab, String> labExperimentColumn;
	@FXML
	private TextField labIDField;
	@FXML
	private TextArea labLocationField;

	private void setupLabTable() {
		labTable.getItems().clear();

		List<Lab> labs;
		try {
			labs = Lab.fetchLabs();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching labs.", e);
			return;
		}

		labTable.getItems().addAll(labs);
	}

	@FXML
	void onAddLab(ActionEvent event) {
		String labLocation = labLocationField.getText();
		
		if (labLocation.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a location.");
			return;
		}

		try {
			Lab.addLab(labLocation);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error adding lab.", e);
			return;
		}
		
		onRefresh(null);
		setStatus(Color.GREEN, "✔ Lab added.");
	}

	@FXML
	void onResetLab(ActionEvent event) {
		statusLabel.setText("");
		labIDField.setText("");
		labLocationField.setText("");
	}

	// Researchers tab
	@FXML
	private TableView<Researcher> researcherTable;
	@FXML
	private TableColumn<Researcher, Integer> researcherIDColumn;
	@FXML
	private TableColumn<Researcher, String> researcherFullNameColumn;

	private List<Researcher> researchers;
	private Researcher selectedResearcher;

	private void setupResearcherTable() {
		researcherTable.getItems().clear();
		researcherTable.getItems().addAll(researchers);
		setupSelectedResearcher(null);
	}

	private void setupSelectedResearcher(Researcher selectedResearcher) {
		this.selectedResearcher = selectedResearcher;
		setupResearcherSpecialtiesTable();
		setupResearcherDetails();
	}

	// Researcher specialties pane
	@FXML
	private VBox researcherSpecialtiesPane;
	@FXML
	private TableView<Field> researcherSpecialtiesTable;
	@FXML
	private TableColumn<Field, String> researcherSpecialtiesColumn;
	@FXML
	private TextField researcherSpecialtyField;

	private void setupResearcherSpecialtiesTable() {
		researcherSpecialtiesTable.getItems().clear();
		researcherSpecialtyField.clear();
		if (selectedResearcher == null) {
			researcherSpecialtiesPane.setDisable(true);
		} else {
			researcherSpecialtiesPane.setDisable(false);
			researcherSpecialtiesTable.getItems().addAll(selectedResearcher.getSpecialties());
		}
	}

	@FXML
	void onResetResarcherSpecialty(ActionEvent event) {
		researcherSpecialtyField.clear();
	}

	@FXML
	void onAddSpecialty(ActionEvent event) {
		String specialtyString = researcherSpecialtyField.getText();
		if (specialtyString.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify specialty desgination.");
			return;
		}

		Field field = new Field(specialtyString);
		if (selectedResearcher.getSpecialties().contains(field)) {
			setStatus(Color.PURPLE, "❌ Researcher already has specified specialty.");
			return;
		}

		try {
			selectedResearcher.addSpecialty(field);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error adding specialty.", e);
			return;
		}

		setupResearcherSpecialtiesTable();
		setStatus(Color.GREEN, "✔ Specialty added.");
	}
	
	// Researcher details pane
	@FXML
	private VBox researcherDetailsPane;
	@FXML
	private Label researcherIDLabel;
	@FXML
	private TextField researcherFullNameField;
	@FXML
	private Label researcherAssignmentLabel;

	private void setupResearcherDetails() {
		if (selectedResearcher == null) {
			researcherDetailsPane.setDisable(true);
			researcherIDLabel.setText("-");
			researcherFullNameField.setText("-");
			researcherAssignmentLabel.setText("-");
		} else {
			researcherDetailsPane.setDisable(false);
			researcherIDLabel.setText(String.valueOf(selectedResearcher.getPersonnelID()));
			researcherFullNameField.setText(selectedResearcher.getFullName());
			researcherAssignmentLabel.setText(selectedResearcher.getExperimentText());
		}
	}

	@FXML
	void onUpdateResearcher(ActionEvent event) {
		String fullName = researcherFullNameField.getText();
		if (fullName.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a full name for researcher.");
			return;
		}

		if (fullName.equals(selectedResearcher.getFullName())) {
			setStatus(Color.PURPLE, "❌ Same full name. Nothing to update.");
			return;
		}
		
		try {
			selectedResearcher.changeResearcherName(fullName);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error updating researcher.", e);
			return;
		}

		researcherTable.refresh();
		setStatus(Color.GREEN, "✔ Researcher updated.");
	}
	
	@FXML
	void onResetResearcher(ActionEvent event) {
		if (selectedResearcher == null) {
			return;
		}
		researcherFullNameField.setText(selectedResearcher.getFullName());
	}

	// Add researcher tab
	@FXML
	private TextField newResearcherFullNameField;
	@FXML
	private TextField newResearcherLoginField;
	@FXML
	private TextField newResearcherPasswordField;
	
	@FXML
	void onAddResearcher(ActionEvent event) {
		String fullName = newResearcherFullNameField.getText();
		String login = newResearcherLoginField.getText();
		String password = newResearcherPasswordField.getText();

		if (fullName.isEmpty() || login.isEmpty() || password.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify all researcher's details.");
			return;
		}

		try {
			Researcher.addResearcher(fullName, login, password);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error adding researcher.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Researcher added.");
	}
	
	@FXML
	void onResetAddResearcher(ActionEvent event) {
		newResearcherFullNameField.clear();
		newResearcherLoginField.clear();
		newResearcherPasswordField.clear();
	}

	// Requests Tab
	@FXML
	private TableView<ResearcherRequest> requestTable;
	@FXML
	private TableColumn<ResearcherRequest, Integer> requestIDColumn;
	@FXML
	private TableColumn<ResearcherRequest, String> requestExperimentColumn;
	@FXML
	private TableColumn<ResearcherRequest, String> requestSupervisorColumn;
	@FXML
	private VBox requestPane;
	@FXML
	private Label requestIDLabel;
	@FXML
	private Label requestFieldLabel;
	@FXML
	private TextField requestResearcherField;
	@FXML
	private TextArea requestDetailsField;

	private List<ResearcherRequest> requests;
	private ResearcherRequest selectedRequest;

	private void setupRequestTable() {
		requestTable.getItems().clear();
		requestTable.getItems().addAll(requests);
		requestTable.refresh();
		setupSelectedReqeust(null);
	}

	private void setupSelectedReqeust(ResearcherRequest selectedRequest) {
		this.selectedRequest = selectedRequest;
		if (selectedRequest == null) {
			requestPane.setDisable(true);
			requestIDLabel.setText("-");
			requestFieldLabel.setText("-");
			requestResearcherField.clear();
			requestDetailsField.clear();
		} else {
			requestPane.setDisable(false);
			requestIDLabel.setText(String.valueOf(selectedRequest.getRequestID()));
			requestFieldLabel.setText(selectedRequest.getFieldDesignation());
			requestResearcherField.clear();
			requestDetailsField.setText(selectedRequest.getDetails());
		}
	}

	@FXML
	void onApproveRequest(ActionEvent event) {
		String researcherIDString = requestResearcherField.getText();
		String details = requestDetailsField.getText();
		Field field = new Field(selectedRequest.getFieldDesignation());

		if (researcherIDString.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a researcher to assign.");
			return;
		}

		// Researcher verification
		int researcherID;
		Researcher researcher;
		try {
			researcherID = Integer.parseInt(researcherIDString);
			researcher = new Researcher(researcherID);

			if (researcher.getExperimentID() != 0) {
				setStatus(Color.PURPLE, "❌ Researcher already assigned. Please specify a free researcher.");
				return;
			}

			if (!researcher.getSpecialties().contains(field)) {
				setStatus(Color.PURPLE, "❌ Researcher not qualified. Please specify a researcher having the requested specialty.");
				return;
			}
		} catch (NumberFormatException e) {
			setStatus(Color.PURPLE, "❌ Please specify a valid researcher ID.");
			return;
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error verifying researcher ID.", e);
			return;
		} catch (NoMatchException e) {
			setStatus(Color.PURPLE, "❌ Researcher doesn't exist. Please specify a valid researcher ID.");
			return;
		}

		// Approval
		try {
			selectedRequest.approveRequest(researcherID, details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error approving experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Request approved.");
	}

	@FXML
	void onDenyRequest(ActionEvent event) {
		String details = requestDetailsField.getText();
		
		try {
			selectedRequest.denyRequest(details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error denying experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Experiment denied.");
	}

	@FXML
	void onResetRequest(ActionEvent event) {
		statusLabel.setText("");
		requestResearcherField.clear();
		requestDetailsField.setText(selectedRequest.getDetails());
	}

}
