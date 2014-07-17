package gui;

import guisupport.MyAccountSession;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import objects.Conference;
import roles.User;

/**
 * Class that provides conference and role selection as well as the logic
 * to switch role panels.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public abstract class Selecter implements Observer {
	
	/**
	 * array of role designations
	 */
	private static final String[] ROLES = { "Author", "Reviewer", "Subprogram Chair", "Program Chair"};
	
	/**
	 * the current user
	 */
	protected final User myUser;
	
	/**
	 * the main application frame
	 */
	protected final JFrame myFrame;
	
	/**
	 * the list of existent conferences
	 */
	private final List<Conference> myConferences;
	
	/**
	 * dropdown of available roles (given conference)
	 */
	private final JComboBox<String> myRoleDrop;
	
	/**
	 * the current role view being displayed
	 */
	private JPanel myView;
	
	/**
	 * Creates main application perspective
	 * 
	 * @param theUser the current user
	 */
	public Selecter(final User theUser) {
		myUser = theUser;
		myView = new BlankView(myUser);
		myFrame = new JFrame();
		myConferences = myUser.getConferences();
		myRoleDrop = new JComboBox<String>();
		setupWindow();
	}
	
	/**
	 * Creates the actual main window of application.
	 */
	private void setupWindow() {
		
		myFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		myFrame.setMinimumSize(new Dimension(800, 500));
		myFrame.setTitle("MSEE Conference Management Application");
		myFrame.setLocationByPlatform(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//create all panels
		final JPanel northPanel = new JPanel(new GridLayout(0, 1));
		final JPanel northNorthPanel = new JPanel(new BorderLayout());
		final JPanel northNorthEastPanel = new JPanel(new FlowLayout());
		final JPanel northSouthPanel = new JPanel(new FlowLayout());
		
		//setup account management and logout buttons as well as actions
		final JButton manageButton = new JButton("My Account");
		final JButton logoutButton = new JButton("Log Out");
		manageButton.setOpaque(false);
		manageButton.setContentAreaFilled(false);
		manageButton.setBorderPainted(false);
		manageButton.setForeground(Color.BLUE);
		manageButton.setHorizontalAlignment(SwingConstants.RIGHT);
		logoutButton.setOpaque(false);
		logoutButton.setContentAreaFilled(false);
		logoutButton.setBorderPainted(false);
		logoutButton.setForeground(Color.BLUE);
		logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
		manageButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new MyAccountSession(myUser);
			}	
		});
		logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				myFrame.dispose();
				new Login(myUser.myUsername);
			}	
		});
		
		//add manage and logout buttons to topmost area 
		northNorthEastPanel.add(manageButton);
		northNorthEastPanel.add(logoutButton);
		northNorthPanel.add(northNorthEastPanel, BorderLayout.EAST);
		
		//setup role combo box  (scattered declarations and instantiations purposeful)
		populateRoles();
		
		//setup conference combo box
		final String[] conferenceNames = new String[myConferences.size() + 1];
		conferenceNames[0] = "Select Your Conference";
		for (int i = 1; i <= myConferences.size(); i++) {
			conferenceNames[i] = myConferences.get(i - 1).myName;
		}
		final JComboBox<String> conferenceDrop = new JComboBox<String>(conferenceNames);
		
		//setup action listener for role combo box
		myRoleDrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myUser.setRole(getRoleID(myRoleDrop.getSelectedItem()));
				createRoleView();
			}
		});
		
		//setup action listener for conference dropdown box
		conferenceDrop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Conference selectedConference = null;
				for (Conference conf : myConferences) {
					if (conf.myName.equals(conferenceDrop.getSelectedItem())) {
						selectedConference = conf;
					}
				}
				if (selectedConference != null) {
					myUser.setConference(selectedConference.myID);
				} else {
					myUser.setConference(0);
				}
				
				myUser.setRole(0);
				populateRoles();
				createRoleView();
			}
		});
		
		//add elements to upper southmost panel
		northSouthPanel.add(new JLabel("Conference: "));
		northSouthPanel.add(conferenceDrop);
		northSouthPanel.add(new JLabel("         "));
		northSouthPanel.add(new JLabel("Role: "));
		northSouthPanel.add(myRoleDrop);
		
		//add both sub-panels to northern panel and then to frame
		northPanel.add(northNorthPanel);
		northPanel.add(northSouthPanel);
		northPanel.add(new JSeparator(SwingConstants.HORIZONTAL));
		myFrame.add(northPanel, BorderLayout.NORTH);
		
		myFrame.add(myView, BorderLayout.CENTER);
		
		myFrame.setVisible(true);
	}
	
	/**
	 * Creates and assigns new role-specific panel on each call.
	 */
	protected void createRoleView() {
		
		// create new mainView 
		// 	noconf + norole = blankview
		// 	norole = defaultview
		//	roleid = 1 authorView, 
		//	roleid = 2 reviewerView, 
		//	roleid = 3 spcView, 
		//	roleid = 4 pcView
		
		final int roleID = myUser.getRoleID();
		final int confID = myUser.getConferenceID();
		
		myFrame.remove(myView);
		
		if (confID == 0) {
			myView = new BlankView(myUser);
		} else if (roleID == 0) {
			myView = new DefaultView(myUser);
		} else if (roleID == 1) {
			myView = new AuthorView(myUser);
		} else if (roleID == 2) {
			myView = new ReviewerView(myUser);
		} else if (roleID == 3) {
			myView = new SubprogramChairView(myUser);
		} else if (roleID == 4) {
			myView = new ProgramChairView(myUser);
		}
		
		myFrame.add(myView, BorderLayout.CENTER);
		myFrame.getContentPane().validate();
		myFrame.getContentPane().repaint();
	}
	
	/**
	 * Determines roleID from combobox selection.
	 * 
	 * @param selectedItem selected role string
	 * @return integer correlating to role id value
	 */
	private int getRoleID(final Object selectedItem) {
		final String theRole = (String) selectedItem;
		int theID = 0;
		
		for (int i = 0; i < 4; i++) {
			if (theRole.equals(ROLES[i])) {
				theID = i + 1;
			}
		}
		
		return theID;
	}
	
	/**
	 * Repopulates the role combobox given conference selection.
	 */
	protected void populateRoles() {
		final List<String> theList = new ArrayList<String>();
		theList.add("Select Your Role");
		
		final int[] roles = myUser.getMyRoles();
		
		for (int i = 0; i < 4; i++) {
			if ( roles[i] == 1) {
				theList.add(ROLES[i]);
			}
		}
		
		final String[] theRoles = new String[theList.size()];
		
		myRoleDrop.setModel(new JComboBox<String>(theList.toArray(theRoles)).getModel());
		
		if (myUser.getRoleID() != 0) {
			myRoleDrop.setSelectedItem(ROLES[myUser.getRoleID() - 1]);
		}
	}
}
