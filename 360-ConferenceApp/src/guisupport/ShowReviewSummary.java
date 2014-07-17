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
import objects.SPCReview;
import roles.User;

/**
 * Shows SPCReview and bottom-level Reviews in a window for PC viewing.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class ShowReviewSummary {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the current paper
	 */
	private final Paper myPaper;
	
	/**
	 * the list of bottom-level reviews for this paper
	 */
	private final List<Review> myReviews;
	
	/**
	 * the spcreview for this paper
	 */
	private final SPCReview mySPCReview;
	
	/**
	 * the window's frame
	 */
	private final JFrame myFrame;
	
	/**
	 * Creates new summary review viewing window.
	 * 
	 * @param theUser the current user
	 * @param thePaper the current paper
	 * @param theReviews the reviews to be displayed
	 * @param theSPCReview the spc review to be displayed
	 */
	public ShowReviewSummary(final User theUser, final Paper thePaper, final List<Review> theReviews, final SPCReview theSPCReview) {
		myUser = theUser;
		myPaper = thePaper;
		myReviews = theReviews;
		mySPCReview = theSPCReview;
		myFrame = new JFrame();
		createWindow();
	}
	
	/**
	 * Creates the actual window display and contents.
	 */
	private void createWindow() {
		
		myFrame.setBounds(75, 75, 600, 400);
		myFrame.setTitle("View Overall Review Summary");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JTextArea reviewField = new JTextArea();
		reviewField.setEditable(false);
		reviewField.setLineWrap(true);
		reviewField.setWrapStyleWord(true);
		JScrollPane thePane = new JScrollPane(reviewField);
		
		final StringBuilder theInfo = new StringBuilder();
		
		theInfo.append("\n");
		theInfo.append("Paper Title: ");
		theInfo.append(myPaper.myTitle);
		theInfo.append("\nAuthor: ");
		theInfo.append(myPaper.myAuthor);
		theInfo.append("\n\n");
		theInfo.append("Assigned Reviewers:");
		theInfo.append("\n");
		for (final User u : myUser.getAssignedReviewersList(myPaper)) {
			theInfo.append(u.getFullName());
			theInfo.append("\n");
		}
		theInfo.append("\n");
		theInfo.append("----------------------------------------------------------------------------------");
		theInfo.append("\n\n");
		
		//if all reviews and spcreview completed
		if (myUser.getIntPaperStatus(myPaper) > 1) {
			
			theInfo.append("Reviewer Ratings Overview");
			theInfo.append("\n");
			
			//for loop through review questions, ommiting final summary value
			//and get each reviewers rating (comma separated)
			for (int i = 0; i < Review.REVIEW_QUESTIONS.length - 1; i++) {
				theInfo.append("\n");
				theInfo.append(Review.REVIEW_QUESTIONS[i]);
				theInfo.append(" ");
				for (Review r : myReviews) {
					theInfo.append(r.myRatings[i]);
					theInfo.append(", ");
				}
				theInfo.delete(theInfo.length()-2, theInfo.length());
				theInfo.append("\n");
			}
			
			//append all review summary ratings
			theInfo.append("\n");
			theInfo.append("Summary Ratings: ");
			for (Review r : myReviews) {
				theInfo.append(r.myRatings[9]);
				theInfo.append(", ");
			}
			theInfo.delete(theInfo.length()-2, theInfo.length());
			
			//append spcreview rating and recommmendation
			theInfo.append("\n\n");
			theInfo.append("----------------------------------------------------------------------------------");
			theInfo.append("\n\n");
			theInfo.append("SPC Summary Rating: ");
			theInfo.append(mySPCReview.myRating);
			theInfo.append("\n\n");
			theInfo.append("SPC Recommendation: ");
			theInfo.append(mySPCReview.myComment);
			
		} else { //reviews incomplete
			
			theInfo.append("Currently Awaiting Reviews");
			
		}
			theInfo.append("\n\n");
			theInfo.append("----------------------------------------------------------------------------------");
			theInfo.append("\n\n");

		
		reviewField.setText(theInfo.toString());
		reviewField.setCaretPosition(0);
		
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
