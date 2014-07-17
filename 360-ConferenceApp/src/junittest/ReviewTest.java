package junittest;

import static org.junit.Assert.*;
import objects.Review;

import org.junit.Before;
import org.junit.Test;

public class ReviewTest {

	private Review myReview; 
	private final int myID = 91;
	private final int myPaperID = 72;
	private final int myUserID = 27;
	private final int myConferenceID = 11;
	private final int[] myRating = {3, 3, 4, 5, 3, 2, 1, 4, 2, 5};
	private final String myComment = "just ... a test";
	
	@Before
	public void setUp() throws Exception {
		myReview = new Review(myID, myPaperID, myUserID, myConferenceID, myRating, myComment);
	}

	@Test
	public void testReview() {
		assertEquals("ID", myReview.myID, myID);
		assertEquals("Paper ID", myReview.myPaperID, myPaperID);
		assertEquals("User ID", myReview.myUserID, myUserID);
		assertEquals("Conf ID", myReview.myConferenceID, myConferenceID);
		for (int i = 0; i < myReview.myRatings.length; i++) {
			assertEquals("Rating", myReview.myRatings[i], myRating[i]);
		}
		assertEquals("Comment", myReview.myComment, myComment);
	}

	@Test
	public void testToString() {
		assertEquals("toString", myReview.toString(), "my ID: 91, my paper ID: 72, "
				+ "my user ID: 27, my conference ID: 11, my rating: , the comment: just ... a test");
		
		
	}

}
