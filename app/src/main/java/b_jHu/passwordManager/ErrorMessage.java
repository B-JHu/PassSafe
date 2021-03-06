package b_jHu.passwordManager;

import java.sql.SQLException;

import javax.swing.JOptionPane;

@SuppressWarnings("serial")
public class ErrorMessage extends JOptionPane {
	
	public ErrorMessage(SQLException e) {
		showMessageDialog(null, "Error interacting with the database:\n" + 
								e.getCause() + "\n" + 
								e.getMessage() + "\n" +
								"Try again. If this error persists, restart the program!", "Error: SQL-exception", JOptionPane.ERROR_MESSAGE);
	}
	
	public ErrorMessage(String errorMessage, Exception e) {
		showMessageDialog(null, errorMessage + "\n" +
	                            e.getCause() + "\n" +
				                e.getMessage(), "Error: Runtime exception", JOptionPane.ERROR_MESSAGE);
	}
}
