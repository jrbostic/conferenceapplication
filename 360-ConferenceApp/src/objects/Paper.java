package objects;

/**
 * Class representing a paper submitted to a conference.
 * 
 * @author Jesse Bostic
 * @author James Nance
 * @version TCSS360 - Spring 2014
 *
 */
public class Paper {
	
	/**
	 * the paper id
	 */
	public int myID;
	
	/**
	 * the paper's string fields (self explanatory)
	 */
	public String myAuthor, myTitle, myAbstract, myCategory, myPath;
	
	/**
	 * Constructs a paper object.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theID paper id
	 * @param theAuthor paper author
	 * @param theTitle paper title
	 * @param theAbstract paper abstract
	 * @param theCategory paper category
	 * @param thePath paper's original file location
	 */
	public Paper(final int theID, final String theAuthor, final String theTitle, 
				final String theAbstract, final String theCategory, final String thePath) {
		myID = theID;
		myAuthor = theAuthor;
		myTitle = theTitle;
		myAbstract = theAbstract;
		myCategory = theCategory;
		myPath = thePath;
	}
	
	/**
	 * Creates new paper with author determined.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theAuthor paper author
	 */
	public Paper(final String theAuthor) {
		myAuthor = theAuthor;
		myTitle = "";
		myAbstract = "";
		myCategory = "";
		myPath = "";
	}
	
	/**
	 * Creates a copy of passed paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper paper to be copied
	 */
	public Paper(final Paper thePaper) {
		myID = thePaper.myID;
		myAuthor = thePaper.myAuthor;
		myTitle = thePaper.myTitle;
		myAbstract = thePaper.myAbstract;
		myCategory = thePaper.myCategory;
		myPath = thePaper.myPath;
	}
	
	/**
	 * Creates a simple hashCode for this paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * (not currently used, but could be in future)
	 * 
	 * @return hashCode for this paper
	 */
	@Override
	public int hashCode() {
		return myTitle.hashCode() + myAuthor.hashCode() + myCategory.hashCode() + myAbstract.hashCode();
	}
	
	/**
	 * Returns a hash string for file naming in local directory.
	 * 
	 * @author Jesse Bostic
	 * 
	 * NOTE: (this could be changed at any time to alter hashing name convention, but all
	 * 	previously submitted paper files would need to be processed and renamed to new 
	 * 	hashString value--this should be done by going through the conference_paper table)
	 * 
	 * @return a string representing unique* hashed string
	 * 
	 */
	public String hashString() {
		return "" + myTitle.hashCode() + myAuthor.hashCode() + myCategory.hashCode() + myAbstract.hashCode() + myPath.hashCode();
	}
	
	/**
	 * Returns whether param object is equal to this object.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		boolean areEqual = false;
		if (o instanceof Paper) {
			Paper thePaper = (Paper) o;
			if (thePaper.myAbstract.equals(this.myAbstract) && thePaper.myAuthor.equals(this.myAuthor)	
					&& thePaper.myTitle.equals(this.myTitle) && thePaper.myCategory.equals(this.myCategory)
					&& thePaper.myPath.equals(this.myPath)) {
				areEqual = true;
			} 
				
		}
		return areEqual;
	}
	
	/**
	 * Constructs and returns string representation of this paper object.
	 * 
	 * @author James Nance
	 * 
	 * @return string representation for this paper
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("my ID: ");
		sb.append(myID);
		sb.append(", my author: ");
		sb.append(myAuthor);
		sb.append(", my title: ");
		sb.append(myTitle);
		sb.append(", my abstract: ");
		sb.append(myAbstract);
		sb.append(", my category: ");
		sb.append(myCategory);
		sb.append(", my path: ");
		sb.append(myPath);
		
		return sb.toString();
	}
}
