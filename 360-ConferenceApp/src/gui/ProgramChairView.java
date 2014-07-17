package gui;

import guisupport.SPCDemote;
import guisupport.SPCPromote;
import guisupport.ShowPaper;
import guisupport.SPCAssign;
import guisupport.ShowReviewSummary;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import objects.SPCReview;
import roles.User;

/**
 * Panel that handles Program Chair display and available operations.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class ProgramChairView extends JPanel {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the list of papers in pc conference
	 */
	private List<Paper> myPapers;
	
	/**
	 * the table for displaying papers
	 */
	private JTable myTable;
	
	/**
	 * Creates new programcharview panel.
	 * 
	 * @param theUser the current user
	 */
	public ProgramChairView(final User theUser) {
		myUser = theUser;
		myPapers = theUser.getPapers();
		myTable = null;
		createView();
	}
	
	/**
	 * Creates the actual pc panel.
	 */
	private void createView() {
		
		this.setLayout(new BorderLayout());
		final JPanel southPanel = new JPanel();
		
		//creates table columns and rows
		final String[] columnNames = {"Paper Title", "View Paper", "SPC", "Assign SPC", "Review Summary", 
										"Accept/Reject", "Status"};
		final Object[][] data = new Object[myPapers.size()][columnNames.length];
		for (int i = 0; i < myPapers.size(); i++) {
			
		    final Paper thePaper = myPapers.get(i);
			
			data[i][0] = thePaper.myTitle;
			data[i][1] = "View";
			data[i][2] = myUser.getSPCName(thePaper);
			data[i][3] = "Assign";
			data[i][4] = "View Summary";
			data[i][5] = "Accept/Reject";
			data[i][6] = myUser.getPaperStatus(thePaper);
		}
		
		//set table prorperties
		final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		myTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		if (theColumn == 0 || theColumn == 2 || theColumn == 6) { 
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
	    
		//setup assign spc buttons and action
	    final Action assignSPCAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final List<User> theList = myUser.getSPCList(myPapers.get(theRow));
		        new SPCAssign(myUser, myPapers.get(theRow), theList);
		    }
		};
		new TButton(myTable, assignSPCAction, 3);
		
		//setup view overall review summary buttons and action
		final Action viewSummaryAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final Paper thePaper = myPapers.get(theRow);
		        final List<Review> theReviews = myUser.getReviews(thePaper);
		        final SPCReview theSPCReview = myUser.getSPCReview(thePaper);
		        new ShowReviewSummary(myUser, thePaper, theReviews, theSPCReview);
		    }
		};
		new TButton(myTable, viewSummaryAction, 4);
	    
		//setup paper decision buttons and action
		final Action acceptRejectAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final Paper thePaper = myPapers.get(theRow);
		        if (myUser.getIntPaperStatus(thePaper) == 2) {
			        int choice = JOptionPane.showInternalOptionDialog(getParent(), "Please Accept or Reject \"" + thePaper.myTitle
			        		+ "\"", "Accept/Reject Paper", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, 
			        		new Object[] {"Accept", "Reject", "Cancel"}, null);
			        if (choice == JOptionPane.YES_OPTION) {
			        	myUser.acceptRejectPaper(thePaper, true);
			        } else if (choice == JOptionPane.NO_OPTION) {
			        	myUser.acceptRejectPaper(thePaper, false);
			        }
		        } else {
		        	JOptionPane.showMessageDialog(myTable, "This function not available due to paper status.", 
							"Action Not Available", JOptionPane.INFORMATION_MESSAGE);
		        }
		    }
		};
		new TButton(myTable, acceptRejectAction, 5);
		
	    final JScrollPane myPane = new JScrollPane(myTable);
	    
	    //setup promote button and action
	    final JButton promoteButton = new JButton("Promote SPC");
	    promoteButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SPCPromote(myUser.getPotentialSPCs(), myUser);
			}
	    });
	    
	    //setup demote button and action
	    final JButton demoteButton = new JButton("Demote SPC");
	    demoteButton.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SPCDemote(myUser.getCurrentSPCs(), myUser);
			}
	    });
	    
	    southPanel.add(promoteButton);
	    southPanel.add(demoteButton);
	    
	    this.add(southPanel, BorderLayout.SOUTH);
		this.add(myPane, BorderLayout.CENTER);
	}

}
