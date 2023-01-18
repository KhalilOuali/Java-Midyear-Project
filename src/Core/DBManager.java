package Core;
import java.sql.*;

public class DBManager {
	private static Connection connection;

	public static boolean initConnection(String url, String username, String password) {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + url, username, password);
			return true;
		} catch (SQLException e) {
			System.err.println("\n! Critical DB Error: Couldn't connect to database\n" + e);
			return false;
		}
	}

	public static boolean initConnection() {
		return initConnection("localhost:3306/research_center", "root", "asdf");
	}

	public static void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.err.println("\nDB Error: Couldn't disconnect from database\n" + e);
		}
	}

	public static ResultSet executeQuery(String query, Object... objects) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		for (int i = 0; i < objects.length; i++) {
			statement.setObject(i+1, objects[i]);
		}
		return statement.executeQuery();
	}

	public static ResultSet executeQuery(String query) throws SQLException {
		Statement statement = connection.createStatement();
		return statement.executeQuery(query);
	}

	public static int executeUpdate(String query, Object... objects) throws SQLException {
		PreparedStatement statement = connection.prepareStatement(query);
		for (int i = 0; i < objects.length; i++) {
			statement.setObject(i+1, objects[i]);
		}
		return statement.executeUpdate();
	}
	
}
