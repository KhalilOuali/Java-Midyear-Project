package Core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Material {
	private String type;
	private double stockedQuantity;

	// Constructors
	public Material(String type, double stockedQuantity) {
		this.type = type;
		this.stockedQuantity = stockedQuantity;
	}

	public Material(String type) throws SQLException, NoMatchException {
		this.type = type;

		String query = """
				SELECT stocked_quantity FROM material_type WHERE material_designation = ?;
				""";

		ResultSet result = DBManager.executeQuery(query, type);

		if (result.next()) {
			stockedQuantity = result.getDouble("stocked_quantity");
		} else {
			throw new NoMatchException();
		}
	}

	// Static methods
	public static void addMaterial(String type, double quantity) throws SQLException {
		String query = """
				SELECT * FROM material_type WHERE material_designation = ?;
				""";
		ResultSet result = DBManager.executeQuery(query, type);
	
		if (!result.next()) {
			query = """
					INSERT INTO material_type VALUES (?, 0);
					""";
			DBManager.executeUpdate(query, type);
		}
	
		query = """
				UPDATE material_type 
				SET stocked_quantity = stocked_quantity + ? 
				WHERE material_designation = ?;
				""";
		DBManager.executeUpdate(query, quantity, type);
	}

	public static List<Material> fetchMaterials() throws SQLException {
		String query = """
					SELECT * FROM material_type;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Material> materials = new ArrayList<>();
	
		while (result.next()) {
			Material material = new Material(
					result.getString("material_designation"),
					result.getDouble("stocked_quantity"));
			materials.add(material);
		}
	
		return materials;
	}

	// Misc
	@Override
	public boolean equals(Object obj) {
		return type.equals(((Material)obj).type);
	}

	// Getters & Setters
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public double getStockedQuantity() {
		return stockedQuantity;
	}

	public void setStockedQuantity(double stockedQuantity) {
		this.stockedQuantity = stockedQuantity;
	}

}
