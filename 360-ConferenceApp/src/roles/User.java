package roles;

import objects.SPCReview;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import access.Database;
import objects.Conference;
import objects.Paper;
import objects.Review;

/**
 * User object that mediates GUI access to database.
 * 
 * @author Jesse Bostic
 * @author John Jackson
 * @version TCSS360 - Spring 2014
 *
 */
public class User {
	
	/**
	 * the default password for all accounts
	 */
	public static final String DEFAULT_PASSWORD = "password";
	
	/**
	 * array of current conference and role ids
	 */
	private final int[] myConferenceAndRole;
	
	/**
	 * the db id for this user
	 */
	public int myID;
	
	/**
	 * the user's username
	 */
	public final String myUsername;
	
	/**
	 * the user's first name
	 */
	public final String myFirstName;
	
	/**
	 * the user's last name
	 */
	public final String myLastName;
	
	/**
	 * the user's email
	 */
	public String myEmail;
	
	/**
	 * the user's password
	 */
	public String myPassword;
	
	/**
	 * No arg constructor for a blank user.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	public User() {
		this(0, "NONE", "", "", "", "");
	}
	
	/**
	 * The full-field constructor for user.
	 * 
	 * @author John Jackson
	 * 
	 * @param user_id user id
	 * @param first_name user first name
	 * @param last_name user last name
	 * @param email user email
	 * @param username user login username
	 * @param password user login password
	 */
	public User(final int user_id, final String first_name, final String last_name, 
				final String email, final String username, final String password) {
		myConferenceAndRole = new int[] {0, 0};
		myID = user_id;
		myFirstName = first_name;
		myLastName = last_name;
		myEmail = email;
		myUsername = username;
		myPassword = password;
	}
	
	/**
	 * Returns full name of this user.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return user's full name
	 */
	public String getFullName() {
		return myFirstName + " " + myLastName;
	}
	
	/**
	 * Set's current conference.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theConferenceID the conference selected
	 */
	public void setConference(final int theConferenceID) {
		myConferenceAndRole[0] = theConferenceID;
	}
	
	/**
	 * Gets current conference
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return selected conference id
	 */
	public int getConferenceID() {
		return myConferenceAndRole[0];
	}
	
	/**
	 * Sets the users Role ID.  1 = Author, 2 = Reviewer, 3 = Subprogram Chair, 4 = Program Chair
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theRoleID integer representing role of user used in determining access to the database
	 */
	public void setRole(final int theRoleID) {
		myConferenceAndRole[1] = theRoleID;
	}
	
	/**
	 * Gets current selected role value.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return current role id
	 */
	public int getRoleID() {
		return myConferenceAndRole[1];
	}
	
	/**
	 * Gets role information for selected conference.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return array of selected roles (1s for holdsRole, 0s for !holdRole)
	 */
	public int[] getMyRoles() {
		return Database.getUserRolesAtConference(myConferenceAndRole[0], myID);
	}
	
	/**
	 * Updates user email and password fields.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	public void updateUser() {
		Database.updateUser(this, this.myPassword, this.myEmail);
	}
	
	
	//general view functions
	
	/**
	 * Adds an observer to Database.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theObserver the observer to add
	 */
	public void addDBObserver(final Observer theObserver) {
		Database.OBSERVABLE.addObserver(theObserver);
	}
	
	/**
	 * Get's list of available conferences.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return all conferences
	 */
	public List<Conference> getConferences() {
		return Database.getConferences();
	}
	
	/**
	 * Get's list of papers based on user.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return conference and role specific paper list
	 */
	public List<Paper> getPapers() {
		return Database.getPapers(this);
	}
	
	/**
	 * Get's paper status that allows for easy status control of gui operations.
	 * (eventually, this feature would be restructured to enforce the deadlines as well)
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the current paper
	 * @return paper status for each stage of review.
	 */
	public int getIntPaperStatus(final Paper thePaper) {
		return Database.getIntPaperStatus(thePaper);
	}
	
	//author view methods
	
	/**
	 * Commits new or edited paper to db.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper paper to be committed
	 */
	public void commitPaper(final Paper thePaper) {
		Database.commitPaper(thePaper, this);
	}
	
	/**
	 * Deletes paper from db.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper paper to be deleted
	 */
	public void deletePaper(final Paper thePaper) {
		Database.removePaper(this, thePaper);
	}
	
	/**
	 * Whether author has submitted max papers.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return true if max reached, false otherwise
	 */
	public boolean hasMaxPapers() {
		return !Database.checkAuthorLimit(myID, myConferenceAndRole[0]);
	}
	
	// program chair view methods
	
	/**
	 * Gets list of spcs available for paper assignment.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper needing spc
	 * @return list of available spcs
	 */
	public List<User> getSPCList(final Paper thePaper) {
		return Database.getAvailableSPCs(thePaper);
	}
	
	/**
	 * Assigns and spc to a paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theSPC the spc to be assigned
	 * @param thePaper the paper to be assigned
	 */
	public void assignSPC(final User theSPC, final Paper thePaper) {
		Database.assignSPC(this, theSPC, thePaper);
	}
	
	/**
	 * Gets the assigned spc name for a paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper assigned
	 * @return the string representation of assigned spc
	 */
	public String getSPCName(final Paper thePaper) {
		User theUser =  Database.getSPCByPaper(thePaper);
		return theUser.getFullName();
	}
	
	/**
	 * Sets pc's decision to accept/reject a paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper to be decided
	 * @param isApproved true for approval, false for rejection
	 */
	public void acceptRejectPaper(final Paper thePaper, final boolean isApproved) {
		Database.makeDecision(thePaper, isApproved);
	}
	
	/**
	 * Returns string representation of current paper status.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper whose status is sought
	 * @return the status string
	 */
	public String getPaperStatus(final Paper thePaper) {
		return Database.getPaperStatus(thePaper);
	}
	
	/**
	 * Get a all users eligible for spc promotion in conference.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return list of those eligible for promotion to spc
	 */
	public List<User> getPotentialSPCs() {
		return Database.getPotentialSPCs(new Conference(this.getConferenceID(), "")); 
	}
	
	/**
	 * Get list of current spcs (ie eligible for demotion).
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return list of those eligible for demotion from spc
	 */
	public List<User> getCurrentSPCs() {
		return Database.getCurrentSPCs(new Conference(this.getConferenceID(), ""));
	}
	
	//spc methods
	
	/**
	 * List of reviews for this paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper for which reviews are sought
	 * @return list of reviews (complete or incomplete)
	 */
	public List<Review> getReviews(final Paper thePaper) {
		return Database.getReviews(thePaper);
	}
	
	/**
	 * The spcreview for this paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper for which spcreview is sought
	 * @return the spcreview for paper
	 */
	public SPCReview getSPCReview(final Paper thePaper) {
		return Database.getSPCReview(thePaper.myID);
	}
	
	/**
	 * List of reviewers available to be assigned to this paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper paper for which reviewers are sought
	 * @return list of reviewers available for this paper
	 */
	public List<User> getReviewerList(final Paper thePaper) {
		return Database.getAvailableReviewers(thePaper);
	}
	
	/**
	 * Assigns reviewer to a paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theNewRev the reviewer to add to paper
	 * @param theOldRev the previous reviewer (can be empty default user)
	 * @param thePaper the paper to add reviewer to
	 */
	public void assignReviewer(final User theNewRev, final User theOldRev, final Paper thePaper) {
		Database.assignReviewer(this, theNewRev, theOldRev, thePaper);
	}
	
	/**
	 * Gets a list of assigned reviewers for a given paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper 
	 * @return the list of assigned reviewers (padded with empty users)
	 */
	public List<User> getAssignedReviewersList(final Paper thePaper) {
		List<User> theList = new ArrayList<User>();
		for (User u : Database.getReviewers(thePaper)) {
			theList.add(u);
		}
		return theList;
	}
	
	/**
	 * Commits spc review.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theReview spcreview to commit
	 * @param thePaper paper being reviewed
	 */
	public void commitSPCReview(final SPCReview theReview, final Paper thePaper) {
		Database.commitSPCReview(theReview, this, thePaper);
	}
	
	/**
	 * Gets list of users eligible for promotion to reviewer role.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return list of eligible users (for promotion)
	 */
	public List<User> getPotentialReviewers() {
		return Database.getPotentialReviewers(new Conference(getConferenceID(), ""));
	}
	
	/**
	 * Gets list of users eligible for demotion from reviewer role.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @return list of eligible users (for demotion)
	 */
	public List<User> getCurrentReviewers() {
		return Database.getCurrentReviewers(new Conference(getConferenceID(), ""));
	}
	
	
	//other
	
	/**
	 * Demotes this user based on the conference and role currently set.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	public void demote() {
		Database.updateUserRolesAtConference(this, false);	
	}
	
	/**
	 * Promotes this user based on the conference and role currently set.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	public void promote() {
		Database.updateUserRolesAtConference(this, true);	
	}
	
	//reviewer methods
	
	/**
	 * Gets the review by this user given paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param thePaper the paper reviewed
	 * @return review object by this user for the passed paper
	 */
	public Review getReview(final Paper thePaper) {
		return Database.getReviewByReviewer(this, thePaper);
	}
	
	/**
	 * Commits the review for paper.
	 * 
	 * @author Jesse Bostic
	 * 
	 * @param theReview the review to be committed
	 * @param thePaper the paper being reviewed
	 */
	public void commitReview(final Review theReview, final Paper thePaper) {
		Database.commitReview(theReview, this, this, thePaper);		
	}
	
	/**
	 * Returns whether passed object is equal to this user object.
	 * 
	 * @author Jesse Bostic
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		boolean areEqual = false;
		if (o instanceof User) {
			User theUser = (User) o;
			if (this.myID == theUser.myID && this.myUsername.equals(theUser.myUsername)
					&& this.getFullName().equals(theUser.getFullName())) {
				areEqual = true;
			}
		}
		return areEqual;
	}
	
	
	/**
	 * Returns string representation of this user.
	 * 
	 * @author John Jackson
	 * 
	 */
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		sb.append("ID = ");
		sb.append(myID);
		sb.append("\nName = ");
		sb.append(myFirstName);
		sb.append(" ");
		sb.append(myLastName);
		sb.append("\nEmail = ");
		sb.append(myEmail);
		sb.append("\n");
		 
		return sb.toString();
	}

}
