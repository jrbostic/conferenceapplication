package guisupport;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import objects.Paper;
import roles.User;

/**
 * Moderates new paper submission and paper updates with GUI.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class PaperSession {
	
	/**
	 * file chooser for opening dialog to get file path
	 */
	private static final JFileChooser CHOOSER = new JFileChooser();
	
	/**
	 * the current user
	 */
	private final User myUser;
	
	/**
	 * the new paper
	 */
	private final Paper myPaper;
	
	/**
	 * a copy of the initial paper state (for determining change)
	 */
	private final Paper myPaperCopy;
	
	/**
	 * Constructs new paper submission session.
	 * 
	 * @param theUser the current user
	 */
	public PaperSession(final User theUser) {
		this(theUser, new Paper(theUser.getFullName()));
	}
	
	/**
	 * Constructs new paper edit session.
	 * 
	 * @param theUser the current user
	 * @param thePaper the existing paper to edit
	 */
	public PaperSession(final User theUser, final Paper thePaper) {
		myUser = theUser;
		myPaper = thePaper;
		myPaperCopy = new Paper(thePaper);
		createWindow();
	}
	
	/**
	 * Creates the actual window for paper session.
	 */
	private void createWindow() {
		
		final JFrame theFrame = new JFrame();
		theFrame.setBounds(100, 100, 475, 375);
		theFrame.setResizable(false);
		theFrame.setAlwaysOnTop(true);
		theFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		theFrame.setTitle("Submit A Paper");
		
		final JPanel centerPanel = new JPanel(new GridLayout(0, 1));

		//construct labels and textfields for centerPanel
		final JLabel authorLabel = new JLabel("Author");
		final JTextField authorField = new JTextField(myPaper.myAuthor);
		authorField.setColumns(40);
		final JPanel authorPanel = new JPanel();
		authorPanel.add(authorLabel);
		authorPanel.add(authorField);
		
		final JLabel titleLabel = new JLabel("Title");
		final JTextField titleField = new JTextField(myPaper.myTitle);
		titleField.setColumns(40);
		final JPanel titlePanel = new JPanel();
		titlePanel.add(titleLabel);
		titlePanel.add(titleField);
		
		final JLabel categoryLabel = new JLabel("Category");	
		final JTextField categoryField = new JTextField(myPaper.myCategory);
		categoryField.setColumns(40);
		final JPanel categoryPanel = new JPanel();
		categoryPanel.add(categoryLabel);
		categoryPanel.add(categoryField);
		
		final JLabel abstractLabel = new JLabel("Abstract");
		final JTextArea abstractField = new JTextArea(myPaper.myAbstract);
		final JScrollPane abstractScroller = new JScrollPane(abstractField);
		abstractField.setLineWrap(true);
		abstractField.setWrapStyleWord(true);
		abstractField.setColumns(35);
		final JPanel abstractNorthPanel = new JPanel();
		abstractNorthPanel.add(abstractLabel);
		final JPanel abstractPanel = new JPanel(new BorderLayout());
		abstractPanel.add(abstractNorthPanel, BorderLayout.NORTH);
		abstractPanel.add(abstractScroller, BorderLayout.CENTER);
		
		//setup file selected text and button for uploading
		String tempSelection = "No File Selected";
		if (!myPaper.myPath.isEmpty()) {
			tempSelection = new File(myPaper.myPath).getName();
		}
		final JLabel uploadLabel = new JLabel(tempSelection);
		final JButton uploadButton = new JButton("Upload File");
		uploadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int choice = CHOOSER.showOpenDialog(theFrame);
				if (choice == JFileChooser.APPROVE_OPTION) {
					final File theFile = CHOOSER.getSelectedFile();
					myPaper.myPath = theFile.getPath();
					uploadLabel.setText(theFile.getName());
					uploadLabel.repaint();
				}
			}
		});
		final JPanel uploadPanel = new JPanel();
		uploadPanel.add(uploadLabel);
		uploadPanel.add(uploadButton);
		
		//add all subpanels to central panel
		centerPanel.add(authorPanel);
		centerPanel.add(titlePanel);
		centerPanel.add(categoryPanel);
		centerPanel.add(abstractPanel);
		centerPanel.add(uploadPanel);
		
		// construct panel and buttons for borderlayout south region
		final JPanel southPanel = new JPanel();
		
		//setup submit button and logic in order submit or update a paper
		final JButton submitButton = new JButton("Submit Paper");
		submitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				myPaper.myAuthor = authorField.getText().trim().replaceAll("'", "''");
				myPaper.myTitle = titleField.getText().trim().replaceAll("'", "''");
				myPaper.myCategory = categoryField.getText().trim().replaceAll("'", "''");
				myPaper.myAbstract = abstractField.getText().trim().replaceAll("'", "''");
				
				if (!myPaper.myAuthor.isEmpty() && !myPaper.myTitle.isEmpty()
						&& !myPaper.myCategory.isEmpty() && !myPaper.myAbstract.isEmpty()
						&& myPaper.myPath != "") {
					
					//if actual paper has changed, create new file and delete old
					// else rehash the file name for paper based on new field values
					if (myPaper.myPath != myPaperCopy.myPath) {
						storeFile();
						deleteOld();
					} else {
						rehashFile();
					}
					
					myUser.commitPaper(myPaper);
					JOptionPane.showMessageDialog(theFrame, "Paper submitted successfully!!!", "Submission Success", 
													JOptionPane.INFORMATION_MESSAGE); 
					theFrame.dispose();
					
				} else {
					
					JOptionPane.showMessageDialog(theFrame, "Ooops!  You must complete all fields to submit paper!", 
														"Submission Not Complete", JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		
		//setup cancel button and action
		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
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
		
		// add all panels to frame in respective regions
		theFrame.add(centerPanel, BorderLayout.CENTER);
		theFrame.add(southPanel, BorderLayout.SOUTH);
		theFrame.add(new JPanel(), BorderLayout.WEST); // these two add space to the side borders
		theFrame.add(new JPanel(), BorderLayout.EAST);
		
		theFrame.setVisible(true);

	}
	
	/**
	 * Deletes the old file referenced by myPaperCopy global.
	 */
	protected void deleteOld() {
		if (myPaperCopy.myPath != "") {
			String type = myPaperCopy.myPath.substring(myPaperCopy.myPath.lastIndexOf('.'));
			final File oldFile = new File(System.getProperty("user.dir") + "\\Papers\\" + myPaperCopy.hashString()
									+ "-" + myUser.getConferenceID() + type);
			oldFile.delete();
		}
	}
	
	/**
	 * Rehashes myPaper filename based on new field values.
	 */
	protected void rehashFile() {
		String type = myPaper.myPath.substring(myPaper.myPath.lastIndexOf('.'));
		final File original = new File(System.getProperty("user.dir") + "\\Papers\\" + myPaperCopy.hashString()
																+ "-" + myUser.getConferenceID() + type);
		final File replace = new File(System.getProperty("user.dir") + "\\Papers\\" + myPaper.hashString()
																+ "-" + myUser.getConferenceID() + type);
		try {
			Files.copy(original.toPath(), replace.toPath(), REPLACE_EXISTING);
			original.delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stores paper in project with file name of hashed value.
	 */
	private void storeFile() {
		File original = new File(myPaper.myPath);
		String type = myPaper.myPath.substring(myPaper.myPath.lastIndexOf('.'));
		File theCopy = new File(System.getProperty("user.dir") + "\\Papers\\" + myPaper.hashString() 
															+ "-" + myUser.getConferenceID() + type);
		try {
			Files.copy(original.toPath(), theCopy.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
