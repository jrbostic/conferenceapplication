package gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import roles.User;

/**
 * Panel that handles welcome display when no conference selected.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class BlankView extends JPanel {
	
	/**
	 * The user.
	 */
	private final User myUser;
	
	/**
	 * Creates new blankview panel object.
	 * 
	 * @param theUser the user object
	 */
	public BlankView(final User theUser) {
		myUser = theUser;
		createPanel();
	}
	
	/**
	 * Creates the actual panel.
	 */
	private void createPanel() {
		
		this.setLayout(new BorderLayout());
		
		JPanel centerPanel = new JPanel(new GridLayout(0, 1));
		
		JLabel welcomeMessage = new JLabel("Welcome to the MSEE Conference Management Application!");
		welcomeMessage.setFont(new Font("Boombastic", Font.BOLD, 20));
		welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
		final JLabel loggedinMessage = new JLabel("Logged in as " + myUser.getFullName() + ".");
		loggedinMessage.setHorizontalAlignment(SwingConstants.CENTER);
		loggedinMessage.setVerticalAlignment(SwingConstants.TOP);
		
		centerPanel.add(welcomeMessage);
		centerPanel.add(loggedinMessage);
		
		this.add(centerPanel, BorderLayout.CENTER);
		

		this.add(new JPanel(), BorderLayout.SOUTH);
	}
}
