package b_jHu.passwordManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

@SuppressWarnings("serial")
public class MainGUI extends JFrame {

	private static final String[] MENU_OPTIONS = new String[]{"Social media", "Gaming", "Office suites", "Online shopping", "WiFi passwords", "Other"};

	private static final Color ACCENT_COLOR = new Color(152, 34, 34); 
	private static final Color ACCENT_COLOR_DARKER = new Color(122, 27, 27); 
	private static final Color MENU_COLOR = new Color(52, 60, 63);
	private static final Color RENDER_PANE_COLOR = new Color(235, 235, 235);
	
	private static final String SETTINGS_FILE_NAME = "config.xml";
	private static final String DB_FILE_NAME = "passwords.db";
	private static final String SUBDIRECTORY_NAME = "/files/";
	
	private static final String APPLICATION_NAME = "PassSafe";
	private static final String VERSION_NUMBER = "v1.1.1";
	
	private static DBInteractor dbInteractor;
	private static AESWrapper aesWrap;
	private XMLFileHandler xmlHandler;
	private DialogWrapper dialogWrapper;
	
	private static String encryptionKey;
	private static String basePath;
	
	private JPanel renderPane;

	public MainGUI() {
		aesWrap = new AESWrapper();
		xmlHandler = new XMLFileHandler();
		dialogWrapper = new DialogWrapper();
		
		//checking master password
		DialogWrapper.MasterPasswordInputPane masterPassPane = dialogWrapper.new MasterPasswordInputPane();
		while (!compareWithMasterPassword(new String(masterPassPane.getInput()))) {
			masterPassPane.deletePasswordInputText();
			int result = JOptionPane.showConfirmDialog(null, masterPassPane, "Input master password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (result == JOptionPane.CANCEL_OPTION) {
				System.exit(0);
				return;
			}
		}
		
		//after correct password is input, decrypt the database
		encryptionKey = sha("SHA-256", new String(masterPassPane.getInput()));
		aesWrap.decryptFile(encryptionKey, basePath + SUBDIRECTORY_NAME + DB_FILE_NAME + ".enc", basePath + SUBDIRECTORY_NAME + DB_FILE_NAME);
		
		//after that, build the main app
		dbInteractor = new DBInteractor();
		
		JPanel contentPane = new JPanel();
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(APPLICATION_NAME + " " + VERSION_NUMBER);
		Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(200, 200, (int)(screenDimensions.getWidth() - 400), (int)(screenDimensions.getHeight() - 400));
		setMinimumSize(new Dimension(600,450));
		setContentPane(contentPane);
		contentPane.setBackground(RENDER_PANE_COLOR);
		
		GridBagLayout gblContentPane = new GridBagLayout();
		GridBagConstraints gbcContentPane = new GridBagConstraints();
		gbcContentPane.fill = GridBagConstraints.BOTH;
		contentPane.setLayout(gblContentPane);
		
		//-------------
		//create header bar
		GridBagLayout gblHeaderBar = new GridBagLayout();
		GridBagConstraints gbcHeaderBar = new GridBagConstraints();
		gbcHeaderBar.gridy = 0;
		
		JPanel headerBar = new JPanel(gblHeaderBar);   
		headerBar.setBackground(ACCENT_COLOR);
		
		JLabel headerText = new JLabel(APPLICATION_NAME);
		headerText.setForeground(Color.WHITE);
		headerText.setOpaque(false);
		headerText.setFont(new Font("Calibri", Font.BOLD, 36));
		gbcHeaderBar.gridx = 0;
		gbcHeaderBar.insets = new Insets(5,5,5,0);
		headerBar.add(headerText, gbcHeaderBar);
		
		
		JLabel spacerHeaderBar = new JLabel();
		gbcHeaderBar.gridx = 1;
		gbcHeaderBar.weightx = 1.0;
		gbcHeaderBar.insets = new Insets(0,0,0,0);
		headerBar.add(spacerHeaderBar, gbcHeaderBar);
		
		JTextField searchField = new JTextField(20);
		gbcHeaderBar.gridx = 2;
		gbcHeaderBar.weightx = 0.0;
		headerBar.add(searchField, gbcHeaderBar);
		
		
		JLabel searchButton = new JLabel("Search");
		searchButton.setForeground(Color.WHITE);
		searchButton.setBackground(ACCENT_COLOR);
		searchButton.setFont(new Font("Calibri", Font.PLAIN, 18));
		searchButton.setOpaque(true);
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_ENTRY, searchField.getText()));
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				searchButton.setBackground(ACCENT_COLOR_DARKER);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				searchButton.setBackground(ACCENT_COLOR);
			}
		});
		gbcHeaderBar.gridx = 3;
		gbcHeaderBar.insets = new Insets(0,10,0,5);
		headerBar.add(searchButton, gbcHeaderBar);
		
		//add to content pane
		gbcContentPane.gridy = 0;
		gbcContentPane.gridx = 0;
		gbcContentPane.gridwidth = 2;
		gbcContentPane.anchor = GridBagConstraints.FIRST_LINE_START;
		contentPane.add(headerBar, gbcContentPane);
		
		//--------------
		//create sidebar-menu
		GridBagLayout gblSideMenu = new GridBagLayout();
		GridBagConstraints gbcSideMenu = new GridBagConstraints();
		gbcSideMenu.fill = GridBagConstraints.BOTH;
		gbcSideMenu.gridwidth = GridBagConstraints.REMAINDER;
		
		JPanel sideMenu = new JPanel(gblSideMenu);
		sideMenu.setBackground(MENU_COLOR);	
		
		//show all as standard option
		SidebarMenuItem showAll = new SidebarMenuItem("All");
		showAll.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_ALL, ""));
			}
		});
		sideMenu.add(showAll, gbcSideMenu);
		sideMenu.add(Box.createVerticalStrut(5), gbcSideMenu);
		sideMenu.add(new JSeparator(), gbcSideMenu);
		sideMenu.add(Box.createVerticalStrut(7), gbcSideMenu);
		
		
		for (String entry : MENU_OPTIONS) {
			SidebarMenuItem menuItem = new SidebarMenuItem(entry);
			menuItem.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_CATEGORY, entry));
				}
			});
			sideMenu.add(menuItem, gbcSideMenu);
		}
		
		JLabel spacerSideMenu = new JLabel();
		gbcSideMenu.weighty = 1.0;
		sideMenu.add(spacerSideMenu, gbcSideMenu);
		
		//add to content pane
		gbcContentPane.gridy = 1;
		gbcContentPane.gridx = 0;
		gbcContentPane.gridwidth = 1;
		gbcContentPane.gridheight = 2;
		gbcContentPane.anchor = GridBagConstraints.LINE_START;
		contentPane.add(sideMenu, gbcContentPane);
		gbcContentPane.gridheight = 1;
		
		//------------
		//create rendering-pane
		renderPane = new JPanel(new FlowLayout());
		renderPane.setBackground(RENDER_PANE_COLOR);
		renderPane.setPreferredSize(new Dimension(200, (int)((dbInteractor.returnDatabaseEntries() / 3) * 127)));
		
		JScrollPane renderPaneScrollable = new JScrollPane(renderPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		renderPaneScrollable.setBorder(null);
				
		gbcContentPane.gridy = 1;
		gbcContentPane.gridx = 1;
		gbcContentPane.weightx = 0.9;
		gbcContentPane.weighty = 0.9;
		contentPane.add(renderPaneScrollable, gbcContentPane);
		
		//-----------
		//'+'-button
		JLabel addEntryLbl = new JLabel("+");
		addEntryLbl.setForeground(Color.WHITE);
		addEntryLbl.setBackground(ACCENT_COLOR);
		addEntryLbl.setFont(new Font("Calibri", Font.PLAIN, 36));
		addEntryLbl.setVerticalAlignment(SwingConstants.TOP);
		addEntryLbl.setHorizontalAlignment(SwingConstants.CENTER);
		addEntryLbl.setMinimumSize(new Dimension(40, 40));
		addEntryLbl.setPreferredSize(new Dimension(40, 40));
		addEntryLbl.setMaximumSize(new Dimension(40, 40));
		addEntryLbl.setOpaque(true);
		
		addEntryLbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				addEntryLbl.setBackground(ACCENT_COLOR_DARKER);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				addEntryLbl.setBackground(ACCENT_COLOR);
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				DialogWrapper.AddEntryDialogPane entryPane = dialogWrapper.new AddEntryDialogPane(MENU_OPTIONS);
				int result = JOptionPane.showConfirmDialog(null, entryPane, "Add entry", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
				if (result == JOptionPane.OK_OPTION) dbInteractor.addEntry(entryPane.getInput());

				renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_ALL, ""));
			}
		});
		
		JPanel dummyPane = new JPanel(); 
		dummyPane.add(addEntryLbl); //using a dummy pane to fix a rendering bug; the '+'-button would resize without it
		
		//add to content pane
		gbcContentPane.gridy = 2;
		gbcContentPane.gridx = 1;
		gbcContentPane.weightx = 0.0;
		gbcContentPane.weighty = 0.0;
		gbcContentPane.fill = GridBagConstraints.NONE;
		gbcContentPane.anchor = GridBagConstraints.LINE_END;
		gbcContentPane.insets = new Insets(0,0,5,5);
		contentPane.add(dummyPane, gbcContentPane);
		
		
		renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_ALL, ""));
	} //end constructor
	
	//begin methods
	public void renderQueryResults(ResultSet resSet) {
		try {
			renderPane.removeAll();
			renderPane.add(Box.createHorizontalStrut(-20));
			while (resSet.next()) {
				DBEntryRenderer newEntry = new DBEntryRenderer(resSet.getString("serviceName"), resSet.getString("username"), resSet.getString("eMail"), resSet.getString("password"));
				newEntry.mntmDeleteEntry.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						dbInteractor.deleteEntry(newEntry.getServiceName());
						renderQueryResults(dbInteractor.queryDatabase(DBInteractor.QUERY_ALL, ""));
					}
				});
				renderPane.add(Box.createVerticalStrut(100));
				renderPane.add(newEntry);
			}
			
			renderPane.setPreferredSize(new Dimension(200, (int)((dbInteractor.returnDatabaseEntries() / 3) * 127))); //recalculate preferred size for scroll pane
			renderPane.revalidate();
			renderPane.repaint();
		} catch (SQLException sqlEx) {
			new ErrorMessage(sqlEx);
		}
	}
	
	public boolean compareWithMasterPassword(String password) {
		String xmlPassHash = xmlHandler.getValue(basePath + SUBDIRECTORY_NAME + SETTINGS_FILE_NAME, "masterPassHash");

		String passHash = sha("SHA-512", password);

		if(xmlPassHash.compareTo(passHash) != 0) return false;
		return true;

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
	//end methods
	
	
	/* ===========
	 * main
	   ===========*/
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				basePath = new File("").getAbsolutePath();

				if(!Files.exists(Paths.get(basePath + SUBDIRECTORY_NAME))) {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						new DatabaseCreator();

						MainGUI frame = new MainGUI();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

						MainGUI frame = new MainGUI();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				try {
					if (dbInteractor != null) dbInteractor.closeConnections();
					if (encryptionKey != null) aesWrap.encryptFile(encryptionKey, basePath + SUBDIRECTORY_NAME + DB_FILE_NAME, basePath + SUBDIRECTORY_NAME + DB_FILE_NAME + ".enc");
					Files.deleteIfExists(Paths.get(basePath + SUBDIRECTORY_NAME + DB_FILE_NAME));
				} catch (IOException e) {
					new ErrorMessage("Error whilst closing the application", e);
				}
			}
		}, "Shutdown-thread"));
	}

}
