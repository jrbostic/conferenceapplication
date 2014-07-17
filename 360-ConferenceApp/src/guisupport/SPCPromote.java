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

//screen to select promotions to spc status from eligible users

public class SPCPromote {
	
	private final JFrame myFrame;
	private List<User> myUsers;
	private User myCaller;
	
	public SPCPromote(final List<User> theUsers, final User theCaller) {
		myFrame = new JFrame();
		myUsers = theUsers;
		myCaller = theCaller;
		setup();
	}
	
	private void setup() {
		myFrame.setBounds(75, 75, 400, 300);
		
		myFrame.setTitle("Promote SPC");
		myFrame.setResizable(false);
		myFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String[] userList = new String[myUsers.size()];
		for (int i = 0; i < myUsers.size(); i++) {
			userList[i] = myUsers.get(i).getFullName();
		}
		
		final JList<String> list = new JList<String>(userList);
		final JScrollPane scrollPane = new JScrollPane(list);
		
		myFrame.add(scrollPane, BorderLayout.CENTER);
		
		final JButton promoteButton = new JButton("Promote");
		promoteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int selectedIndex = list.getSelectedIndex();
				User user = myUsers.get(selectedIndex);
				user.setRole(3);
				user.setConference(myCaller.getConferenceID());
				user.promote();
				myFrame.dispose();
			}	
		});
		
		myFrame.add(promoteButton, BorderLayout.SOUTH);
		myFrame.setVisible(true);
	}
}
