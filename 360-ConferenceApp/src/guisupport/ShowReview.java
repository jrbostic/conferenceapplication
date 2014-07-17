package guisupport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Paper;
import objects.Review;
import roles.User;

/**
 * Displays all bottom-level reviews in a window for spc to view.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class ShowReview {
	
	/**
	 * the window's frame
	 */
	private final JFrame myFrame;
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the paper for which reviews are being viewed
	 */
	private final Paper myPaper;
	
	/**
	 * list of reviews 
	 */
	private final List<Review> myReviews;
	
	/**
	 * Creates new review viewing window.
	 * 
	 * @param theUser the current user
	 * @param thePaper the current paper
	 * @param theReviews the reviews to be displayed
	 */
	public ShowReview(final User theUser, final Paper thePaper, final List<Review> theReviews) {
		myFrame = new JFrame();
		myUser = theUser;
		myPaper = thePaper;
		myReviews = theReviews;
		createWindow();
	}
	
	/**
	 * Creates the actual window.
	 */
	private void createWindow() {
		
		myFrame.setBounds(75, 75, 800, 500);
		myFrame.setTitle("View Reviews");
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JTextArea paperField = new JTextArea();
		paperField.setEditable(false);
		paperField.setLineWrap(true);
		paperField.setWrapStyleWord(true);
		JScrollPane thePane = new JScrollPane(paperField);
		final StringBuilder theInfo = new StringBuilder();
		
		theInfo.append("\n");
		theInfo.append("Paper Title: ");
		theInfo.append(myPaper.myTitle);
		theInfo.append("\nAuthor: ");
		theInfo.append(myPaper.myAuthor);
		theInfo.append("\n\n");
		theInfo.append("----------------------------------------------------------------------------------");
		theInfo.append("\n\n");
		
		//if all reviews are completed
		if (myUser.getIntPaperStatus(myPaper) > 0) {
			
			//loops through reviews
			for (int j = 0; j < myReviews.size(); j++) {
				Review theReview = myReviews.get(j);
				theInfo.append("Review ");
				theInfo.append(j + 1);
				theInfo.append("\n");
				
				//loops through questions and answers in review
				for (int i = 0; i < Review.REVIEW_QUESTIONS.length; i++) {
					theInfo.append("\n");
					theInfo.append(Review.REVIEW_QUESTIONS[i]);
					theInfo.append(" ");
					theInfo.append(theReview.myRatings[i]);
					theInfo.append("\n");
				}
				
				//appends comments
				theInfo.append("\n");
				theInfo.append("Comments: ");
				theInfo.append(theReview.myComment);	
				theInfo.append("\n\n");
				theInfo.append("----------------------------------------------------------------------------------");
				theInfo.append("\n\n");
			} 	
			
		} else { //not all reviews complete
				
				theInfo.append("Currently Awaiting Reviews");
				theInfo.append("\n\n");
				theInfo.append("----------------------------------------------------------------------------------");
				theInfo.append("\n\n");
				
		}

		paperField.setText(theInfo.toString());
		paperField.setCaretPosition(0);
		
		//setup done button and dispose action
		final JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myFrame.dispose();
			}	
		});
		
		final JPanel southPanel = new JPanel();
		southPanel.add(doneButton);
		
		myFrame.add(thePane, BorderLayout.CENTER);
		myFrame.add(southPanel, BorderLayout.SOUTH);
		myFrame.add(new JPanel(), BorderLayout.NORTH);
		myFrame.add(new JPanel(), BorderLayout.EAST);
		myFrame.add(new JPanel(), BorderLayout.WEST);
		myFrame.setVisible(true);
	}

}