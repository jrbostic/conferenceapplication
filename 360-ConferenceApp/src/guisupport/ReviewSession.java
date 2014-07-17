package guisupport;

import java.awt.BorderLayout;
import java.awt.GridLayout;
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
import objects.Review;
import roles.User;

/**
 * This window implements user creation or update of a review.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class ReviewSession {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the paper being reviewed
	 */
	private final Paper myPaper;
	
	/**
	 * the review being worked on (or edited rather)
	 */
	private final Review myReview;
	
	/**
	 * Creates new review session window.
	 * 
	 * @param theUser the current user
	 * @param thePaper the paper being reviewed
	 */
	public ReviewSession(final User theUser, final Paper thePaper) {
		myUser = theUser;
		myPaper = thePaper;
		myReview = myUser.getReview(myPaper);
		createWindow();
	}
	
	/**
	 * Creates the actual review window.
	 */
	private void createWindow() {
		
		final JFrame theFrame = new JFrame();
		theFrame.setBounds(100, 100, 700, 750);
		theFrame.setResizable(false);
		theFrame.setAlwaysOnTop(true);
		theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		theFrame.setTitle("Review Form");
		
		final JPanel northPanel = new JPanel(new GridLayout(0, 2));
		final JPanel northEastPanel = new JPanel(new GridLayout(0, 6));
		final JPanel northWestPanel = new JPanel();
		final JPanel centerPanel = new JPanel(new GridLayout(0, 2));
		final JPanel southPanel = new JPanel();
		
		//setup northern labels
		northWestPanel.add(new JLabel("QUESTIONS"));
		northPanel.add(northWestPanel);
		northEastPanel.add(new JPanel());
		northEastPanel.add(new JLabel("Strong -"));
		northEastPanel.add(new JPanel());
		northEastPanel.add(new JPanel());
		northEastPanel.add(new JLabel("Strong +"));
		northPanel.add(northEastPanel);
		
		// loops through creating row for each question which contain the question text
		// in a text area and one radio button group of size 5 in a panel; those are then
		// added to the center panel with grid layout manager
		final ButtonGroup[] theGroups = new ButtonGroup[Review.REVIEW_QUESTIONS.length];
		for (int k = 0; k < theGroups.length; k++) {
			final int index = k;
			final JPanel thePanel = new JPanel();
			final JRadioButton[] theRateButtons = new JRadioButton[5];
			theGroups[k] = new ButtonGroup();
			for (int i = 0; i < theRateButtons.length; i++) {
				final int rating = i + 1;
				theRateButtons[i] = new JRadioButton("" + (i + 1));
				theGroups[k].add(theRateButtons[i]);
				thePanel.add(theRateButtons[i]);
				if (myReview.myRatings[k] == i + 1) {
					theRateButtons[i].setSelected(true);
				}
				theRateButtons[i].addActionListener( new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						myReview.myRatings[index] = rating;
					}
				});
			}
			
			final JTextArea theQuestion = new JTextArea(Review.REVIEW_QUESTIONS[k]);
			theQuestion.setLineWrap(true);
			theQuestion.setWrapStyleWord(true);
			theQuestion.setEditable(false);
			theQuestion.setOpaque(false);
			centerPanel.add(theQuestion);
			centerPanel.add(thePanel);
		}		
		
		//create comment box at bottom
		final JLabel commentLabel = new JLabel("Additional Comments:");
		final JTextArea theComment = new JTextArea();
		theComment.setLineWrap(true);
		theComment.setWrapStyleWord(true);
		JScrollPane thePane = new JScrollPane(theComment);
		if (!myReview.myComment.isEmpty()) {
			theComment.setText(myReview.myComment);
		}
		centerPanel.add(commentLabel);
		centerPanel.add(thePane);
		
		//setup submit button and action
		final JButton submitButton = new JButton("Submit");
		submitButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//test all fields
				boolean canSubmit = true;
				for (int n : myReview.myRatings) {
					if (n == 0) {
						canSubmit = false;
					}
				}
				//if all field filled out, proceed
				if (canSubmit) {
					myReview.myComment = theComment.getText().trim().replaceAll("'", "''");
					myUser.commitReview(myReview, myPaper);
					JOptionPane.showMessageDialog(theFrame, "Review submitted successfully!!!", 
													"Submission Success", JOptionPane.INFORMATION_MESSAGE);
					theFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(theFrame, "Ooops!  You must fill out all ratings to submit.", 
							"Submission Incomplete", JOptionPane.INFORMATION_MESSAGE);
				}
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
		theFrame.add(new JPanel(), BorderLayout.WEST);
		theFrame.add(new JPanel(), BorderLayout.EAST);
		
		theFrame.setVisible(true);
	}
}
