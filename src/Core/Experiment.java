package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Experiment {
	public static enum State {
		Suggested, Denied, Ongoing, Concluded;
	};

	private int experimentID;
	private int personnelID;
	private State state;
	private String codeName;
	private String description;
	private int labID;

	// Constructors
	public Experiment(int experimentID, int personnelID, State state, String codeName, String description, int labID) {
		this.experimentID = experimentID;
		this.personnelID = personnelID;
		this.state = state;
		this.codeName = codeName;
		this.description = description;
		this.labID = labID;
	}

	public Experiment(int experimentID) throws SQLException {
		this.experimentID = experimentID;

		String query = """
					SELECT * FROM experiment WHERE experiment_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, experimentID);

		if (result.next()) {
			personnelID = result.getInt("researcher_id");
			state = State.valueOf(result.getString("State"));
			codeName = result.getString("code_name");
			labID = result.getInt("lab_id");
			description = result.getString("description");
		}
	}
	
	// Class methods
	public List<Researcher> getResearchers() throws SQLException {
		String query = """
					SELECT researcher_id, full_name
					FROM personnel
					INNER JOIN researcher
					ON researcher_id = personnel_id
					WHERE experiment_id = ? AND researcher_id != ?;
				""";

		ResultSet result = DBManager.executeQuery(query, experimentID, personnelID);
		List<Researcher> researchers = new ArrayList<>();

		while (result.next()) {
			Researcher researcher = new Researcher(
					result.getInt("researcher_id"),
					result.getString("full_name"),
					experimentID);
			researchers.add(researcher);
		}

		return researchers;
	}

	public List<Equipment> getEquipments() throws SQLException {
		String query = """
					SELECT * FROM equipment WHERE experiment_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, experimentID);
		List<Equipment> equipments = new ArrayList<>();

		while (result.next()) {
			Equipment equipment = new Equipment(
					result.getInt("equipment_id"),
					result.getString("equipment_designation"),
					experimentID);
			equipments.add(equipment);
		}

		return equipments;
	}

	public void removeResearcher(int personnelID) throws SQLException {
		String query = """
					UPDATE researcher SET experiment_id = NULL WHERE researcher_id = ?;
				""";

		DBManager.executeUpdate(query, personnelID);
	}

	public void removeEquipment(int equipmentID) throws SQLException {
		String query = """
					UPDATE equipment SET experiment_id = NULL WHERE equipment_id = ?;
				""";

		DBManager.executeUpdate(query, equipmentID);
	}

	public boolean submitResearcherRequest(String specialty, String details) throws SQLException {
		String query = """
					SELECT * FROM fields WHERE field_designation = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, specialty);

		if (!result.next()) {
			return false;
		}

		query = """
					INSERT INTO request(type, experiment_id, field_designation, details)
					VALUES ('Researcher', ?, ?, ?);
				""";

		DBManager.executeUpdate(query, experimentID, specialty, details);
		return true;
	}

	public boolean submitMaterialRequest(String material, double quantity, String details) throws SQLException {
		String query = """
					SELECT * FROM material_type WHERE material_designation = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, material);

		if (!result.next()) {
			return false;
		}

		query = """
					INSERT INTO request(type, experiment_id, material_designation, quantity, details)
					VALUES ('Material', ?, ?, ?, ?);
				""";

		DBManager.executeUpdate(query, experimentID, material, quantity, details);
		return true;
	}

	public boolean submitEquipmentRequest(String equipment, String details) throws SQLException {
		String query = """
					SELECT * FROM equipment_type WHERE equipment_designation = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, equipment);

		if (!result.next()) {
			return false;
		}

		query = """
					INSERT INTO request(type, experiment_id, equipment_designation, details)
					VALUES ('Equipment', ?, ?, ?);
				""";

		DBManager.executeUpdate(query, experimentID, equipment, details);
		return false;
	}

	public void conclude() throws SQLException {
		String queries[] = {
				"UPDATE experiment SET state = 'Concluded' WHERE experiment_id = ?;",
				"UPDATE researcher SET experiment_id = NULL WHERE experiment_id = ?;",
				"UPDATE equipment SET experiment_id = NULL WHERE experiment_id = ?;",
				"UPDATE request SET state = FALSE WHERE experiment_id = ?;"
		};

		for (String query : queries) {
			DBManager.executeUpdate(query, experimentID);
		}

		state = State.Concluded;
	}

	public String getSupervisorText() {
		try {
			String supervisorName = (new Researcher(personnelID)).getFullName();
			return supervisorName + " (ID: " + personnelID + ")";
		} catch (SQLException | NoMatchException e) {
			return "Error fetching name.";
		}
	}

	public String getLabText() {
		if (labID == 0) {
			return "None";
		}

		try {
			return labID + " (" + (new Lab(labID)).getLocation() + ")";
		} catch (SQLException | NoMatchException e) {
			return "Error fetching location.";
		}
	}

	public void denySuggestion(String codeName, String description) throws SQLException {
		String query = """
					UPDATE experiment 
					SET state = 'Denied', code_name = ?, description = ?
					WHERE experiment_id = ?;
				""";
	
		DBManager.executeUpdate(query, codeName, description, this.experimentID);
	}

	public void approveSuggestion(String codeName, int labID, String description) throws SQLException {
		String query = """
					UPDATE experiment
					SET state = 'Ongoing', code_name = ?, lab_id = ?, description = ?
					WHERE experiment_id = ?;
				""";
		DBManager.executeUpdate(query, codeName, labID, description, this.experimentID);
	
		query = "UPDATE researcher SET experiment_id = ? WHERE researcher_id = ?;";
		DBManager.executeUpdate(query, this.experimentID, this.personnelID);
	}

	// Static methods
	public static List<Experiment> fetchExperiments() throws SQLException {
		String query = """
					SELECT * FROM experiment;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Experiment> experiments = new ArrayList<>();
	
		while (result.next()) {
			Experiment experiment = new Experiment(
					result.getInt("experiment_id"),
					result.getInt("researcher_id"),
					State.valueOf(result.getString("state")),
					result.getString("code_name"),
					result.getString("description"),
					result.getInt("lab_id"));
			experiments.add(experiment);
		}
	
		return experiments;
	}

	// Getters & Setters
	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}

	public int getPersonnelID() {
		return personnelID;
	}

	public void setResearcherID(int personnelID) {
		this.personnelID = personnelID;
	}

	public Experiment.State getState() {
		return state;
	}

	public void setState(Experiment.State state) {
		this.state = state;
	}

	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getLabID() {
		return labID;
	}

	public void setLabID(int labID) {
		this.labID = labID;
	}

}
