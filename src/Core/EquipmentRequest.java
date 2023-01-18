package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentRequest extends Request {
	private String type;

	// Constructors
	public EquipmentRequest(int requestID, State state, int experimentID, String details, String type) {
		super(requestID, state, experimentID, details);
		this.type = type;
	}

	public EquipmentRequest(int requestID) throws SQLException {
		super(requestID);

		String query = """
					SELECT equipment_designation FROM request WHERE request_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, requestID);

		if (result.next()) {
			type = result.getString("equipment_designation");
		}
	}

	// Class methods
	public void approveRequest(int equipmentID, String details) throws SQLException {
		super.approveRequest(details);

		String query = "UPDATE equipment SET experiment_id = ? WHERE equipment_id = ?;";
		DBManager.executeUpdate(query, getExperimentID(), equipmentID);
	}

	// Static methods
	public static List<EquipmentRequest> fetchEquipmentRequests(Request.State state) throws SQLException {
		String query = """
					SELECT * FROM request WHERE type = 'Equipment' AND state = ?;
				""";
	
		ResultSet result = DBManager.executeQuery(query, String.valueOf(state));
		List<EquipmentRequest> requests = new ArrayList<>();
	
		while (result.next()) {
			EquipmentRequest request = new EquipmentRequest(
				result.getInt("request_id"), 
				Request.State.valueOf(result.getString("state")), 
				result.getInt("experiment_id"), 
				result.getString("details"), 
				result.getString("equipment_designation"));
			requests.add(request);
		}
	
		return requests;
	}

	// Getters & Setters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
