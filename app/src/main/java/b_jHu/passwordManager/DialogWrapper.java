package b_jHu.passwordManager;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;

@SuppressWarnings("serial")
public class DialogWrapper {
	
	public DialogWrapper() {
		
	}
	
	class AddEntryDialogPane extends JPanel {
		JLabel lblServiceName;
		JLabel lblCategory;
		JLabel lblWebsite;
		JLabel lblUsername;
		JLabel lblEMail;
		JLabel lblPassword;
		
		JTextField serviceNameInput;
		JTextField websiteInput;
		JTextField usernameInput;
		JTextField eMailInput;
		
		JPasswordField passwordInput;

		SpinnerModel table;
		JSpinner categoryOptions;  
		
		public AddEntryDialogPane(String[] categories) {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			lblServiceName = new JLabel("Service name");
			lblCategory = new JLabel("Category");
			lblWebsite = new JLabel("Website");
			lblUsername = new JLabel("Username");
			lblEMail = new JLabel("E-mail");
			lblPassword = new JLabel("Password");
			
			serviceNameInput = new JTextField();
			websiteInput = new JTextField();
			usernameInput = new JTextField();
			eMailInput = new JTextField();
			
			passwordInput = new JPasswordField();
			
			table = new SpinnerListModel(categories);
			categoryOptions = new JSpinner(table);
			
			add(lblServiceName);
			add(serviceNameInput);
			add(lblCategory);
			add(categoryOptions);
			add(lblWebsite);
			add(websiteInput);
			add(lblUsername);
			add(usernameInput);
			add(lblEMail);
			add(eMailInput);
			add(lblPassword);
			add(passwordInput);
		}
		
		public String[] getInput() {
			return new String[]{serviceNameInput.getText(), serviceNameInput.getText().toLowerCase(), categoryOptions.getValue().toString(), websiteInput.getText(), usernameInput.getText(), eMailInput.getText(), new String(passwordInput.getPassword())};
		}
		
	}
	
	class RemoveEntryDialogPane extends JPanel {
		JLabel lblInstruction;
		
		JTextField serviceToRemoveInput;
		
		public RemoveEntryDialogPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			lblInstruction = new JLabel("Service to remove");
			
			serviceToRemoveInput = new JTextField();
			
			add(lblInstruction);
			add(serviceToRemoveInput);
		}
		
		public String getInput() {
			return serviceToRemoveInput.getText();
		}
	}
	
	class MasterPasswordInputPane extends JPanel {		
		JLabel lblInstruction;
		
		JPasswordField passwordInput;
		
		public MasterPasswordInputPane() {
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			lblInstruction = new JLabel("Input the master password");
			
			passwordInput = new JPasswordField();
			
			add(lblInstruction);
			add(passwordInput);
		}
		
		public char[] getInput() {
			return passwordInput.getPassword();
		}
		
		public void deletePasswordInputText() {
			passwordInput.setText("");
		}
	}
	
	class CreateDatabasePane extends JPanel {
		JLabel lblInstruction;
		JLabel confirmInstruction;
		
		JPasswordField masterPassInput;
		JPasswordField confirmPassInput;
		
		public CreateDatabasePane() {
			setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			
			lblInstruction = new JLabel("Input a 'master password':");
			gbc.gridy = 0;
			gbc.gridx = 0;
			add(lblInstruction, gbc);
			
			confirmInstruction = new JLabel("Confirm your master password:");
			gbc.gridy = 0;
			gbc.gridx = 1;
			add(confirmInstruction, gbc);
			
			masterPassInput = new JPasswordField(20);
			gbc.gridy = 1;
			gbc.gridx = 0;
			add(masterPassInput, gbc);
			
			confirmPassInput = new JPasswordField(20);
			gbc.gridy = 1;
			gbc.gridx = 1;
			gbc.insets = new Insets(0,10,0,0);
			add(confirmPassInput, gbc);
		}
		
		public char[] getMasterPass() {
			return masterPassInput.getPassword();
		}
		
		public char[] getConfirmPass() {
			return confirmPassInput.getPassword();
		}
	}
}
