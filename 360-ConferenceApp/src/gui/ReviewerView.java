package gui;

import guisupport.ReviewSession;
import guisupport.ShowPaper;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import objects.Paper;
import roles.User;

/**
 * Panel that handles Reviewer display and available operations.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
@SuppressWarnings("serial")
public class ReviewerView extends JPanel {
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the list of reviewer's assigned papers for current conference
	 */
	private final List<Paper> myPapers;
	
	/**
	 * the table for holding paper data
	 */
	private JTable myTable;
	
	
	/**
	 * Creates new reviewerview panel.
	 * 
	 * @param theUser the current user
	 */
	public ReviewerView(final User theUser) {
		myUser = theUser;
		myPapers = theUser.getPapers();	
		myTable = null;
		createView();
	}
	
	/**
	 * Creates the actual panel.
	 */
	private void createView() {
		
		this.setLayout(new BorderLayout());
		
		//create table columns and rows
		final String[] columnNames = {"Paper Title", "View Paper", "Rate Paper", "Status"};
		final Object[][] data = new Object[myPapers.size()][columnNames.length];
		for (int i = 0; i < myPapers.size(); i++) {
			
		    final Paper thePaper = myPapers.get(i);
			
			data[i][0] = thePaper.myTitle;
			data[i][1] = "View";
			data[i][2] = "Rate";
			data[i][3] = myUser.getPaperStatus(thePaper);
		
		}
		
		//set table prorperties
		final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		myTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		if (theColumn == 1 || theColumn == 2) { 
	        			return true;    
	        		}
	        		return false;
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
	    
		//setup rate buttons and action
		final Action rateAction = new AbstractAction() {
			@Override
		    public void actionPerformed(final ActionEvent e) {
		        final int theRow = Integer.valueOf(e.getActionCommand());
		        final Paper thePaper = myPapers.get(theRow);
		        if (myUser.getIntPaperStatus(thePaper) == 0) {
		        	new ReviewSession(myUser, thePaper);
		        } else {
		        	JOptionPane.showMessageDialog(myTable, "This function not available due to paper status.", 
							"Action Not Available", JOptionPane.INFORMATION_MESSAGE);
		        }
		    }
		};
		new TButton(myTable, rateAction, 2);
		
	    final JScrollPane myPane = new JScrollPane(myTable);
	    
	    this.add(new JPanel(), BorderLayout.SOUTH);
		this.add(myPane, BorderLayout.CENTER);
	}

}
