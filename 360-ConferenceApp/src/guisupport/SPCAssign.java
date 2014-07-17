package guisupport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import objects.Paper;
import roles.User;

/**
 * Handles window for selecting and assigning an SPC to a paper.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class SPCAssign {
	
	/**
	 * the window's frame
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
	 * the list of available SPC in current conference
	 */
	private final List<User> mySPCList;
	
	/**
	 * Creates new window for selecting and assigning spc to paper.
	 * 
	 * @param theUser the current user
	 * @param thePaper the current paper
	 * @param theSPCList the list of available spcs
	 */
	public SPCAssign(User theUser, Paper thePaper, final List<User> theSPCList) {
		myFrame = new JFrame();
		myUser = theUser;
		myPaper = thePaper;
		mySPCList = theSPCList;
		createWindow();
	}
	
	/**
	 * Creates the actual assignment window.
	 */
	@SuppressWarnings("serial")
	private void createWindow() {
		
		myFrame.setBounds(75, 75, 400, 300);
		myFrame.setTitle("Assign SPC to Paper");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JPanel southPanel = new JPanel();
		
		//setup as a single column jtable 
		//this makes it easy to add additional fields to differentiate users further 
		final String[] columnNames = {"Available SPCs"};
		final Object[][] data = new Object[mySPCList.size()][columnNames.length];
		for (int i = 0; i < mySPCList.size(); i++) {
			data[i][0] = mySPCList.get(i).myFirstName + " " + mySPCList.get(i).myLastName;
		}
		
		//set table properties
	    final DefaultTableModel theModel = new DefaultTableModel(data, columnNames);
		final JTable theTable = new JTable(theModel) {
			@Override
	        public boolean isCellEditable(final int theRow, final int theColumn) {
	        		return false;    
	        }
	    };
	    theTable.getTableHeader().setReorderingAllowed(false);
	    
	    final JScrollPane thePane = new JScrollPane(theTable);
	    
	    //setup ok and cancel buttons and actions
	    final JButton okButton = new JButton("Ok");
	    final JButton cancelButton = new JButton("Cancel");
	    okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final int theRow = theTable.getSelectedRow();
				if (theRow >= 0) {
					myUser.assignSPC(mySPCList.get(theRow), myPaper);
					myFrame.dispose();
				} else {
					JOptionPane.showMessageDialog(theTable, "Please select an SPC to assign.");
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
		myFrame.add(new JPanel(), BorderLayout.NORTH);
		myFrame.add(new JPanel(), BorderLayout.EAST);
		myFrame.add(new JPanel(), BorderLayout.WEST);
		myFrame.setVisible(true);
	}
}
