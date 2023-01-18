package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResearcherRequest extends Request {
	private String fieldDesignation;

	// Constructors
	public ResearcherRequest(int requestID, State state, int experimentID, String details, String fieldDesignation) {
		super(requestID, state, experimentID, details);
		this.fieldDesignation = fieldDesignation;
	}

	public ResearcherRequest(int requestID) throws SQLException {
		super(requestID);

		String query = """
					SELECT field_designation FROM request WHERE request_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, requestID);

		if (result.next()) {
			fieldDesignation = result.getString("field_designation");
		}
	}

	// Class methods
	public void approveRequest(int researcherID, String details) throws SQLException {
		super.approveRequest(details);
	
		String query = "UPDATE researcher SET experiment_id = ? WHERE researcher_id = ?;";
		DBManager.executeUpdate(query, getExperimentID(), researcherID);
	}

	// Static methods
	public static List<ResearcherRequest> fetchRequests(Request.State state) throws SQLException {
		String query = """
					SELECT * FROM request WHERE type = 'Researcher' AND state = ?;
				""";
	
		ResultSet result = DBManager.executeQuery(query, String.valueOf(state));
		List<ResearcherRequest> requests = new ArrayList<>();
	
		while (result.next()) {
			ResearcherRequest request = new ResearcherRequest(
				result.getInt("request_id"), 
				Request.State.valueOf(result.getString("state")), 
				result.getInt("experiment_id"), 
				result.getString("details"), 
				result.getString("field_designation"));
			requests.add(request);
		}
	
		return requests;
	}

	// Getters & Setters
	public String getFieldDesignation() {
		return fieldDesignation;
	}

	public void setFieldDesignation(String fieldDesignation) {
		this.fieldDesignation = fieldDesignation;
	}

}
