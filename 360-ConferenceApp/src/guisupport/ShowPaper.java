package guisupport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.*;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import objects.Paper;
import roles.User;

/**
 * Handles uneditable viewing of a paper in conference.
 * 
 * @author Jesse Bostic
 * @version TCSS360 - Spring 2014
 *
 */
public class ShowPaper {
	
	/**
	 * the file chooser for downloading process
	 */
	private static final JFileChooser CHOOSER = new JFileChooser();
	
	/**
	 * the window's frame
	 */
	private final JFrame myFrame;
	
	/**
	 * the paper being viewed
	 */
	private final Paper myPaper;
	
	/**
	 * the current user viewing (mainly for access to current conference)
	 */
	private final User myUser;
	
	/**
	 * Creates new paper viewing window.
	 * 
	 * @param paper the paper to be viewed
	 * @param theUser the current user
	 */
	public ShowPaper(final Paper paper, final User theUser) {
		myFrame = new JFrame();
		myPaper = paper;
		myUser = theUser;
		createWindow();
	}
	
	/**
	 * Creates the actual window.
	 */
	private void createWindow() {
		
		myFrame.setBounds(75, 75, 400, 300);
		myFrame.setTitle("View Paper");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		final JTextArea paperField = new JTextArea();
		final JScrollPane thePane = new JScrollPane(paperField);
		paperField.setEditable(false);
		paperField.setLineWrap(true);
		paperField.setWrapStyleWord(true);
		final StringBuilder theInfo = new StringBuilder();
		final String fileName = myPaper.myPath.substring(myPaper.myPath.lastIndexOf('\\')+1);
		
		theInfo.append("Paper Title: ");
		theInfo.append(myPaper.myTitle);
		theInfo.append("\n\nAuthor: ");
		theInfo.append(myPaper.myAuthor);
		theInfo.append("\n\nCategory: ");
		theInfo.append(myPaper.myCategory);
		theInfo.append("\n\nAbstract: ");
		theInfo.append(myPaper.myAbstract);
		theInfo.append("\n\nFile Name: ");
		theInfo.append(fileName);
		
		paperField.setText(theInfo.toString());
		
		//setup download button and action
		//gets hashed filename and stores in local directory
		// under user selected name; if chosen name is not
		//properly typed then type is appended to new file name
		//(overwrites existing files w/o warning)
		final JButton downloadButton = new JButton("Download");
		downloadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				CHOOSER.setSelectedFile(new File(fileName));
				int choice = CHOOSER.showSaveDialog(myFrame);
				if (choice == JFileChooser.APPROVE_OPTION) {
					File chosenFile = CHOOSER.getSelectedFile();
					String type = myPaper.myPath.substring(myPaper.myPath.lastIndexOf('.'));
					File storedFile = new File(System.getProperty("user.dir") + "\\Papers\\" + myPaper.hashString() 
																	+ "-" + myUser.getConferenceID() + type);
					try {
						Files.copy(storedFile.toPath(), chosenFile.toPath(), REPLACE_EXISTING);
						if (chosenFile.getName().indexOf(type) == -1) {
							String cfPath = chosenFile.getPath();
							Files.copy(chosenFile.toPath(), 
										new File(cfPath + type).toPath(), 
										REPLACE_EXISTING);
							Files.delete(chosenFile.toPath());
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}		
		});
		
		//setup done button and dispose action
		final JButton doneButton = new JButton("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				myFrame.dispose();
			}	
		});
		
		final JPanel southPanel = new JPanel();
		southPanel.add(downloadButton);
		southPanel.add(doneButton);
		
		myFrame.add(thePane, BorderLayout.CENTER);
		myFrame.add(southPanel, BorderLayout.SOUTH);
		myFrame.add(new JPanel(), BorderLayout.NORTH);
		myFrame.add(new JPanel(), BorderLayout.EAST);
		myFrame.add(new JPanel(), BorderLayout.WEST);
		myFrame.setVisible(true);
	}

}
