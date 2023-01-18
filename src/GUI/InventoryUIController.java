package GUI;

import java.sql.SQLException;
import java.util.List;

import Core.Material;
import Core.MaterialRequest;
import Core.NoMatchException;
import Core.Personnel;
import Core.Equipment;
import Core.EquipmentRequest;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class InventoryUIController extends UIController {
	// General
	@FXML
	private Label welcomeLabel;
	@FXML
	private Label idLabel;

	private Personnel inventoryManager;

	@FXML
	private void initialize() {
		// equipments
		equipmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("equipmentID"));
		equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		equipmentExperimentColumn.setCellValueFactory(new PropertyValueFactory<>("experimentText"));
		
		equipmentRequestIDColumn.setCellValueFactory(new PropertyValueFactory<>("RequestID"));
		equipmentRequestExperimentColumn.setCellValueFactory(new PropertyValueFactory<>("experimentText"));
		equipmentRequestSupervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisorText"));

		equipmentRequestTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EquipmentRequest>() {
			@Override
			public void changed(ObservableValue<? extends EquipmentRequest> observable, EquipmentRequest oldValue, EquipmentRequest newValue) {
				setupSelectedEquipmentRequest(newValue);
			}
		});
		
		// materials
		materialTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
		materialQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("stockedQuantity"));

		materialRequestIDColumn.setCellValueFactory(new PropertyValueFactory<>("RequestID"));
		materialRequestExperimentColumn.setCellValueFactory(new PropertyValueFactory<>("experimentText"));
		materialRequestSupervisorColumn.setCellValueFactory(new PropertyValueFactory<>("supervisorText"));

		materialRequestTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MaterialRequest>() {
			@Override
			public void changed(ObservableValue<? extends MaterialRequest> observable, MaterialRequest oldValue, MaterialRequest newValue) {
				setupSelectedMaterialRequest(newValue);
			}
		});
	}

	@Override
	public void setup(int id) {
		statusLabel.setText("");
		
		try {
			inventoryManager = new Personnel(id);
		} catch (SQLException e) {
			inventoryManager = null;
			setStatus(Color.RED, "DB Error fetching inventory manager.", e);
			return;
		}

		welcomeLabel.setText("Welcome, " + inventoryManager.getFullName() + "!");
		idLabel.setText("Inventory Manager ID: " + inventoryManager.getPersonnelID());

		onRefresh(null);
	}

	@FXML
	void onRefresh(ActionEvent event) {
		try {
			equipmentRequests = EquipmentRequest.fetchEquipmentRequests(EquipmentRequest.State.Pending);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching equipmentRequests.", e);
		}

		try {
			materialRequests = MaterialRequest.fetchMaterialRequests(EquipmentRequest.State.Pending);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching equipmentRequests.", e);
		}

		setupEquipmentTable();
		setupEquipmentRequestTable();
		setupMaterialTable();
		setupMaterialRequestTable();
	}

	@FXML
	void onSignOut(ActionEvent event) {
		App.setScreen(AppScreen.LOGINSCREEN, 0);
	}

	// Equipments tab
	@FXML
	private TableView<Equipment> equipmentTable;
	@FXML
	private TableColumn<Equipment, Integer> equipmentIDColumn;
	@FXML
	private TableColumn<Equipment, String> equipmentTypeColumn;
	@FXML
	private TableColumn<Equipment, String> equipmentExperimentColumn;
	@FXML
	private TextArea equipmentTypeField;

	private void setupEquipmentTable() {
		equipmentTable.getItems().clear();

		List<Equipment> equipments;
		try {
			equipments = Equipment.fetchEquipments();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching equipments.", e);
			return;
		}

		equipmentTable.getItems().addAll(equipments);
	}

	@FXML
	void onAddEquipment(ActionEvent event) {
		String equipmentType = equipmentTypeField.getText();
		
		if (equipmentType.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a Type.");
			return;
		}

		try {
			Equipment.addEquipment(equipmentType);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error adding equipment.", e);
			return;
		}
		
		onRefresh(null);
		setStatus(Color.GREEN, "✔ Equipment added.");
	}

	@FXML
	void onResetEquipment(ActionEvent event) {
		statusLabel.setText("");
		equipmentTypeField.setText("");
	}

	// EquipmentRequests Tab
	@FXML
	private TableView<EquipmentRequest> equipmentRequestTable;
	@FXML
	private TableColumn<EquipmentRequest, Integer> equipmentRequestIDColumn;
	@FXML
	private TableColumn<EquipmentRequest, String> equipmentRequestExperimentColumn;
	@FXML
	private TableColumn<EquipmentRequest, String> equipmentRequestSupervisorColumn;
	@FXML
	private VBox equipmentRequestPane;
	@FXML
	private Label equipmentRequestIDLabel;
	@FXML
	private Label equipmentRequestTypeLabel;
	@FXML
	private TextField equipmentRequestTypeField;
	@FXML
	private TextArea equipmentRequestDetailsField;

	private List<EquipmentRequest> equipmentRequests;
	private EquipmentRequest selectedEquipmentRequest;

	private void setupEquipmentRequestTable() {
		equipmentRequestTable.getItems().clear();
		equipmentRequestTable.getItems().addAll(equipmentRequests);
		equipmentRequestTable.refresh();
		setupSelectedEquipmentRequest(null);
	}

	private void setupSelectedEquipmentRequest(EquipmentRequest selectedEquipmentRequest) {
		this.selectedEquipmentRequest = selectedEquipmentRequest;
		if (selectedEquipmentRequest == null) {
			equipmentRequestPane.setDisable(true);
			equipmentRequestIDLabel.setText("-");
			equipmentRequestTypeLabel.setText("-");
			equipmentRequestTypeField.clear();
			equipmentRequestDetailsField.clear();
		} else {
			equipmentRequestPane.setDisable(false);
			equipmentRequestIDLabel.setText(String.valueOf(selectedEquipmentRequest.getRequestID()));
			equipmentRequestTypeLabel.setText(selectedEquipmentRequest.getType());
			equipmentRequestTypeField.clear();
			equipmentRequestDetailsField.setText(selectedEquipmentRequest.getDetails());
		}
	}

	@FXML
	void onApproveEquipmentRequest(ActionEvent event) {
		String equipmentIDString = equipmentRequestTypeField.getText();
		String details = equipmentRequestDetailsField.getText();
		String type = selectedEquipmentRequest.getType();

		if (equipmentIDString.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a equipment to assign.");
			return;
		}

		// Equipment verification
		int equipmentID;
		Equipment equipment;
		try {
			equipmentID = Integer.parseInt(equipmentIDString);
			equipment = new Equipment(equipmentID);
			
			if (equipment.getExperimentID() != 0) {
				setStatus(Color.PURPLE, "❌ Equipment already assigned. Please specify unassigned equipment.");
				return;
			}

			if (!equipment.getType().equals(type)) {
				setStatus(Color.PURPLE, "❌ Incorrect type. Please specify a equipment having the requested type.");
				return;
			}
		} catch (NumberFormatException e) {
			setStatus(Color.PURPLE, "❌ Please specify a valid equipment ID.");
			return;
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error verifying equipment ID.", e);
			return;
		} catch (NoMatchException e) {
			setStatus(Color.PURPLE, "❌ Equipment doesn't exist. Please specify a valid equipment ID.");
			return;
		}
		
		// Approval
		try {
			selectedEquipmentRequest.approveRequest(equipmentID, details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error approving experiment.", e);
			return;
		}
		
		onRefresh(null);
		setStatus(Color.GREEN, "✔ Equipment request approved.");
	}

	@FXML
	void onDenyEquipmentRequest(ActionEvent event) {
		String details = equipmentRequestDetailsField.getText();

		try {
			selectedEquipmentRequest.denyRequest(details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error denying experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Equipment request denied.");
	}

	@FXML
	void onResetEquipmentRequest(ActionEvent event) {
		statusLabel.setText("");
		equipmentRequestTypeField.clear();
		equipmentRequestDetailsField.setText(selectedEquipmentRequest.getDetails());
	}

	// Materials tab
	@FXML
	private TableView<Material> materialTable;
	@FXML
	private TableColumn<Material, String> materialTypeColumn;
	@FXML
	private TableColumn<Material, Double> materialQuantityColumn;
	@FXML
	private TextField materialQuantityField;
	@FXML
	private TextArea materialTypeField;

	private void setupMaterialTable() {
		materialTable.getItems().clear();

		List<Material> materials;
		try {
			materials = Material.fetchMaterials();
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error fetching materials.", e);
			return;
		}

		materialTable.getItems().addAll(materials);
	}

	@FXML
	void onAddMaterial(ActionEvent event) {
		String materialType = materialTypeField.getText();
		String materialQuantity = materialQuantityField.getText();
		
		if (materialType.isEmpty() || materialQuantity.isEmpty()) {
			setStatus(Color.PURPLE, "❌ Please specify a type and a quantity.");
			return;
		}

		double quantity;
		try {
			quantity = Double.parseDouble(materialQuantity);
			if (quantity <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			setStatus(Color.PURPLE, "❌ Please specify a valid quantity.");
			return;
		}

		try {
			Material.addMaterial(materialType, quantity);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error adding material.", e);
			return;
		}
		
		onRefresh(null);
		setStatus(Color.GREEN, "✔ Material added.");
	}

	@FXML
	void onResetMaterial(ActionEvent event) {
		statusLabel.setText("");
		materialTypeField.setText("");
		materialQuantityField.setText("");
	}

	// MaterialRequests Tab
	@FXML
	private TableView<MaterialRequest> materialRequestTable;
	@FXML
	private TableColumn<MaterialRequest, Integer> materialRequestIDColumn;
	@FXML
	private TableColumn<MaterialRequest, String> materialRequestExperimentColumn;
	@FXML
	private TableColumn<MaterialRequest, String> materialRequestSupervisorColumn;
	@FXML
	private VBox materialRequestPane;
	@FXML
	private Label materialRequestIDLabel;
	@FXML
	private Label materialRequestTypeLabel;
	@FXML
	private Label materialRequestQuantityLabel;
	@FXML
	private TextArea materialRequestDetailsField;

	private List<MaterialRequest> materialRequests;
	private MaterialRequest selectedMaterialRequest;

	private void setupMaterialRequestTable() {
		materialRequestTable.getItems().clear();
		materialRequestTable.getItems().addAll(materialRequests);
		materialRequestTable.refresh();
		setupSelectedMaterialRequest(null);
	}

	private void setupSelectedMaterialRequest(MaterialRequest selectedMaterialRequest) {
		this.selectedMaterialRequest = selectedMaterialRequest;
		if (selectedMaterialRequest == null) {
			materialRequestPane.setDisable(true);
			materialRequestIDLabel.setText("-");
			materialRequestTypeLabel.setText("-");
			materialRequestQuantityLabel.setText("-");
			materialRequestDetailsField.clear();
		} else {
			materialRequestPane.setDisable(false);
			materialRequestIDLabel.setText(String.valueOf(selectedMaterialRequest.getRequestID()));
			materialRequestTypeLabel.setText(selectedMaterialRequest.getType());
			materialRequestDetailsField.setText(selectedMaterialRequest.getDetails());
			materialRequestQuantityLabel.setText(String.valueOf(selectedMaterialRequest.getQuantity()));
		}
	}

	@FXML
	void onApproveMaterialRequest(ActionEvent event) {
		String details = materialRequestDetailsField.getText();
		
		// Material verification
		try {
			Material material = new Material(selectedMaterialRequest.getType());

			if (material.getStockedQuantity() < selectedMaterialRequest.getQuantity()) {
				setStatus(Color.PURPLE, "❌ Not enough material to approve request.");
				return;
			}
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error verifying material quantity.", e);
			return;
		} catch (NoMatchException e) {
			setStatus(Color.PURPLE, "❌ Material doesn't exist. Please specify a valid material type.");
			return;
		}
		
		// Approval
		try {
			selectedMaterialRequest.approveMaterialRequest(details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error approving experiment.", e);
			return;
		}
		
		onRefresh(null);
		setStatus(Color.GREEN, "✔ Material request approved.");
	}

	@FXML
	void onDenyMaterialRequest(ActionEvent event) {
		String details = materialRequestDetailsField.getText();

		try {
			selectedMaterialRequest.denyRequest(details);
		} catch (SQLException e) {
			setStatus(Color.RED, "DB Error denying experiment.", e);
			return;
		}

		onRefresh(null);
		setStatus(Color.GREEN, "✔ Material request denied.");
	}

	@FXML
	void onResetMaterialRequest(ActionEvent event) {
		statusLabel.setText("");
		materialRequestDetailsField.setText(selectedMaterialRequest.getDetails());
	}

}
