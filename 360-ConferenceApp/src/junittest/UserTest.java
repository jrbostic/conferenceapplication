package junittest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import roles.User;

public class UserTest {
	
	private User myUser;
	private User mySecondUser;
	@Before
	public void setUp() throws Exception {
		myUser = new User(14, "Thomas", "Alexander", "Thomas.Alexander@potlatchcorp.com", "Thomas.Alexander@potlatchcorp.com", "password");
		mySecondUser = new User(11, "Khoi", "Le", "khoile1121@gmail.com", "khoile1121@gmail.com", User.DEFAULT_PASSWORD); //
		
	}

	@Test
	public void testUserIntStringStringStringStringString() {
		assertEquals("ID", myUser.myID, 14);
		assertEquals("first n", myUser.myFirstName, "Thomas");
		assertEquals("last n", myUser.myLastName, "Alexander");
		assertEquals("email", myUser.myEmail, "Thomas.Alexander@potlatchcorp.com");
		assertEquals("username", myUser.myUsername, "Thomas.Alexander@potlatchcorp.com");
		assertEquals("password", myUser.myPassword, "password");
	}

	@Test
	public void testUserIntStringStringString() {
		assertEquals("ID", mySecondUser.myID, 11);
		assertEquals("first n", mySecondUser.myFirstName, "Khoi");
		assertEquals("last n", mySecondUser.myLastName, "Le");
		assertEquals("email", mySecondUser.myEmail, "khoile1121@gmail.com");
		assertEquals("username", mySecondUser.myUsername, "khoile1121@gmail.com");
		assertEquals("password", mySecondUser.myPassword, "password");
	}

	@Test
	public void testSetConference() {
		mySecondUser.setConference(22);
		assertEquals("set conference", mySecondUser.getConferenceID(), 22);
	}

	@Test
	public void testGetConferenceID() {
		assertEquals("get conference ID", myUser.getConferenceID(), 0);
	}

	@Test
	public void testSetRole() {
		mySecondUser.setRole(3);
		assertEquals("set role", mySecondUser.getRoleID(), 3);
	}

	@Test
	public void testGetRoleID() {
		assertEquals("get role ID", myUser.getRoleID(), 0);
	}



	@Test
	public void testToString() {
		assertEquals("toString", myUser.toString(), "ID = 14\nName = Thomas Alexander\n"
				+ "Email = Thomas.Alexander@potlatchcorp.com\n");
		

	}

}
