package gui;

import guisupport.PaperSession;
import guisupport.ShowAuthorReview;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import roles.User;
import objects.Paper;

/**
 * Panel that handles Author display and available operations.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class AuthorView extends DefaultView {
	
	/**
	 * The current author.
	 */
	private final User myUser;
	
	/**
	 * The list of authored papers.
	 */
	private List<Paper> myPapers;
	
	/**
	 * The table for displaying papers and associated functionality.
	 */
	private JTable myTable;
	
	/**
	 * Constructs a new authorview panel object.
	 * 
	 * @param theUser the author
	 */
	public AuthorView(final User theUser) {
		super(theUser);	
		myUser = theUser;
		myPapers = theUser.getPapers();
		myTable = null;
		createView();
	}
	
	/**
	 * Creates the actual panel, table, buttons, functionality.
	 */
	private void createView() {
		
		//setup the table columns and rows
		final String[] columnNames = {"Paper Title", "Paper Status", "Edit Paper", "Delete Paper", "Review Details"};
		final Object[][] data = new Object[myPapers.size()][columnNames.length];
		for (int i = 0; i < myPapers.size(); i++) {
			
			final Paper thePaper = myPapers.get(i);
			
			data[i][0] = thePaper.myTitle;
			data[i][1] = myUser.getPaperStatus(thePaper);
			data[i][2] = "Edit";
			data[i][3] = "Delete";
			data[i][4] = "Reviews";
		}
		
		if (myPapers.size() == 0) {
			myUser.setRole(0);
		}
		
		//set table prorperties
		final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		myTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		if (theColumn < 2) { 
	        			return false;    
	        		}
	        		return true;
	        }
	    };
	    myTable.setRowHeight(25);
	    myTable.getTableHeader().setReorderingAllowed(false);
		
	    //setup column of buttons with associated actions//
	    
	    //create edit paper button and action
		final Action editAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        Paper thePaper = myPapers.get(theRow);
		        if (myUser.getIntPaperStatus(thePaper) == 0 
		        		&& !myUser.getPaperStatus(thePaper).equals("Under Review")) {
		        	new PaperSession(myUser , myPapers.get(theRow));
		        } else {
		        	JOptionPane.showMessageDialog(myTable, "This function not available due to paper status.", 
							"Action Not Available", JOptionPane.INFORMATION_MESSAGE);
		        }
		    }
		};
		new TButton(myTable, editAction, 2);
		
		//create delete paper button and action
		final Action deleteAction = new AbstractAction() {
			@Override
		    public void actionPerformed(ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final int choice = JOptionPane.showConfirmDialog(myTable, "Are you sure that you want to delete \"" 
		        			+ myPapers.get(theRow).myTitle + "\"?  This information will be permanently lost!",
		        			"Delete Paper?", JOptionPane.YES_NO_OPTION);
		        if (choice == JOptionPane.YES_OPTION) {
		        	final JTable theTable = (JTable) e.getSource();
		        	((DefaultTableModel) theTable.getModel()).removeRow(theRow);
		        	final Paper thePaper = myPapers.get(theRow);
		    		final String type = thePaper.myPath.substring(thePaper.myPath.lastIndexOf('.'));
		    		//delete physical file
		        	final File original = new File(System.getProperty("user.dir") + "\\Papers\\" + thePaper.hashString()
															+ "-" + myUser.getConferenceID() + type);
		        	original.delete();
		        	//detete db reference and table row
		        	myUser.deletePaper(thePaper);
		        	myPapers.remove(theRow);
		        }
		    }
		};
		new TButton(myTable, deleteAction, 3);
		
		//create view review button and action
		final Action viewReviewAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        Paper thePaper = myPapers.get(theRow);
		        new ShowAuthorReview(myUser, thePaper, myUser.getReviews(thePaper) );
		    }
		};
		new TButton(myTable, viewReviewAction, 4);
		
	    final JScrollPane myPane = new JScrollPane(myTable);
		this.add(myPane);
	}

}
