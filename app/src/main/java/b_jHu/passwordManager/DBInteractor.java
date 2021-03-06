package b_jHu.passwordManager;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBInteractor {
	
	public static final int QUERY_ALL = 0;
	public static final int QUERY_CATEGORY = 1;
	public static final int QUERY_ENTRY = 2;
	
	private static final String DB_FILE_NAME = "passwords.db";
	private static final String SUBDIRECTORY_NAME = "/files/";
	
	private Connection conn;
	private Statement stmt;
	
	public DBInteractor() {
		try {
			String basePath = new File("").getAbsolutePath();
			
			conn = DriverManager.getConnection("jdbc:sqlite:" + basePath + SUBDIRECTORY_NAME + DB_FILE_NAME);
			stmt = conn.createStatement();
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		}	
	}
	
	public void addEntry(String[] entryData) {
		try {
			String entryValues = String.join("','", entryData);
			String sql = "INSERT INTO passwords(serviceName, serviceNameLowercase, category, website, username, eMail, password) VALUES ('" + entryValues + "')";
			stmt.executeUpdate(sql);
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		}
	}
	
	public void deleteEntry(String entryName) {
		try {
			String sql = "DELETE FROM passwords WHERE serviceNameLowercase='" + entryName.toLowerCase() + "'";
			stmt.executeUpdate(sql);
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		}
	}
	
	public ResultSet queryDatabase(int queryType, String query) {
		ResultSet results = null;
		
		try {
			switch (queryType) {
				case QUERY_ALL:
						String sql = "SELECT * FROM passwords ORDER BY serviceNameLowercase ASC";
						results = stmt.executeQuery(sql);
						return results;
				case QUERY_CATEGORY:
						sql = "SELECT * FROM passwords WHERE category='" + query + "' ORDER BY serviceNameLowercase ASC";
						results = stmt.executeQuery(sql);
						return results;
				case QUERY_ENTRY:
						sql = "SELECT * FROM passwords WHERE serviceNameLowercase LIKE'" + query.toLowerCase() + "%' ORDER BY serviceNameLowercase ASC";
						results = stmt.executeQuery(sql);
						return results;
				default:
					return null;
			}
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
			return results;
		}
	}
	
	public void closeConnections() {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		}
	}
}
