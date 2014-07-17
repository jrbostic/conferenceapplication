package guisupport;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import roles.User;

/**
 * Displays window to change account information (email & password).
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class MyAccountSession {
	
	/**
	 * the current user (author)
	 */
	private final User myUser;
	
	/**
	 * Creates new account management session.
	 * 
	 * @param theUser the current user
	 */
	public MyAccountSession(final User theUser) {
		myUser = theUser;
		createWindow();
	}
	
	/**
	 * Creates the actual management window.
	 */
	private void createWindow() {
		
		final JFrame theFrame = new JFrame();
		theFrame.setBounds(100, 100, 475, 375);
		theFrame.setResizable(false);
		theFrame.setAlwaysOnTop(true);
		theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theFrame.setTitle("Manage Account");
		
		final JPanel centerPanel = new JPanel(new GridLayout(0, 1));
		final JPanel southPanel = new JPanel();
		
		//username panel setup
		final JPanel usernamePanel = new JPanel(new GridLayout(1, 2));
		final JPanel lUsernamePanel = new JPanel();
		lUsernamePanel.add(new JLabel("Username:"));
		usernamePanel.add(lUsernamePanel);
		final JPanel rUsernamePanel = new JPanel();
		rUsernamePanel.add(new JLabel(myUser.myUsername));
		usernamePanel.add(rUsernamePanel);
		
		//given name panel setup
		final JPanel fullnamePanel = new JPanel(new GridLayout(1, 2));
		final JPanel lFullnamePanel = new JPanel();
		lFullnamePanel.add(new JLabel("Full Name:"));
		fullnamePanel.add(lFullnamePanel);
		final JPanel rFullnamePanel = new JPanel();
		rFullnamePanel.add(new JLabel(myUser.getFullName()));
		fullnamePanel.add(rFullnamePanel);
		
		//email address panel setup
		final JPanel emailPanel = new JPanel(new GridLayout(1, 2));
		final JPanel lEmailPanel = new JPanel();
		lEmailPanel.add(new JLabel("E-mail Address:"));
		emailPanel.add(lEmailPanel);
		final JPanel rEmailPanel = new JPanel();
		final JTextField emailField = new JTextField(myUser.myEmail);
		emailField.setColumns(15);
		rEmailPanel.add(emailField);
		emailPanel.add(rEmailPanel);
		
		//new password panel
		final JPanel passwordPanel = new JPanel(new GridLayout(1, 2));
		final JPanel lpasswordPanel = new JPanel();
		lpasswordPanel.add(new JLabel("Enter New Password:"));
		passwordPanel.add(lpasswordPanel);
		final JPanel rpasswordPanel = new JPanel();
		final JPasswordField theNewPass = new JPasswordField();
		theNewPass.setColumns(15);
		rpasswordPanel.add(theNewPass);
		passwordPanel.add(rpasswordPanel);
		
		//new password reentry panel
		final JPanel rePasswordPanel = new JPanel(new GridLayout(1, 2));
		final JPanel lrepasswordPanel = new JPanel();
		lrepasswordPanel.add(new JLabel("Re-Enter Password:"));
		rePasswordPanel.add(lrepasswordPanel);
		final JPanel rrepasswordPanel = new JPanel();
		final JPasswordField theReNewPass = new JPasswordField();
		theReNewPass.setColumns(15);
		rrepasswordPanel.add(theReNewPass);
		rePasswordPanel.add(rrepasswordPanel);
		
		//add all panels in order of desired appearance
		centerPanel.add(fullnamePanel);
		centerPanel.add(usernamePanel);
		centerPanel.add(emailPanel);
		centerPanel.add(passwordPanel);
		centerPanel.add(rePasswordPanel);
		
		//setup submit button and all logic to determine if submission is valid
		final JButton submitButton = new JButton("Update Account");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String thePassword = myUser.myPassword;
				if (passMatch(theNewPass.getPassword(), theReNewPass.getPassword())) {
					String theNewPassword = newPassword(theNewPass.getPassword());
					thePassword = !theNewPassword.isEmpty() ? theNewPassword : thePassword;
					String theEmail = myUser.myEmail;
					if (emailField.getText().trim().length() > 0) {
						theEmail = emailField.getText().trim();
					}
					if (theEmail.indexOf('@') != -1 && theEmail.lastIndexOf('.') > theEmail.indexOf('@') 
							&& theEmail.lastIndexOf('.') < theEmail.length()-1) {
						myUser.myEmail = theEmail;
						myUser.myPassword = thePassword;
						myUser.updateUser();
						theFrame.dispose();
						JOptionPane.showMessageDialog(theFrame, "Account update successful!", "Success!", 
								JOptionPane.INFORMATION_MESSAGE);
						
					} else {
						JOptionPane.showMessageDialog(theFrame, "Ooops!  Email not valid.", "Invalid Email", 
								JOptionPane.WARNING_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(theFrame, "Ooops!  Passwords don't match.", "Password Mismatch", 
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		//setup cancel button and action
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				theFrame.dispose();
			}
		});
		
		southPanel.add(submitButton); 
		southPanel.add(cancelButton);
		
		// add all panels to frame in respective regions
		theFrame.add(centerPanel, BorderLayout.CENTER);
		theFrame.add(southPanel, BorderLayout.SOUTH);
		theFrame.add(new JPanel(), BorderLayout.WEST); 
		theFrame.add(new JPanel(), BorderLayout.EAST);
		
		theFrame.setVisible(true);
	}
	
	/**
	 * Checks to see if new password matches in both entered fields.
	 * 
	 * @param pass first entry
	 * @param repass second entry
	 * @return true if passwords match, false otherwise
	 */
	private boolean passMatch(final char[] pass, final char[] repass) {
		boolean theyMatch = true;
		if (pass.length == repass.length) {
			for (int i = 0; i < pass.length; i++) {
				if (pass[i] != repass[i]) {
					theyMatch = false;
				}
			}
		} else {
			theyMatch = false;
		}
		return theyMatch;
	}
	
	/**
	 * Converts char array password to a string.
	 * 
	 * @param pass char array representing password
	 * @return string version of char array passed
	 */
	private String newPassword(final char[] pass) {
		String newPassword = "";
		for (char c : pass) {
			newPassword += c;
		}
		return newPassword;
	}
}
