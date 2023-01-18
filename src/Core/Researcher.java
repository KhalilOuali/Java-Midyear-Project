package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Researcher extends Personnel {
	private int experimentID;
	private List<Field> specialties = new ArrayList<>();

	// Constructors
	public Researcher(int personnelID, String fullName, int experimentID) {
		super(personnelID, fullName);
		this.experimentID = experimentID;
	}

	public Researcher(int personnelID) throws SQLException, NoMatchException {
		super(personnelID);

		String query = """
					SELECT experiment_id FROM researcher WHERE researcher_id = ?;
				""";
		ResultSet result = DBManager.executeQuery(query, personnelID);
		if (result.next()) {
			experimentID = result.getInt("experiment_id");
		} else {
			throw new NoMatchException();
		}

		query = """
				SELECT field_designation FROM specialty WHERE researcher_id = ?;
				""";
		result = DBManager.executeQuery(query, personnelID);
		while (result.next()) {
			specialties.add(new Field(result.getString("field_designation")));
		}
	}

	// Class Methods
	public void suggestExperiment(String codeName, String description) throws SQLException {
		String query = """
					INSERT INTO experiment(researcher_id, state, code_name, description)
					VALUES (?, 'Suggested', ?, ?);
				""";

		DBManager.executeUpdate(query, personnelID, codeName, description);
	}

	public void updateSpecialties() throws SQLException {
		String query = """
				SELECT field_designation FROM specialty WHERE researcher_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, personnelID);

		while (result.next()) {
			specialties.add(new Field(result.getString("field_designation")));
		}
	}

	public String getExperimentText() {
		String query = """
				SELECT code_name FROM experiment WHERE experiment_id = ?;
				""";

		ResultSet result;
		try {
			result = DBManager.executeQuery(query, experimentID);
			if (result.next()) {
				String codeName = result.getString("code_name");
				return codeName + " (ID: " + experimentID + ")";
			} else {
				return "None";
			}
		} catch (SQLException e) {
			return "Error fetching experiment.";
		}
	}

	public void changeResearcherName(String newName) throws SQLException {
		if (newName.equals(fullName)) {
			return;
		}
	
		String query = """
				UPDATE personnel SET full_name = ? WHERE personnel_id = ?;
				""";
	
		DBManager.executeUpdate(query, newName, personnelID);
	
		this.fullName = newName;
	}

	public void addSpecialty(Field field) throws SQLException {
		if (specialties.contains(field)) {
			return;
		}
		String query;
	
		if (!Field.fetchFields().contains(field)) {
			query = """
					INSERT INTO field VALUES (?)
					""";
			DBManager.executeUpdate(query, field.getDesignation());
		}
	
		query = """
				INSERT INTO specialty VALUES (?, ?)
				""";
		DBManager.executeUpdate(query, personnelID, field.getDesignation());
		
		specialties.add(field);
	}

	// Static methods
	public static void addResearcher(String fullName, String login, String password) throws SQLException {
		String query = """
				INSERT INTO personnel(full_name, role, login, password)
				VALUES (?, 'Researcher', ?, ?);
				""";
		DBManager.executeUpdate(query, fullName, login, password);
		
		query = """
				INSERT INTO researcher(researcher_id)
				VALUES (LAST_INSERT_ID());
				""";
		DBManager.executeUpdate(query);
	}

	public static List<Researcher> fetchResearchers() throws SQLException {
		String query = """
					SELECT researcher_id, full_name, experiment_id
					FROM personnel
					INNER JOIN researcher
					ON researcher_id = personnel_id;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Researcher> researchers = new ArrayList<>();
	
		while (result.next()) {
			Researcher researcher = new Researcher(
					result.getInt("researcher_id"),
					result.getString("full_name"),
					result.getInt("experiment_id"));
			researcher.updateSpecialties();
			researchers.add(researcher);
		}
	
		return researchers;
	}

	// Getters & Setters
	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}
	
	public List<Field> getSpecialties() {
		return specialties;
	}

	public void setSpecialties(List<Field> specialties) {
		this.specialties = specialties;
	}

}
