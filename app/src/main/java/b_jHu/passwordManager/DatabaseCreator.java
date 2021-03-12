package b_jHu.passwordManager;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;

import javax.swing.JOptionPane;

public class DatabaseCreator {
	
	private static final String SETTINGS_FILE_NAME = "config.xml";
	private static final String DB_FILE_NAME = "passwords.db";
	private static final String SUBDIRECTORY_NAME = "/files/";
	
	private XMLFileHandler xmlHandler;
	private AESWrapper aesWrap;
	private DialogWrapper dialogWrap;
	
	public DatabaseCreator() {
		xmlHandler = new XMLFileHandler();
		aesWrap = new AESWrapper();
		dialogWrap = new DialogWrapper();
		
		DialogWrapper.CreateDatabasePane createDBPane = dialogWrap.new CreateDatabasePane();
		int result = JOptionPane.showConfirmDialog(null, createDBPane, "Create database", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION && Arrays.equals(createDBPane.getMasterPass(), createDBPane.getConfirmPass())) createPasswordDatabase(createDBPane.getMasterPass());
	}
	
	public void createPasswordDatabase(char[] masterPassword) {		
		try {
			String programPath = new File("").getAbsolutePath();
			String filePath = programPath + SUBDIRECTORY_NAME;
			
			Files.createDirectories(Paths.get(filePath));
			
			
			xmlHandler.createXMLFile(filePath + SETTINGS_FILE_NAME, "settings");
			xmlHandler.appendNode(filePath + SETTINGS_FILE_NAME, "settings", "masterPassHash", sha("SHA-512", new String(masterPassword)));
			
			Connection conn = DriverManager.getConnection("jdbc:sqlite:" + filePath + DB_FILE_NAME);
			
			String sql = "CREATE TABLE passwords(serviceName varchar(127) NOT NULL, serviceNameLowercase varchar(127) NOT NULL, category varchar(127), website varchar(255), username varchar(127), eMail varchar(255), password varchar(255) NOT NULL)";
			conn.createStatement().executeUpdate(sql);
			conn.close();

			aesWrap.encryptFile(sha("SHA-256", new String(masterPassword)), filePath + DB_FILE_NAME, filePath + DB_FILE_NAME + ".enc");
			Files.deleteIfExists(Paths.get(filePath + DB_FILE_NAME));
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		} catch (IOException e) {
			new ErrorMessage("Error interacting with the files", e);
		}
	}

	
	public String sha(String algo, String text) {
		try {
			byte[] textHashBytes = MessageDigest.getInstance(algo)
										.digest(text.getBytes("UTF-8"));

			return new BigInteger(1, textHashBytes).toString(16);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			new ErrorMessage("Error hashing some text", e);
			return null;
		}	

	}
}
