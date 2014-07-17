package junittest;

import static org.junit.Assert.*;
import objects.Paper;

import org.junit.Before;
import org.junit.Test;

public class PaperTest {
	
	private Paper myPaper;
	
	private Paper myOtherPaper;
	
	
	private final int myID = 17;
	private final String myAuthor = "Khoi Le";
	private final String myTitle = "Khoi's Test Paper";
	private final String myAbstract = "This is a test";
	private final String myCategory = "test, just test";
	private final String myPath = "Somewhere in THE cl0ud";

	@Before
	public void setUp() throws Exception {
		myPaper = new Paper(myID, myAuthor, myTitle, myAbstract, myCategory, myPath);
		myOtherPaper = new Paper(myAuthor);
	}

	@Test
	public void testPaperIntStringStringStringStringString() {
		assertEquals("ID", myPaper.myID, 17);
		assertEquals("Author", myPaper.myAuthor, "Khoi Le");
		assertEquals("Title", myPaper.myTitle, "Khoi's Test Paper");
		assertEquals("Abstract", myPaper.myAbstract, "This is a test");
		assertEquals("Category", myPaper.myCategory, "test, just test");
		assertEquals("Path", myPaper.myPath, "Somewhere in THE cl0ud");
	}

	@Test
	public void testPaperString() {
		assertEquals("ID", myOtherPaper.myID, 0);
		assertEquals("Author", myOtherPaper.myAuthor, "Khoi Le");
		assertEquals("Title", myOtherPaper.myTitle, "");
		assertEquals("Abstract", myOtherPaper.myAbstract, "");
		assertEquals("Category", myOtherPaper.myCategory, "");
		assertEquals("Path", myOtherPaper.myPath, "");
	}

	@Test
	public void testToString1() {
		assertEquals("toString", myPaper.toString(), "my ID: 17, my author: Khoi Le, "
				+ "my title: Khoi's Test Paper, my abstract: This is a test, my category: test, just test, "
				+ "my path: Somewhere in THE cl0ud");
		
		
	}
	
	@Test
	public void testToString2() {
		assertEquals("toString", myOtherPaper.toString(), "my ID: 0, my author: Khoi Le, "
				+ "my title: , my abstract: , my category: , "
				+ "my path: ");
		
		
	}

}
