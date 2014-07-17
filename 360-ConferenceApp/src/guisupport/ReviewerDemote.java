package guisupport;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import roles.User;

//allows for selection and demotion of eligible reviewers

public class ReviewerDemote {
	private final JFrame myFrame;
	private List<User> myUsers;
	private User myCaller;
	
	public ReviewerDemote(final List<User> theUsers, final User theCaller) {
		myFrame = new JFrame();
		myUsers = theUsers;
		myCaller = theCaller;
		setup();
	}
	
	private void setup() {
		myFrame.setBounds(75, 75, 400, 300);
		
		myFrame.setTitle("Demote Reviewer");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String[] userList = new String[myUsers.size()];
		for (int i = 0; i < myUsers.size(); i++) {
			userList[i] = myUsers.get(i).getFullName();
		}
		
		final JList<String> list = new JList<String>(userList);
		final JScrollPane scrollPane = new JScrollPane(list);
		
		myFrame.add(scrollPane, BorderLayout.CENTER);
		
		final JButton demoteButton = new JButton("Demote");
		demoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = list.getSelectedIndex();
				User user = myUsers.get(selectedIndex);
				user.setRole(2);
				user.setConference(myCaller.getConferenceID());
				user.demote();
				myFrame.dispose();
			}	
		});
		
		myFrame.add(demoteButton, BorderLayout.SOUTH);
		myFrame.setVisible(true);
	}
}
