package Core;

import java.sql.*;

public abstract class Request {
	public static enum State {
		Pending, Approved, Denied
	}

	private int requestID;
	private State state;
	private int experimentID;
	private String details;

	// Contructors
	public Request(int requestID, State state, int experimentID, String details) {
		this.requestID = requestID;
		this.state = state;
		this.experimentID = experimentID;
		this.details = details;
	}

	public Request(int requestID) throws SQLException {
		this.requestID = requestID;

		String query = """
					SELECT * FROM request WHERE request_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, requestID);

		if (result.next()) {
			experimentID = result.getInt("experiment_id");
			state = State.valueOf(result.getString("state"));
		}
	}

	// Class methods
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

	public String getSupervisorText() {
		String query = """
				SELECT full_name, researcher_id 
				FROM personnel
				INNER JOIN experiment 
				ON personnel_id = researcher_id 
				WHERE experiment_id = ?;
				""";

		ResultSet result;
		try {
			result = DBManager.executeQuery(query, experimentID);
			if (result.next()) {
				int researcherID = result.getInt("researcher_id");
				String fullName = result.getString("full_name");
				return fullName + " (ID: " + researcherID + ")";
			} else {
				return "None";
			}
		} catch (SQLException e) {
			return "Error fetching supervisor.";
		}
	}

	public void approveRequest(String details) throws SQLException {
		String query = """
					UPDATE request
					SET state = 'Approved', details = ?
					WHERE request_id = ?;
				""";

		DBManager.executeUpdate(query, details, requestID);
	}

	public void denyRequest(String details) throws SQLException {
		String query = """
					UPDATE request
					SET state = 'Denied', details = ?
					WHERE request_id = ?;
				""";
	
		DBManager.executeUpdate(query, details, this.requestID);
	}

	// Getters & Setters
	public int getRequestID() {
		return requestID;
	}

	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getExperimentID() {
		return experimentID;
	}

	public void setExperimentID(int experimentID) {
		this.experimentID = experimentID;
	}
	
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

}
