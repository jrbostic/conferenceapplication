package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import roles.User;
import access.Database;

/**
 * Pane that handles the initial application login.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class Login {
	
	/**
	 * The field names.
	 */
	private final String[] fields = {"Username: ", "Password: "};
	
	/**
	 * The button names.
	 */
	private final String[] buttons = {"Login", "Cancel"};
	
	/**
	 * Constructs new login with no initial username.
	 */
	public Login() {
		this("");
	}
	
	/**
	 * Construct a new login with initial username.
	 * 
	 * @param theUsername the intial username field value
	 */
	public Login(final String theUsername) {
		
		//create panels
		final JPanel mainPanel = new JPanel(new GridLayout(3, 0, 0, 0));
		final JPanel topPanel = new JPanel(new BorderLayout());
		final JPanel botPanel = new JPanel(new BorderLayout());
		
		//create labels and fields and set state
		final JLabel usernameLabel = new JLabel(fields[0]);
		final JTextField usernameText = new JTextField(theUsername);
		usernameText.setColumns(20);
		final JLabel passwordLabel = new JLabel(fields[1]);
		final JPasswordField passwordText = new JPasswordField("");
		passwordText.setColumns(20);
		
		final JButton resetButton = new JButton("Forgot your password?");
		resetButton.setOpaque(false);
		resetButton.setContentAreaFilled(false);
		resetButton.setBorderPainted(false);
		resetButton.setForeground(Color.BLUE);
		resetButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final String thePassword = Database.getPassword(usernameText.getText());
				if (thePassword.equals("Account Not Found")) {
					JOptionPane.showMessageDialog(mainPanel, thePassword);
				} else {
					passwordText.setText(thePassword);
				}
			}
		});
		
		//add labels and fields to appropriate panels
		topPanel.add(usernameLabel, BorderLayout.CENTER);
		topPanel.add(usernameText, BorderLayout.EAST);
		botPanel.add(passwordLabel, BorderLayout.CENTER);
		botPanel.add(passwordText, BorderLayout.EAST);
		
		//add sub-panels to main panel
		mainPanel.add(topPanel);
		mainPanel.add(botPanel);
		
		mainPanel.add(resetButton);
		
		//pass main panel as parameter to dialog
		final int choice = JOptionPane.showOptionDialog(null, mainPanel, "MSEE Conference Login", 
														JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, 
														null, buttons, "holla");
		
		
		//if dialog does not end with cancel button being pressed
		if (choice == JOptionPane.OK_OPTION) {
			
			User theUser = null;
			
			//gather password into string
			String pass = "";
			for (char c : passwordText.getPassword()) {
				pass += c;
			}
			
			try {
				theUser = Database.login(usernameText.getText(), pass);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			if (theUser == null) {
				JOptionPane.showMessageDialog(mainPanel, "Sorry, couldn't find your account!");
				new Login(usernameText.getText());
			} else {
				new MainView(theUser);
			}
		}
		
	}
	
}
