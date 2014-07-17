package objects;

/**
 * Class representing conference object.
 * 
 * (should eventually evolve to hold conference description and deadlines)
 * 
 * @author Jesse Bostic
 * @author James Nance
 * @version TCSS360 - Spring 2014
 *
 */
public class Conference {
	
	/**
	 * the conference id
	 */
	public final int myID;
	
	/**
	 * the conference name
	 */
	public final String myName;
	
	/**
	 * Constructs new conference object.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theId conf id
	 * @param theName conf name
	 */
	public Conference (int theId, String theName) {
		myID = theId;
		myName = theName;
	}
	
	/**
	 * Returns whether passed object is equal to this conference.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		boolean areEqual = false;
		if (o instanceof Conference) {
			Conference theConf = (Conference) o;
			if (this.myID == theConf.myID && this.myName.equals(theConf.myName)) {
				areEqual = true;
			}
		}
		return areEqual;
	}
	
	/**
	 * Returns string repsentation of this conference.
	 * 
	 * @author James Nance
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("my ID: ");
		sb.append(myID);
		sb.append(", my name: ");
		sb.append(myName);
		
		return sb.toString();
	}
}
