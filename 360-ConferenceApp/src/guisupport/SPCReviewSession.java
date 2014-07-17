package guisupport;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Paper;
import objects.SPCReview;
import roles.User;

/**
 * Class that implements interface to fill out spcreview.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class SPCReviewSession {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the current paper
	 */
	private final Paper myPaper;
	
	/** 
	 * the current spcreview
	 */
	private final SPCReview myReview;
	
	/**
	 * Creates new spcreview sesssion window.
	 * 
	 * @param theUser the current user
	 * @param thePaper the current paper
	 */
	public SPCReviewSession(final User theUser, final Paper thePaper) {
		myUser = theUser;
		myPaper = thePaper;
		myReview = myUser.getSPCReview(myPaper);
		createWindow();
	}
	
	/**
	 * Creates the actual window.
	 */
	private void createWindow() {
		
		final JFrame theFrame = new JFrame();
		theFrame.setBounds(100, 100, 475, 375);
		theFrame.setResizable(false);
		theFrame.setAlwaysOnTop(true);
		theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theFrame.setTitle("SPC Review Form");
		
		final JPanel northPanel = new JPanel();
		final JPanel centerPanel = new JPanel();
		final JPanel southPanel = new JPanel();
		
		northPanel.add(new JLabel("Rating:  "));
		
		//setup northern rating buttons (1-5)
		final JRadioButton[] theRateButtons = new JRadioButton[5];
		final ButtonGroup theButtonGroup = new ButtonGroup();
		for (int i = 0; i < theRateButtons.length; i++) {
			theRateButtons[i] = new JRadioButton("" + (i + 1));
			theButtonGroup.add(theRateButtons[i]);
			northPanel.add(theRateButtons[i]);
			if (myReview.myRating == i+1) {
				theRateButtons[i].setSelected(true);
			}
			theRateButtons[i].addActionListener( new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					myReview.myRating = Integer.parseInt(e.getActionCommand());
				}
			});
		}
		
		//setup comment box
		final String currComm = myReview.myComment;
		final String commentText = currComm.length()>0 ? currComm : "Insert Text Here..." ;
		final JTextArea theCommentField = new JTextArea();
		theCommentField.setText(commentText);
		theCommentField.setPreferredSize(new Dimension(400, 300));
		theCommentField.setLineWrap(true);
		theCommentField.setWrapStyleWord(true);
		final JScrollPane thePane = new JScrollPane(theCommentField);
		centerPanel.add(thePane);
		
		//setup submit button and action
		final JButton submitButton = new JButton("Submit");
		submitButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myReview.myComment = theCommentField.getText().trim().replaceAll("'", "''");
				myUser.commitSPCReview(myReview, myPaper);
				JOptionPane.showMessageDialog(theFrame, "Review submitted successfully!!!", "Submission Success", 
												JOptionPane.INFORMATION_MESSAGE);
				theFrame.dispose();
			}
		});
		
		//setup cancel button and action
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int choice = JOptionPane.showConfirmDialog(theFrame, "Are you sure you want to cancel submission?"
						+ "  All new changes will be lost!", "Confirm Cancel", 
						JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					theFrame.dispose();
				}
			}
		});
		
		southPanel.add(submitButton);
		southPanel.add(cancelButton);
		
		theFrame.add(northPanel, BorderLayout.NORTH);
		theFrame.add(centerPanel, BorderLayout.CENTER);
		theFrame.add(southPanel, BorderLayout.SOUTH);
		theFrame.add(new JPanel(), BorderLayout.EAST);
		theFrame.add(new JPanel(), BorderLayout.WEST);
		
		theFrame.setVisible(true);
	}
}
