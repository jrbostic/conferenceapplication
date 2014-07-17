package objects;

/**
 * Class representing a review object in conference.
 * 
 * @author Jesse Bostic
 * @author James Nance
 * @version TCSS360 - Spring 2014
 *
 */
public class Review {
	
	/**
	 * array of the questions on the review form
	 */
	public static final String[] REVIEW_QUESTIONS = new String[] { 
		"Can the content be directly applied by classroom instructors or curriculum designers?",
		"Does the work appeal to a broad readership interested in engineering education or is it narrowly specialized?",
		"Does the work address a significant problem?",
		"Does the author build upon relevant references and bodies of knowledge?",
		"If a teaching intervention is reported, is it adequately evaluated in terms of its impact on learning in actual use?",
		"Does the author use methods appropriate to the goals, both for the instructional intervention and the evaluation of impact on learning?",
		"Did the author provide sufficient detail to replicate and evaluate?",
		"Is the paper clearly and carefully written?",
		"Does the paper adhere to accepted standards of style, usage, and composition?",
		"Summary Rating:"};
	
	/**
	 * the relevant ids for a review object
	 */
	public int myID, myPaperID, myUserID, myConferenceID;
	
	/**
	 * array of numerical ratings (1-5) correlating to REVIEW_QUESTIONS
	 */
	public final int[] myRatings;
	
	/**
	 * the comment
	 */
	public  String myComment;
	
	/**
	 * Creates new review.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaperID paper id
	 * @param theUserID user id
	 * @param theConferenceID conference id
	 */
	public Review(final int thePaperID, final int theUserID, final int theConferenceID) {
		this(0, thePaperID, theUserID, theConferenceID, new int[REVIEW_QUESTIONS.length], "");
	}
	
	/**
	 * Creates new review (mostly for editing or viewing).
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theID review id
	 * @param thePaperID paper id
	 * @param theUserID user id
	 * @param theConferenceID conf id
	 * @param theRatings the ratings array
	 * @param theComment the comment
	 */
	public Review(final int theID, final int thePaperID, final int theUserID, 
				  final int theConferenceID, final int[] theRatings, final String theComment) {
		myID = theID;
		myPaperID = thePaperID;
		myUserID = theUserID;
		myConferenceID = theConferenceID;
		myRatings = theRatings;
		myComment = theComment;
	}
	
	/**
	 * Returns whether passed object is equal to this review object.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		boolean areEqual = false;
		if (o instanceof Review) {
			Review theRev = (Review) o;
			if (this.myConferenceID == theRev.myConferenceID 
					&& this.myPaperID == theRev.myPaperID
					&& this.myUserID == theRev.myUserID) {
				areEqual = true;
			}
		}
		return areEqual;
	}
	
	/**
	 * Constructs and returns string representation of this review object.
	 * 
	 * @author James Nance
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{my ID: ");
		sb.append(myID);
		sb.append(", my paper ID: ");
		sb.append(myPaperID);
		sb.append(", my user ID: ");
		sb.append(myUserID);
		sb.append(", my conference ID: ");
		sb.append(myConferenceID);
		sb.append(", my rating: ");
		sb.append(", the comment: ");
		sb.append(myComment);
		sb.append("}");

		return sb.toString();
	}
}
