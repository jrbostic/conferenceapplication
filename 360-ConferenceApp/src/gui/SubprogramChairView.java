package gui;

import guisupport.ReviewerAssign;
import guisupport.ReviewerDemote;
import guisupport.ReviewerPromote;
import guisupport.SPCReviewSession;
import guisupport.ShowPaper;
import guisupport.ShowReview;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import objects.Paper;
import objects.Review;
import roles.User;

/**
 * Panel that handles Subprogram Chair display and available operations.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class SubprogramChairView extends JPanel {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the list of spc's assigned papers
	 */
	private final List<Paper> myPapers;
	
	/**
	 * list of lists of currently assigned reviewers for each paper
	 */
	private List<List<User>> myReviewersLists;
	
	/**
	 * the table holding paper info
	 */
	private JTable myTable;
	
	/**
	 * Creates subprogramchairview panel.
	 * 
	 * @param theUser the current user
	 */
	public SubprogramChairView(final User theUser) {
		myUser = theUser;
		myPapers = theUser.getPapers();	
		myReviewersLists = new ArrayList<List<User>>();
		myTable = null;
		createView();
	}
	
	/**
	 * Creates the actual panel.
	 */
	private void createView() {
		
		this.setLayout(new BorderLayout());
		final JPanel southPanel = new JPanel();
		
		//setup the tables columns and rows
		final String[] columnNames = {"Paper Title", "View Paper", "Reviewer 1", "Reviewer 2", 
								"Reviewer 3", "Reviewer 4", "View Reviews", "Rate Paper", "Status"};
		final Object[][] data = new Object[myPapers.size()][columnNames.length];
		for (int i = 0; i < myPapers.size(); i++) {
			
		    final Paper thePaper = myPapers.get(i);
		    final List<User> theReviewers = myUser.getAssignedReviewersList(thePaper);
		    myReviewersLists.add(theReviewers);
			
			data[i][0] = thePaper.myTitle;
			data[i][1] = "View";
			data[i][2] = theReviewers.get(0).getFullName();
			data[i][3] = theReviewers.get(1).getFullName();
			data[i][4] = theReviewers.get(2).getFullName();
			data[i][5] = theReviewers.get(3).getFullName();
			data[i][6] = "View";
			data[i][7] = "Rate";
			data[i][8] = myUser.getPaperStatus(thePaper);
		}
		
		//set table prorperties
		final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		myTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		if (theColumn == 0 || theColumn == 8) { 
	        			return false;    
	        		}
	        		return true;
	        }
	    };
	    myTable.setRowHeight(25);
	    myTable.getTableHeader().setReorderingAllowed(false);
	    
	    
	    //setup view paper buttons and action
	    final Action viewPaperAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        new ShowPaper(myPapers.get(theRow), myUser);
		    }
		};
		new TButton(myTable, viewPaperAction, 1);
	    
		//setup assign reviewer buttons and action (4 total)
	    final Action assignReviewerAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final List<User> theList = myUser.getReviewerList(myPapers.get(theRow));
		        final User theCurrentRev = myReviewersLists.get(theRow).get(myTable.getSelectedColumn() - 2);
		        new ReviewerAssign(myUser, theCurrentRev, myPapers.get(theRow), theList); //currently throws exception from db
		    }
		};
		new TButton(myTable, assignReviewerAction, 2);
		new TButton(myTable, assignReviewerAction, 3);
		new TButton(myTable, assignReviewerAction, 4);
		new TButton(myTable, assignReviewerAction, 5);
		
		//setup view reviews buttons and action
		final Action viewReviewsAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final Paper thePaper = myPapers.get(theRow);
		        final List<Review> theReviews = myUser.getReviews(thePaper);
		        new ShowReview(myUser, thePaper, theReviews);
		    }
		};
		new TButton(myTable, viewReviewsAction, 6);
	    
		//setup rate buttons and action
		final Action rateAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final Paper thePaper = myPapers.get(theRow);
		        if (myUser.getIntPaperStatus(thePaper) == 1) {
		        	new SPCReviewSession(myUser, thePaper);
		        } else {
		        	JOptionPane.showMessageDialog(myTable, "This function not available due to paper status.", 
							"Action Not Available", JOptionPane.INFORMATION_MESSAGE);
		        }
		    }
		};
		new TButton(myTable, rateAction, 7);
		
	    final JScrollPane myPane = new JScrollPane(myTable);
	    
	    //setup promote reviewer button and action
	    final JButton promoteButton = new JButton("Promote Reviewer");
	    promoteButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ReviewerPromote(myUser.getPotentialReviewers(), myUser);
			}
	    });
	    
	    //setup demote reviewer button and action
	    final JButton demoteButton = new JButton("Demote Reviewer");
	    demoteButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new ReviewerDemote(myUser.getCurrentReviewers(), myUser);
			}
	    });
	    
	    southPanel.add(promoteButton);
	    southPanel.add(demoteButton);
	    
	    this.add(southPanel, BorderLayout.SOUTH);
		this.add(myPane, BorderLayout.CENTER);
	}

}
