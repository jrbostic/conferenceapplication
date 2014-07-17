package junittest;

import static org.junit.Assert.*;
import objects.SPCReview;

import org.junit.Before;
import org.junit.Test;

public class SPCReviewTest {

	private SPCReview mySPCReview; 
	
	private final int myID = 91;
	private final int myUserID = 27;
	private final int myConferenceID = 11;
	private final int myPaperID = 82;
	private final int myRating = 3;
	private final String myComment = "just ... a test";
	private final int myPCRating = 4;
	
    
	@Before
	public void setUp() throws Exception {
		mySPCReview = new SPCReview(myID, myUserID, myConferenceID, myPaperID, myRating, myComment, myPCRating);
	}

	@Test
	public void testSPCReview() {
		assertEquals("ID", mySPCReview.myID, myID);
		assertEquals("Paper ID", mySPCReview.myUserID, myUserID);
		assertEquals("User ID", mySPCReview.myConferenceID, myConferenceID);
		assertEquals("Conf ID", mySPCReview.myPaperID, myPaperID);
		assertEquals("Rating", mySPCReview.myRating, myRating);
		assertEquals("Comment", mySPCReview.myComment, myComment);
		assertEquals("PC Rating", mySPCReview.myPCRating, myPCRating);
	}

	@Test
	public void testToString() {
		assertEquals("toString", mySPCReview.toString(), "my ID: 91, my user ID: 27, "
				+ "my conference ID: 11, my paper ID: 82, my rating: 3, my comment: just ... a test, "
				+ "my PC rating: 4");
		
		
	}

}
