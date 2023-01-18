package Core;

import java.sql.*;

public class Personnel {
	protected int personnelID;
	protected String fullName;

	public Personnel(int personnelID, String fullName) {
		this.personnelID = personnelID;
		this.fullName = fullName;
	}

	public Personnel(int personnelID) throws SQLException {
		this.personnelID = personnelID;

		String query = """
					SELECT full_name FROM personnel WHERE personnel_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, personnelID);

		if (result.next()) {
			fullName = result.getString("full_name");
		}
	}
	
	// Getters & Setters
	public int getPersonnelID() {
		return personnelID;
	}

	public void setPersonnelID(int personnelID) {
		this.personnelID = personnelID;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
