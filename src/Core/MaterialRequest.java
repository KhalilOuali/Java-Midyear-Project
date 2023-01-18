package Core;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MaterialRequest extends Request {
	private String type;
	private double quantity;

	// Constructors
	public MaterialRequest(int requestID, State state, int experimentID, String details, String type, double quantity) {
		super(requestID, state, experimentID, details);
		this.type = type;
		this.quantity = quantity;
	}

	public MaterialRequest(int requestID) throws SQLException {
		super(requestID);

		String query = """
					SELECT material_designation, quantity FROM request WHERE request_id = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, requestID);

		if (result.next()) {
			type = result.getString("material_designation");
			quantity = result.getDouble("quantity");
		}
	}

	public void approveMaterialRequest(String details) throws SQLException {
		super.approveRequest(details);
	
		String query = "UPDATE material_type SET stocked_quantity = stocked_quantity - ? WHERE material_designation = ?;";
		DBManager.executeUpdate(query, quantity, type);
	}

	// Static methods
	public static List<MaterialRequest> fetchMaterialRequests(Request.State state) throws SQLException {
		String query = """
					SELECT * FROM request WHERE type = 'Material' AND state = ?;
				""";
	
		ResultSet result = DBManager.executeQuery(query, String.valueOf(state));
		List<MaterialRequest> requests = new ArrayList<>();
	
		while (result.next()) {
			MaterialRequest request = new MaterialRequest(
				result.getInt("request_id"), 
				Request.State.valueOf(result.getString("state")), 
				result.getInt("experiment_id"), 
				result.getString("details"), 
				result.getString("material_designation"),
				result.getDouble("quantity"));
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

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

}
