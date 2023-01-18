package GUI;
import java.sql.SQLException;
import java.util.List;

import Core.Equipment;
import Core.Experiment;
import Core.Researcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;

public class ExperimentUIController extends UIController {
	// General
	@FXML
	private Label experimentLabel;
	@FXML
	private Label idLabel;

	private Experiment experiment;

	@FXML
	private void initialize() {
		personnelIDColumn.setCellValueFactory(new PropertyValueFactory<>("personnelID"));
		researcherNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

		equipmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentID"));
		equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
	}

	@Override
	public void setup(int id) {
		try {
			experiment = new Experiment(id);
		} catch (SQLException e) {
			experiment = null;
			setStatus(Color.RED, "DB Error fetching experiment.", e);
			return;
		}

		experimentLabel.setText("Experiment: " + experiment.getCodeName());
		idLabel.setText("Experiment ID: " + experiment.getExperimentID());
		
		onResetResearcherRequest(null);
		onResetEquipmentRequest(null);
		onResetMaterialRequest(null);
		onRefresh(null);
	}

	@FXML
	void onRefresh(ActionEvent event) {
		setupDetailsPane();
		setupResearcherTable();
		setupEquipmentTable();
	}

	@FXML
	void onReturn(ActionEvent event) {
		App.setScreen(AppScreen.RESEARCHERSCREEN, experiment.getPersonnelID());
	}

	// Details tab
	@FXML
	private Label experimentCodeNameLabel;
	@FXML
	private Label experimentIDLabel;
	@FXML
	private Label supervisorLabel;
	@FXML
	private Label labLabel;
	@FXML
	private Label descriptionLabel;

	private void setupDetailsPane() {
		experimentIDLabel.setText(String.valueOf(experiment.getExperimentID()));
		experimentCodeNameLabel.setText(experiment.getCodeName());
		descriptionLabel.setText(experiment.getDescription());
		supervisorLabel.setText(experiment.getSupervisorText());
		labLabel.setText(experiment.getLabText());
	}

	@FXML
	void onConclude(ActionEvent event) {
		try {
			experiment.conclude();
			onReturn(null);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error concluding experiment.", e);
			return;
		}
	}

	// Researchers tab
	@FXML
	private TableView<Researcher> researcherTable;
	@FXML
	private TableColumn<Researcher, Integer> personnelIDColumn;
	@FXML
	private TableColumn<Researcher, String> researcherNameColumn;
	@FXML
	private TextField researcherSpecialtyField;
	@FXML
	private TextArea researcherDetailsField;

	private void setupResearcherTable() {
		researcherTable.getItems().clear();

		List<Researcher> researchers;
		try {
			researchers = experiment.getResearchers();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching researchers.", e);
			return;
		}

		researcherTable.getItems().addAll(researchers);
	}

	@FXML
	void onRemoveResearcher(ActionEvent event) {
		Researcher researcher = researcherTable.getSelectionModel().getSelectedItem();

		if (researcher == null) {
			setStatus(Color.PURPLE, "❌ Please select a researcher to remove.");
			return;
		}

		int researcher_id = researcher.getPersonnelID();

		try {
			experiment.removeResearcher(researcher_id);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error removing researcher.", e);
			return;
		}

		researcherTable.getItems().remove(researcher);
		setStatus(Color.GREEN, "✔ Researcher removed.");
	}

	@FXML
	void onSubmitResearcherRequest(ActionEvent event) {
		String specialty = researcherSpecialtyField.getText();
		if (specialty.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify requested specialty.");
			return;
		}

		String details = researcherDetailsField.getText();

		boolean submitted;
		try {
			submitted = experiment.submitResearcherRequest(specialty, details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error submitting researcher request.", e);
			return;
		}

		if (submitted) {
			setStatus(Color.GREEN, "✔ Request submitted.");
		} else {
			setStatus(Color.PURPLE, "❌ Specialty unavailable. Couldn't submit request.");
		}
	}

	@FXML
	void onResetResearcherRequest(ActionEvent event) {
		researcherSpecialtyField.clear();
		researcherDetailsField.clear();
		statusLabel.setText("");
	}

	// Equipments tab
	@FXML
	private TableView<Equipment> equipmentTable;
	@FXML
	private TableColumn<Equipment, Integer> equipmentIDColumn;
	@FXML
	private TableColumn<Equipment, String> equipmentTypeColumn;
	@FXML
	private TextField equipmentTypeField;
	@FXML
	private TextArea equipmentDetailsField;

	private void setupEquipmentTable() {
		equipmentTable.getItems().clear();

		List<Equipment> equipments;
		try {
			equipments = experiment.getEquipments();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching equipments.", e);
			return;
		}

		equipmentTable.getItems().addAll(equipments);
	}

	@FXML
	void onRemoveEquipment(ActionEvent event) {
		Equipment equipment = equipmentTable.getSelectionModel().getSelectedItem();

		if (equipment == null) {
			setStatus(Color.PURPLE, "❌ Please select a equipment to remove.");
			return;
		}

		int equipment_id = equipment.getEquipmentID();

		try {
			experiment.removeEquipment(equipment_id);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error removing equipment.", e);
			return;
		}

		equipmentTable.getItems().remove(equipment);
		setStatus(Color.GREEN, "✔ Equipment removed.");
	}

	@FXML
	void onSubmitEquipmentRequest(ActionEvent event) {
		String type = equipmentTypeField.getText();
		if (type.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify equipment type.");
			return;
		}

		String details = equipmentDetailsField.getText();

		boolean submitted;
		try {
			submitted = experiment.submitEquipmentRequest(type, details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error submitting equipment request.", e);
			return;
		}

		if (submitted) {
			setStatus(Color.GREEN, "✔ Request submitted.");
		} else {
			setStatus(Color.PURPLE, "❌ Equipment unavailable. Couldn't submit request.");
		}
	}

	@FXML
	void onResetEquipmentRequest(ActionEvent event) {
		equipmentTypeField.clear();
		equipmentDetailsField.clear();
		statusLabel.setText("");
	}

	// Materials tab
	@FXML
	private TextField materialTypeField;
	@FXML
	private TextField materialQuantityField;
	@FXML
	private TextArea materialDetailsField;

	@FXML
	void onSubmitMaterialRequest(ActionEvent event) {
		String type = materialTypeField.getText();
		String quantityString = materialQuantityField.getText();
		if (type.isEmpty() || quantityString.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify material type and quantity.");
			return;
		}

		double quantity;
		try { 
			quantity = Double.parseDouble(materialQuantityField.getText());
			if (quantity <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			setStatus(Color.PURPLE, "❌ Please enter a valid quantity.");
			return;
		}

		String details = materialDetailsField.getText();

		boolean submitted;
		try {
			submitted = experiment.submitMaterialRequest(type, quantity, details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error submitting material request.", e);
			return;
		}

		if (submitted) {
			setStatus(Color.GREEN, "✔ Request submitted.");
		} else {
			setStatus(Color.PURPLE, "❌ Material unavailable. Couldn't submit request.");
		}
	}

	@FXML
	void onResetMaterialRequest(ActionEvent event) {
		materialTypeField.clear();
		materialQuantityField.clear();
		materialDetailsField.clear();
		statusLabel.setText("");
	}

}
