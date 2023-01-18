package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Lab {
	private int labID;
	private String location;

	// Contructors
	public Lab(int ladbID) throws SQLException, NoMatchException {
		this.labID = ladbID;

		String query = """
					SELECT location FROM lab WHERE lab_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, labID);

		if (result.next()) {
			this.location = result.getString("location");
		} else {
			throw new NoMatchException();
		}
	}

	public Lab(int labID, String location) {
		this.labID = labID;
		this.location = location;
	}

	// Class methods
	public String getExperimentText() {
		String query = """
				SELECT experiment_id FROM experiment WHERE lab_id = ?;
				""";

		ResultSet result;
		try {
			result = DBManager.executeQuery(query, labID);
			if (result.next()) {
				int experimentID = result.getInt("experiment_id");
				Experiment experiment = new Experiment(experimentID);
				return experiment.getCodeName() + " (ID: " + experimentID + ")";
			} else {
				return "None";
			}
		} catch (SQLException e) {
			return "Error fetching experiment.";
		}
	}

	// Static methods
	public static void addLab(String location) throws SQLException {
		String query = """
				INSERT INTO lab(location) VALUES (?);
				""";
		DBManager.executeUpdate(query, location);
	}

	public static List<Lab> fetchLabs() throws SQLException {
		String query = """
					SELECT * FROM lab;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Lab> labs = new ArrayList<>();
	
		while (result.next()) {
			Lab lab = new Lab(
					result.getInt("lab_id"),
					result.getString("location"));
			labs.add(lab);
		}
	
		return labs;
	}
	
	// Getters & Setters
	public int getLabID() {
		return labID;
	}

	public void setLabID(int ladbID) {
		this.labID = ladbID;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
