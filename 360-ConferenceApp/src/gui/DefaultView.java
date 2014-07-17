package gui;

import guisupport.PaperSession;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import objects.Conference;
import roles.User;

/**
 * Panel that handles Default display of selected conference (w/o role selected).
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class DefaultView extends JPanel {
	
	/**
	 * The user.
	 */
	private final User myUser;
	
	/**
	 * Creates new defaultview panel.
	 * 
	 * @param theUser the current user
	 */
	public DefaultView(final User theUser) {
		myUser = theUser;
		createPanel();
	}
	
	/**
	 * Creates actual panel and functionality.
	 */
	private void createPanel() {
		
		this.setLayout(new BorderLayout());
		
		final List<Conference> theConferences = myUser.getConferences();
		
		final JPanel centerPanel = new JPanel(new BorderLayout());
		JPanel centerNorthPanel = new JPanel();
		JPanel centerCenterPanel = new JPanel();
		
		final JLabel welcomeMessage = new JLabel();
		welcomeMessage.setFont(new Font("Title", Font.BOLD, 20));
		welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
		welcomeMessage.setVerticalAlignment(SwingConstants.BOTTOM);
		
		final JPanel descriptionPanel = new JPanel();
		final JTextArea descriptionMessage = new JTextArea();
		descriptionMessage.setLineWrap(true);
		descriptionMessage.setWrapStyleWord(true);
		descriptionMessage.setOpaque(false);
		descriptionMessage.setEditable(false);
		descriptionMessage.setSize(new Dimension(600, 200));
		descriptionMessage.setFont(new Font("Description", Font.PLAIN, 16));
		descriptionPanel.add(descriptionMessage);
		
		centerNorthPanel.add(welcomeMessage);
		centerCenterPanel.add(descriptionPanel);
		
		//hacks in conference information
		//hardcoded on this iteration, but should migrate descriptions
		// into the conference objects and db conference table
		for (Conference c : theConferences) {
			if (c.myID == myUser.getConferenceID()) {
				welcomeMessage.setText(c.myName);
				if (c.myID == 4) {
					descriptionMessage.setText("OOPSLA is an annual conference covering topics related to object-oriented programming systems, languages and applications.  Like other conferences, OOPSLA offers various tracks and many simultaneous sessions, and thus has a different meaning to different people.  It is an academic conference, and draws doctoral students who present peer-reviewed papers.  It also draws a number of non-academic attendees, many of whom present experience reports and conduct panels, workshops and tutorials.");
				} else if (c.myID == 8) {
					descriptionMessage.setText("ASME is a not-for-profit membership organization that enables collaboration, knowledge sharing, career enrichment, and skills development across all engineering disciplines, toward a goal of helping the global engineering community develop solutions to benefit lives and livelihoods.");
				} else if (c.myID == 9) {
					descriptionMessage.setText("The PNWCSE seeks broad interaction and exchange of ideas from different sources. Whether you are a software quality engineer, tester, manager, consultant, Agilist, non-Agilist, software developer, maintenance engineer, or whether you are directly in the field of software quality or simply related to it or concerned about it, we invite you to interact.");
				} else if (c.myID == 10) {
					descriptionMessage.setText("This conference is intended to promote scientific exchange of interdisciplinary research at the intersection of the learning sciences and computer science. Inspired by the emergence of Massive Open Online Courses (MOOCs) and the accompanying huge shift in thinking about education, this conference was created by ACM as a new scholarly venue and key focal point for the review and presentation of the highest quality research on how learning and teaching can change and improve when done at scale.");
				}
			}
		}
		
		centerPanel.add(centerNorthPanel, BorderLayout.NORTH);
		centerPanel.add(centerCenterPanel, BorderLayout.CENTER);
		
		this.add(centerPanel, BorderLayout.CENTER);
		
		
		//creates the submit paper button and panel for south border,
		//which is carried over to the author panel as inherited functionality
		final JPanel southPanel = new JPanel();
		final JButton button = new JButton("Submit A Paper");
		button.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!myUser.hasMaxPapers()) {
					new PaperSession(myUser);
				} else {
					JOptionPane.showMessageDialog(centerPanel, "You have already submitted the maximum " 
														+ "allowable papers to this conference.", 
														"Max Papers Submitted", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		southPanel.add(button);
		this.add(southPanel, BorderLayout.SOUTH);
	}
}
