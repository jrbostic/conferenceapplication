package guisupport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import objects.Paper;
import roles.User;

/**
 * Window that provides GUI functionality for selection and assignment of reviewers to a paper.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class ReviewerAssign {
	
	/**
	 * the window frame
	 */
	private final JFrame myFrame;
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the current paper
	 */
	private final Paper myPaper;
	
	/**
	 * the current reviewer selected
	 */
	private final User myCurrentReviewer;
	
	/**
	 * the list of available reviewers for paper
	 */
	private final List<User> myReviewerList;
	
	/**
	 * Creates new assignment window.
	 * 
	 * @param theUser the current user
	 * @param theCurrentRev the current (prev) user
	 * @param thePaper the paper being assigned a reviewer
	 * @param theReviewerList the list of available reviewers
	 */
	public ReviewerAssign(final User theUser, final User theCurrentRev, 
							final Paper thePaper, final List<User> theReviewerList) {
		myFrame = new JFrame();
		myUser = theUser;
		myPaper = thePaper;
		myCurrentReviewer = theCurrentRev;
		myReviewerList = theReviewerList;
		createWindow();
	}
	
	/**
	 * Creates the actual window features.
	 */
	@SuppressWarnings("serial")
	private void createWindow() {
		
		myFrame.setBounds(75, 75, 400, 300);
		myFrame.setTitle("Assign Reviewer to Paper");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JPanel northPanel = new JPanel();
		final JPanel southPanel = new JPanel();
		
		northPanel.add(new JLabel("Currently Assigned: "));
		northPanel.add(new JLabel(myCurrentReviewer.getFullName()));
		
		//setup table columns and rows
		final String[] columnNames = {"Available Reviewers"};
		final Object[][] data = new Object[myReviewerList.size()][columnNames.length];
		for (int i = 0; i < myReviewerList.size(); i++) {
			data[i][0] = myReviewerList.get(i).getFullName();
		}
		
		//set table state
	    final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		final JTable theTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		return false;    
	        }
	    };
	    theTable.getTableHeader().setReorderingAllowed(false);
	    final JScrollPane thePane = new JScrollPane(theTable);
	    
	    //create ok and cancel buttons and their listeners
	    final JButton okButton = new JButton("Ok");
	    final JButton cancelButton = new JButton("Cancel");
	    okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int theRow = theTable.getSelectedRow();
				if (theRow >= 0) {
					myUser.assignReviewer(myReviewerList.get(theTable.getSelectedRow()), myCurrentReviewer, myPaper);
					myFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(theTable, "Please select a Reviewer to assign.");
				}
			}	
	    });
	    cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myFrame.dispose();
			}	
	    });
	    
	    southPanel.add(okButton);
	    southPanel.add(cancelButton);
		
		myFrame.add(thePane, BorderLayout.CENTER);
		myFrame.add(southPanel, BorderLayout.SOUTH);
		myFrame.add(northPanel, BorderLayout.NORTH);
		myFrame.add(new JPanel(), BorderLayout.EAST);
		myFrame.add(new JPanel(), BorderLayout.WEST);
		myFrame.setVisible(true);
	}
}
