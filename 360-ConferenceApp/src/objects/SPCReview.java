package objects;

/**
 * Class representing an SPC review object in conference.
 * 
 * @author Jesse Bostic
 * @author James Nance
 * @version TCSS360 - Spring 2014
 *
 */
public class SPCReview {
	
	/**
	 * associated ids for this spc review
	 */
	public int myID, myUserID, myConferenceID, myPaperID;
	
	/**
	 * the spc's numerical rating of paper (1-5)
	 */
	public int myRating;
	
	/**
	 * the pc's numerical rating of paper (-1 reject, 1 accept, 0 undecided)
	 */
	public int myPCRating;
	
	/**
	 * the spc text recommendation to pc on paper
	 */
	public String myComment;
	
	/**
	 * No arg constructor for empty spcreview object.
	 * 
	 * @author Jesse Bostic
	 */
	public SPCReview() {
		this(0, 0, 0);
	}
	
	/**
	 * Constructor for new spcreview object with no rating or comment.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theUserID the spc
	 * @param theConferenceID the conf
	 * @param thePaperID the paper
	 */
	public SPCReview(int theUserID, int theConferenceID, int thePaperID) {
		this(0, theUserID, theConferenceID, thePaperID, 0, "", 0);
	}
	
	/**
	 * Constructor for spcreview that is underway (mostly for editing or viewing).
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theID spcreview id
	 * @param theUserID the user
	 * @param theConferenceID the conference 
	 * @param thePaperID the paper being reviewed
	 * @param theRating the spc rating
	 * @param theComment the spc comment
	 * @param thePCRating the pc rating
	 */
	public SPCReview(int theID, int theUserID, int theConferenceID, int thePaperID, 
				     int theRating, String theComment, int thePCRating) {
		myID = theID;
		myUserID = theUserID;
		myConferenceID = theConferenceID;
		myPaperID = thePaperID;
		myRating = theRating;
		myComment = theComment;
		myPCRating = thePCRating;
	}
	
	/**
	 * Returns whether passed object is equal to this spcreview object.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		boolean areEqual = false;
		if (o instanceof SPCReview) {
			SPCReview theRev = (SPCReview) o;
			if (this.myID == theRev.myID 
					&& this.myUserID == theRev.myUserID 
					&& this.myPaperID == theRev.myPaperID) {
				areEqual = true;
			}
		}
		return areEqual;
	}
	
	/**
	 * Constructs and returns a string representation of this spcreview object.
	 * 
	 * @author James Nance
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("my ID: ");
		sb.append(myID);
		sb.append(", my user ID: ");
		sb.append(myUserID);
		sb.append(", my conference ID: ");
		sb.append(myConferenceID);
		sb.append(", my paper ID: ");
		sb.append(myPaperID);
		sb.append(", my rating: ");
		sb.append(myRating);
		sb.append(", my comment: ");
		sb.append(myComment);
		sb.append(", my PC rating: ");
		sb.append(myPCRating);
		
		return sb.toString();
	}
}
