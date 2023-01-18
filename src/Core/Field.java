package Core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Field {
	private String designation;

	// Constructors
	public Field(String designation) {
		this.designation = designation;
	}

	// Static methods
	public static List<Field> fetchFields() throws SQLException {
		String query = """
					SELECT * FROM field;
				""";
	
		ResultSet result = DBManager.executeQuery(query);
		List<Field> fields = new ArrayList<>();
	
		while (result.next()) {
			Field field = new Field(result.getString("field_designation"));
			fields.add(field);
		}
	
		return fields;
	}

	// Misc
	@Override
	public boolean equals(Object obj) {
		return designation.equals(((Field)obj).designation);
	}

	// Getters & Setters
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

}
