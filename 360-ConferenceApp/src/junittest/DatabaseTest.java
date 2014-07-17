package junittest;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.List;

import objects.Conference;
import objects.Paper;

import org.junit.Before;
import org.junit.Test;

import access.Database;
import roles.User;

/**
 * A class to test the Database class.
 * 
 * @author Khoi
 */
public class DatabaseTest {

	/**
	 * Objects that help with the test.
	 */
	private User myUser;
	private User myUser2;
	private Conference conf;
	private Paper myPaper;

	/**
	 * Setup the preconditions for the test.
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		myUser = new User(14, "Thomas", "Alexander", "Thomas.Alexander@potlatchcorp.com", "Thomas.Alexander@potlatchcorp.com", User.DEFAULT_PASSWORD);
		myUser2 = Database.login("Roger.Harden@kla-tencor.com", "password");
		myUser2.setConference(10);
		myUser2.setRole(1);
		conf = new Conference(10, "Learning At Scale");
		myPaper = new Paper(0, "Roger", "title", "abstract", "category", "path");
	}

	/**
	 * Test when user log in using correct password.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testLogin1() throws SQLException {
		User temp = Database.login("Thomas.Alexander@potlatchcorp.com", "password");
		assertEquals("login", temp.toString(), myUser.toString());
	}

	/**
	 * Test when user log in using incorrect password.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testLogin2() throws SQLException {
		User temp = Database.login("Thomas.Alexander@potlatchcorp.com", "wrong pass");
		assertNull(temp);
	}

	/**
	 * Test for expected user roles of specific user.
	 */
	@Test
	public void testGetUserRolesAtConference() {
		int[] temp = Database.getUserRolesAtConference(10, 17);
		int[] expect = new int[4];
		expect[1] = 1;
		
		assertEquals("user roles at conf", temp[0], expect[0]);
		assertEquals("user roles at conf", temp[1], expect[1]);
		assertEquals("user roles at conf", temp[2], expect[2]);
		assertEquals("user roles at conf", temp[3], expect[3]);
	}

	/**
	 * Test and get a list of available conferences.
	 */
	@Test
	public void testGetConferences() {
		List<Conference> temp = Database.getConferences();
		
		assertEquals("get conferences", temp.get(0).myName, "Object-Oriented Programming, Systems, Languages & Applications");
		assertEquals("get conferences", temp.get(0).myID, 4);
		
		assertEquals("get conferences", temp.get(1).myName, "International Conference for Mechanical Engineering Education");
		assertEquals("get conferences", temp.get(1).myID, 8);

		assertEquals("get conferences", temp.get(2).myName, "Pacific Northwest Conference on Software Engineering");
		assertEquals("get conferences", temp.get(2).myID, 9);

		assertEquals("get conferences", temp.get(3).myName, "Learning At Scale");
		assertEquals("get conferences", temp.get(3).myID, 10);
	}
	
	/**
	 * Try to demote a SPC.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testDemoteSPC() {
		Database.demoteSPC(myUser2, conf);
		
		int[] roles = Database.getUserRolesAtConference(10,  89);
		assertEquals("no longer SPC", roles[2], 0);
	}

	/**
	 * Try to promote a SPC.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testPromoteToSPC() {
		Database.promoteToSPC(myUser2, conf);
		
		int[] roles = Database.getUserRolesAtConference(10,  89);
		assertEquals("is SPC", roles[2], 1);
	}

	/**
	 * Try to promote a user to reviewer.
	 */
	@Test
	public void testPromoteToReviewer() {
		Database.promoteToReviewer(myUser2, conf);
		
		int[] roles = Database.getUserRolesAtConference(10,  89);
		assertEquals("is Reviewer", roles[1], 1);
	}

	/**
	 * Try to demote a reviewer from his/her role.
	 */
	@Test
	public void testDemoteReviewer() {
		Database.demoteReviewer(myUser2, conf);
		
		int[] roles = Database.getUserRolesAtConference(10,  89);
		assertEquals("no longer Reviewer", roles[1], 0);
	}

	/**
	 * Try to get password if user forget password.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void testGetPassword() throws SQLException {
		String pass = Database.getPassword("Thomas.Alexander@potlatchcorp.com");
		
		User temp = Database.login("Thomas.Alexander@potlatchcorp.com", pass);
		assertEquals("login", temp.toString(), myUser.toString());
	}

	/**
	 * Try to submit a paper to a conference.
	 */
	@Test
	public void testCommitPaper() {
		Database.commitPaper(myPaper, myUser2);
		List<Paper> papers = Database.getPapers(myUser2);
		assertEquals("commit paper", papers.get(papers.size() - 1), myPaper);
	}
	
	/**
	 * Try and remove a user's submitted paper.
	 */
	@Test
	public void testRemovePaper() {
		List<Paper> papers = Database.getPapers(myUser2);
		Database.removePaper(myUser2, papers.get(papers.size() - 1));
			
		if (papers.size() > 0) {
			assertEquals("number of paper", papers.size(), Database.getPapers(myUser2).size() + 1);
		} else {
			assertEquals("number of paper", papers.size(), Database.getPapers(myUser2).size());
		}
	}
}