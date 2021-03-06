package b_jHu.passwordManager;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class DBEntryRenderer extends JPanel {
	
	private JLabel lblServiceName;
	
	public JMenuItem mntmDeleteEntry;
	
	public DBEntryRenderer(String serviceName, String username, String eMail, String password) {
		setPreferredSize(new Dimension(350,85));
		setBackground(Color.WHITE);
		setLayout(new FlowLayout());
		setName(password);
		
		lblServiceName = new JLabel(serviceName, SwingConstants.CENTER);
		JLabel lblUsername = new JLabel(username, SwingConstants.CENTER);
		JLabel lblEMail = new JLabel(eMail, SwingConstants.CENTER);
		
		lblServiceName.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblServiceName.setPreferredSize(new Dimension(350,35));
		
		lblUsername.setPreferredSize(new Dimension(350,15));
		
		lblEMail.setForeground(new Color(180,180,180));
		lblEMail.setPreferredSize(new Dimension(350,15));
		
		add(lblServiceName);
		add(lblUsername);
		add(lblEMail);
		
		JPopupMenu actions = new JPopupMenu();
		JMenuItem mntmCopyUsername = new JMenuItem("Copy Username");
		JMenuItem mntmCopyEMail = new JMenuItem("Copy E-Mail");
		JMenuItem mntmCopyPassword = new JMenuItem("Copy Password");
		JSeparator sep = new JSeparator();
		mntmDeleteEntry = new JMenuItem("Delete entry");
		
		mntmCopyUsername.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				copyTextToClipboard(lblUsername.getText());
			}
		});
		mntmCopyEMail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				copyTextToClipboard(lblEMail.getText());
			}
		});
		mntmCopyPassword.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				copyTextToClipboard(getName());
			}
		});
		
		actions.add(mntmCopyUsername);
		actions.add(mntmCopyEMail);
		actions.add(mntmCopyPassword);
		actions.add(sep);
		actions.add(mntmDeleteEntry);
		
		//hovering animation + click event
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				actions.show(e.getComponent(), e.getX(), e.getY());			        
			}
			@Override
			public void mouseEntered(MouseEvent e) {
				setBackground(new Color(238,238,238));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setBackground(new Color(255,255,255));
			}
		});
	}
	
	public String getServiceName() {
		return lblServiceName.getText();
	}
	
	public void copyTextToClipboard(String text) {
		StringSelection strSel = new StringSelection(text);
		Clipboard clp = Toolkit.getDefaultToolkit().getSystemClipboard();
		clp.setContents(strSel, null);
	}

}
