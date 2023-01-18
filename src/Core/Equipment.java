package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Equipment {
	private int equipmentID;
	private String type;
	private int experimentID;

	// Contructors
	public Equipment(int equipmentID) throws SQLException, NoMatchException {
		this.equipmentID = equipmentID;

		String query = """
					SELECT * FROM equipment WHERE equipment_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, equipmentID);

		if (result.next()) {
			type = result.getString("equipment_designation");
			experimentID = result.getInt("experiment_id");
		} else {
			throw new NoMatchException();
		}
	}

	public Equipment(int equipmentID, String type, int experimentID) {
		this.equipmentID = equipmentID;
		this.type = type;
		this.experimentID = experimentID;
	}
	
	// Class methods
	public String getExperimentText() {
		if (experimentID == 0) {
			return "None";
		}

		Experiment experiment;
		try {
			experiment = new Experiment(experimentID);
			return experiment.getCodeName() + " (ID: " + experimentID + ")";
		} catch (SQLException e) {
			return "Error fetching experiment.";
		}
	}

	// Static methods
	public static void addEquipment(String type) throws SQLException {
		String query = """
				SELECT * FROM equipment_type WHERE equipment_designation = ?;
				""";
		ResultSet result = DBManager.executeQuery(query, type);
	
		if (!result.next()) {
			query = """
					INSERT INTO equipment_type(equipment_designation) VALUES (?);
					""";
			DBManager.executeUpdate(query, type);
		}
	
		query = """
				INSERT INTO equipment(equipment_designation) VALUES (?);
				""";
		DBManager.executeUpdate(query, type);
	}

	public static List<Equipment> fetchEquipments() throws SQLException {
		String query = """
					SELECT * FROM equipment;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Equipment> equipments = new ArrayList<>();
	
		while (result.next()) {
			Equipment equipment = new Equipment(
					result.getInt("equipment_id"),
					result.getString("equipment_designation"),
					result.getInt("experiment_id"));
			equipments.add(equipment);
		}
	
		return equipments;
	}
	
	// Getters & Setters
	public int getEquipmentID() {
		return equipmentID;
	}

	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}

}
