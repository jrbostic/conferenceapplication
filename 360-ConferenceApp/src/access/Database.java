
package access;
	
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

import objects.Conference;
import objects.Paper;
import objects.Review;
import objects.SPCReview;
import roles.User;
	
/**
 * A static library for managing the database for 360-ConferenceApp.
 * 
 * @author John Jackson, James Nance and Jesse Bostic
 */
public class Database extends Observable {
	
	/**
	 * the observable object for calling notify observers
	 */
	public static final Database OBSERVABLE = new Database();
	private static final int AUTHOR_PAPER_LIMIT = 4;
	private static final int SPC_REVIEW_LIMIT = 4;
	private static Connection connection = null;
	private static java.sql.Statement statement = null;
		
	/**
	 * For running direct tests on db.
	 * 
	 */
	public static void main (String[] args) throws SQLException {

	}
	
	/**
	 * Debug code to print all the users
	 */
	public static void printUsers() {
		connect();
		try {
			statement = connection.createStatement();
			ResultSet results1 = statement.executeQuery("select * from user");	
			while(results1.next())  {
				System.out.println("user_id = " + results1.getInt("user_id"));
				System.out.println("name = " + results1.getString("first_name") + " " + results1.getString("last_name"));
				System.out.println("email = " + results1.getString("email"));
				System.out.println("username = " + results1.getString("username"));
				System.out.println("password = " + results1.getString("password"));
				      
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		closeConnect();
	}
	
	/**
	 * Demote a User from the role of Subprogram Chair in a Conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being demoted.
	 * @param conference	The Conference wherein the User is being demoted.
	 */
	public static void demoteSPC( User user, Conference conference ) {
		boolean has_other_roles = false;
		
		connect();
		
		try {
			//Update the necessary tables.
			demoteSPCUpdate( user, conference );
			
			//Determine is the User has other roles in the Conference.
			has_other_roles = moreThanOneRoleInConference( user, conference );
			
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		try {
			statement = connection.createStatement();
			
			//If a User has other roles in the conference, only an update is necessary.
			if( has_other_roles ) {
				statement.executeUpdate( "UPDATE conference_user SET spc_role = 0"
										 + " WHERE user_id = " + user.myID
										 + " AND conference_id = " + conference.myID);
				
			//Else, if the User does not have other roles in the conference, and 
			} else {
				statement.executeUpdate( "DELETE FROM conference_user"
										 + " WHERE user_id = " + user.myID
										 + " AND conference_id = " + conference.myID);
			}
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		closeConnect();
	}
	
	/**
	 * Manages the table that may be affected by the demotion of a Subprogram Chair.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being demoted.
	 * @param conference	The Conference wherein the User is being demoted.
	 * @throws SQLException
	 */
	private static void demoteSPCUpdate(User user, Conference conference) throws SQLException {
		List<Integer> paper_ids = new ArrayList<Integer>();
		
		statement = connection.createStatement();
		
		//Get all papers relative to the SPC being demoted in a Conference.
		ResultSet res = statement.executeQuery( "SELECT paper_id FROM spcreview"
												+ " WHERE user_id = " + user.myID 
												+ " AND conference_id = " + conference.myID );
		
		int limit = 0;
		
		//Gather results.
		while( res.next() ) {
			paper_ids.add(res.getInt("paper_id"));
			limit++;
		}
		
		//Remove any SPCReviews they may have in this conference.
		for( int i = 0; i < limit; i++ ) {
			statement.executeUpdate( "DELETE FROM spcreview WHERE paper_id = " + paper_ids.get( i ));
		}
	}

	/**
	 * Promotes a User to the role of Subprogram Chair in a conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being promoted.
	 * @param conference	The Conference wherein the User is being promoted.
	 */
	public static void promoteToSPC( User user, Conference conference ) {
		user.setRole(3);
		user.setConference(conference.myID);
		
		//Update the necessary tables.
		updateUserRolesAtConference(user, true);
	}
	
	/**
	 * Promotes a User to the role of Reviewer in a given conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being promoted.
	 * @param conference	The Conference the User is being promoted in.
	 */
	public static void promoteToReviewer( User user, Conference conference ) {
		user.setRole(2);
		user.setConference(conference.myID);
		
		//Update the necessary tables.
		updateUserRolesAtConference(user, true);
	}
	
	/**
	 * Demote a User from the role of Reviewer in a given Conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being demoted.
	 * @param conference	The Conference the User is being demoted in.
	 */
	public static void demoteReviewer( User user, Conference conference ) {
		boolean has_other_roles = false;
		connect();
		
		try {
			//Update the necessary tables.
			demoteReviewerUpdate( user, conference );
			has_other_roles = moreThanOneRoleInConference( user, conference );
			
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		try {
			statement = connection.createStatement();
			
			//If the user has other roles, only an update is appropriate.
			if( has_other_roles ) {
				statement.executeUpdate( "UPDATE conference_user SET reviewer_role = 0"
										 + " WHERE user_id = " + user.myID
										 + " AND conference_id = " + conference.myID );
			//Else, if the user has no other roles in this conference, his relationship 
			//to this conference is deleted.
			} else {
				statement.executeUpdate( "DELETE FROM conference_user"
										 + " WHERE user_id = " + user.myID
										 + " AND conference_id = " + conference.myID);
			}
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		closeConnect();
	}
	
	/**
	 * Private helper method for demote methods.  Returns a boolean indicating a user is
	 * serving in a multi-role capacity in a given conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The User in question.
	 * @param conference	The Conference in question.
	 * @return				A boolean indicating a user is serving in a multi-role capacity
	 * 						in a given conference.
	 */
	private static boolean moreThanOneRoleInConference(User user,	Conference conference) {
		int returnable = 0;
		
		int[] roles = getUserRolesAtConferencePrivate( conference.myID, user.myID );
		
		for( int i : roles ) {
			returnable += i;
		}
		
		return returnable > 1;
	}

	/**
	 * Private helper method for returning a user's role in a given conference.
	 * 
	 * @author 				John Jackson
	 * @param user_id		ID of the user.
	 * @param conference_id ID of the conference.
	 * @return				A 4 element int[].  A '0' at an index indicates the user is not
	 * 						serving in the corresponding role during the provided conference.
	 * 						A '1' indicates the user is serving in the corresponding role 
	 * 						during the provided conference.
	 * 						Index Key: [0] = Author, [1] = Reviewer, [2] = Subprogram Chair, 
	 * 						[3] = Program Chair
	 */
	private static int[] getUserRolesAtConferencePrivate(int user_id, int conference_id) {
		int[] returnable = new int[4];
		
		try {
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery( "SELECT author_role, reviewer_role, " 
													+ "spc_role, pc_role FROM conference_user "
					 								+ "WHERE user_id = " + user_id  
					 								+ " AND conference_id = " + conference_id);
			
			while( res.next() ) {
				returnable[0] = res.getInt("author_role");
				returnable[1] = res.getInt("reviewer_role");
				returnable[2] = res.getInt("spc_role");
				returnable[3] = res.getInt("pc_role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return returnable;
	}

	/**
	 * Manages the tables that may be affected by the demotion of a Reviewer.
	 * 
	 * @author 				John Jackson
	 * @param user			The User being demoted.
	 * @param conference	The Conference the user being demoted in.
	 * @throws SQLException
	 */
	private static void demoteReviewerUpdate(User user, Conference conference) throws SQLException {
		List<Integer> review_ids = new ArrayList<Integer>(); 
		List<Integer> paper_ids = new ArrayList<Integer>();
		
		statement = connection.createStatement();
		
		//Get the Reviews this Reviewer has worked on this conference.
		ResultSet res = statement.executeQuery(" SELECT paper_id, review_id FROM review"
												 + " WHERE user_id = " + user.myID 
												 + " AND conference_id = " + conference.myID );
		int limit = 0;
		while( res.next() ) {
			review_ids.add(res.getInt("review_id"));
			paper_ids.add(res.getInt("paper_id"));
			limit++;
		}
	
		res = null;
		
		SPCReview spcreview = null;
		
		//Determine what reviews require updating.
		for( int i = 0; i < limit; i++ ) {
			spcreview = getSPCReviewPrivate( paper_ids.get( 0 ));
			
			//If the SPC has rated, but no decision by PC yet, the SPC rating is revoked.
			if( spcreview.myRating == 1 ) {
				statement.executeUpdate( "UPDATE spcreview SET spc_rating = 0, spc_comment = ''"
						 				 + " WHERE spcreview_id = " + spcreview.myID );
				//If a decision has been made by the PC, revoke that rating.
				if( spcreview.myPCRating == 1 ) {
					statement.executeUpdate( "UPDATE spcreview SET pc_rating = 0"
											 + " WHERE spcreview_id = " + spcreview.myID );
				}
			}
			
			//Delete the Review relative to that conference.
			statement.executeUpdate( "DELETE FROM review WHERE review_id = " + review_ids.get( i ));
		}
	}

	/**
	 * Private helper method that returns a SPCReview object.
	 * 
	 * @author 				John Jackson
	 * @param paper_id		ID of paper for which the SPCReview is being sought.
	 * @return				An SPCReview object relative to the ID.
	 * @throws SQLException
	 */
	private static SPCReview getSPCReviewPrivate( int paper_id ) throws SQLException {
		SPCReview returnable = null;
		
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery( "SELECT * FROM spcreview"
												+ " WHERE paper_id = " + paper_id);
		
		//Use results to construct a new SPCReview object.
		if( res.next() ) {	
			returnable = new SPCReview( res.getInt("spcreview_id"), res.getInt("user_id"), 
										res.getInt("conference_id"), res.getInt("paper_id"), 
										res.getInt("spc_rating"), res.getString("spc_comment"), 
										res.getInt("pc_rating") );
		}

		return returnable;
	}

	/**
	 * Returns a user's password when passed the corresponding username.
	 * 
	 * @author 			John Jackson
	 * @param username	The user's username.
	 * @return			The user's password.
	 */
	public static String getPassword( String username ) {
		String returnable = "Account Not Found"; 
		
		connect();
		
		try {
		
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery("SELECT password FROM user WHERE username = '" + username + "'");
			
			//If a result exists, there is a match.
			if(res.next()) {
				returnable = res.getString("password");
			}
		
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable; 
	}
	
	////////TEMPLATE DB METHOD STRUCTURE//////////////////////////////////
	/*
		returnType methodName (parameters) throws SQLException {
	
			connect();
			
			statement = connection.createStatement();
			ResultSet result = statement.executeQuery("SELECT * FROM table WHERE field1 = '" 
														+ value1 + "' AND field2 = '" + value2);
	
			****OPERATE ON RESULTSET****
		
			closeConnect();
		}
	*/
	//////////////////////////////////////////////////////////////////////	
	
	
	public Database() {
	}
	
	/**
	 * Opens the connection to the database.
	 * 
	 * @author 	John Jackson
	 */
	private static void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:lib/CSEE_JAR_CLEAN.db");
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the connection to the database.
	 * 
	 * @author	John Jackson
	 */
	private static void closeConnect() {
		if(connection != null)
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * Verifies user login information and returns a User object.
	 * 
	 * @author				John Jackson
	 * @param username  	Username provided at login.
	 * @param password		Password provided at login.
	 * @return				User object.
	 * @throws SQLException 
	 */
	public static User login( String username, String password ) throws SQLException {
		User returnable = null;
		
		connect();
		
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery("SELECT * FROM user WHERE username = '" + username + "' AND password = '" + password + "'");
		
		//No result, no account.
		if( !res.next() ) {
			return null;
		} else {
			//Creates a new User object with the results.
			returnable = new User( res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), 
								   res.getString("email"), res.getString("username"), res.getString("password") ) ;
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a representation of a user's available roles in a given conference.
	 * 
	 * @author					John Jackson
	 * @param conference_id		Conference a user may have a role in.
	 * @param user_id			The user's id.
	 * @return returnable		A 4 element int[].  A '0' at an index indicates the user is not
	 * 							serving in the corresponding role during the provided conference.
	 * 							A '1' indicates the user is serving in the corresponding role 
	 * 							during the provided conference.
	 * 							Index Key: [0] = Author, [1] = Reviewer, [2] = Subprogram Chair, [3] = Program Chair
	 */
	public static int[] getUserRolesAtConference( int conference_id, int user_id ) {
		int[] returnable = new int[4];
		
		connect();
		
		try {
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery( "SELECT author_role, reviewer_role, " 
													+ "spc_role, pc_role FROM conference_user "
					 								+ "WHERE user_id = " + user_id  
					 								+ " AND conference_id = " + conference_id);
			
			//Assemble results per above JavaDoc comments.
			while( res.next() ) {
				returnable[0] = res.getInt("author_role");
				returnable[1] = res.getInt("reviewer_role");
				returnable[2] = res.getInt("spc_role");
				returnable[3] = res.getInt("pc_role");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns an unfiltered list of Conferences.
	 * 
	 * @author 				John Jackson
	 * @return returnable	A list of all conferences in the conference table.
	 */
	public static List<Conference> getConferences() {
		List<Conference> returnable = new ArrayList<Conference>();
		
		connect();
		
		try {
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery("select * from conference");
		
			while( res.next() ) {
				Conference addMe = new Conference(res.getInt("conference_id"), res.getString("conference_name"));
				returnable.add(addMe);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Updates an entry in review table.
	 * 
	 * @author				John Jackson
	 * @param review		The Review being added to the database.
	 * @throws SQLException 
	 */
	private static void updateReview( Review review, int user_id, int conference_id ) {
		connect();
		try {
			statement = connection.createStatement();
			statement.executeUpdate("UPDATE review SET paper_id =" + review.myPaperID
											      + ", user_id = " + user_id
												  + ", conference_id = " + conference_id
												  + ", review_rating = " + review.myRatings[9]
												  + ", review_comment = '" + review.myComment  
												  + "', q1 = " + review.myRatings[0]
												  + ", q2 = " + review.myRatings[1]
												  + ", q3 = " + review.myRatings[2]
												  + ", q4 = " + review.myRatings[3]
												  + ", q5 = " + review.myRatings[4]
												  + ", q6 = " + review.myRatings[5]
												  + ", q7 = " + review.myRatings[6]
												  + ", q8 = " + review.myRatings[7]
												  + ", q9 = " + review.myRatings[8]
												  + " WHERE review_id = " + review.myID);
		} catch(SQLException e) {
			e.printStackTrace();
		}
		closeConnect();
		OBSERVABLE.notifyMainView();
	}

	/**
	 * Removes a paper by a user in a conference.
	 * 
	 * @author 				John Jackson
	 * @param the_paper		The paper to be removed.
	 * @throws SQLException 
	 */
	public static void removePaper( User user, Paper the_paper ) {		
		
		boolean retain_authorship = GetPaperCountByAuthorAndConference( user.myID, user.getConferenceID() ) > 1 ? true : false;
		
		connect();
		
		try {
			statement = connection.createStatement();
			
			statement.executeUpdate("DELETE FROM paper WHERE paper_id = " + the_paper.myID);
			statement.executeUpdate("DELETE FROM conference_paper WHERE paper_id = " + the_paper.myID);
			statement.executeUpdate("DELETE FROM review WHERE paper_id = " + the_paper.myID);
			statement.executeUpdate("DELETE FROM spcreview WHERE paper_id = " + the_paper.myID);
			
			updateUserRolesAtConference(user, retain_authorship);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		OBSERVABLE.notifyMainView();
	}
	
	/**
	 * Returns verification that the paper is in the database.
	 * 
	 * @author 				John Jackson
	 * @param paper			Paper being verified.
	 * @return				Verification that the paper is in the database.
	 * @throws SQLException
	 */
	private static boolean paperExists( Paper paper ) throws SQLException {
		boolean returnable = false;
		
		connect();
		
		statement = connection.createStatement();		
		
		ResultSet res = statement.executeQuery("SELECT paper_id FROM paper WHERE paper_id = " + paper.myID);
		
		if( res.next() ) {
			returnable = true;
		}
		
		closeConnect();
		
		return returnable;
	}
	

	/**
	 * This method checks if the PC has accepted or rejected a paper.
	 * 
	 * @author James Nance
	 * @param the_paper_id The paper the SPC reveiw is about
	 * @param the_conference_id The primary key of the conference the paper has been submitted to.
	 * @return True if the PC has assigned an accept or a reject.  False otherwise.
	 */
	public static boolean isPcReviewPresent(int the_paper_id, int the_conference_id)
	{
		boolean isReviewFinished = false;
		connect();
		try {
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery("SELECT * "
												 + "FROM spcReview"
												 + "WHERE paper_id = " + the_paper_id +" AND conference_id = " + the_conference_id);
			
			//1 is accepted and -1 is rejected.
			if(res.next() && (res.getInt("PC_RATING") == 1 || res.getInt("PC_RATING") == -1))
			{
				isReviewFinished = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		return isReviewFinished;
	}
	
	/**
	 * Assigns a Paper to a Reviewer.
	 * 
	 * @author 					John Jackson	
	 * @param the_spc			The Subprogram Chair assigned to the paper.
	 * @param the_new_reviewer	The Reviewer being assigned to the paper.
	 * @param the_old_reviewer	A previous Reviewer that may be getting replaced.  A no arg 
	 * 							new User() instantiation may be passed if no previous Reviewer
	 * 				`			had been assigned.
	 * @param paper				The paper being assigned.
	 */
	public static void assignReviewer( User the_spc, User the_new_reviewer, User the_old_reviewer, Paper paper ) {
		try {
			the_old_reviewer.setConference(getConference(paper));
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Review review = getReviewByReviewer( the_old_reviewer, paper );
		
		the_new_reviewer.setRole(2);
		the_new_reviewer.setConference( the_spc.getConferenceID() );
		
		commitReview( review, the_new_reviewer, the_old_reviewer, paper );
			
		updateUserRolesAtConference( the_new_reviewer, true );
	}
	
	/**
	 * Returns a Review relative to a Reviewer and a Paper.
	 * 
	 * @author 				John Jackson
	 * @param the_reviewer	The Reviewer relative to the Review.
	 * @param paper			The Paper relative to the Review.
	 * @return				Th Review relative to the_reviewer and paper.
	 */
	public static Review getReviewByReviewer( User the_reviewer, Paper paper ) {
		Review returnable = null;
		
		if( the_reviewer.myID != 0 ) {
			try {
				connect();
				
				statement = connection.createStatement();
				
				//Get ID from Review table where the user_id and paper_id match.
				ResultSet res = statement.executeQuery( "SELECT review_id FROM review" 
														+ " WHERE paper_id = " + paper.myID
														+ " AND user_id = " + the_reviewer.myID);
				
				//If there is a result, there can only be 1.  Get the review relative to it.
				if( res.next() ) {
					returnable = getReview( res.getInt( "review_id" ));
				}
														
				closeConnect();
				
			} catch( SQLException e ) {
				e.printStackTrace();
			}
		}
		
		return returnable;
	}
	
	/**
	 * Returns a Review object.  Returns null if review does not exist.
	 * 
	 * @author					John Jackson
	 * @param review_id			ID of Review being sought.
	 * @return returnable		The corresponding Review.
	 * @throws SQLException
	 */
	private static Review getReview( int review_id ) throws SQLException {
		Review returnable = null;
		
		statement = connection.createStatement();
		
		ResultSet res =	statement.executeQuery( "SELECT * FROM review WHERE review_id = " + review_id);
		
		if( res.next() ) {
			int[] responses = { res.getInt("q1"), res.getInt("q2"), res.getInt("q3"),
				 	 			res.getInt("q4"), res.getInt("q5"), res.getInt("q6"),
				 	 			res.getInt("q7"), res.getInt("q8"), res.getInt("q9"),
				 	 			res.getInt("review_rating")};
			
			//Construct a new Review object based on the results of the query.
			returnable = new Review( res.getInt("review_id"), res.getInt("paper_id"), 
					 			 	 res.getInt("user_id"), res.getInt("conference_id"), 
					 			 	 responses, res.getString("review_comment") );
		}
		
		return returnable;
	}
	
	/**
	 * Updates a users email and password.
	 * 
	 * @author 			John Jackson
	 * @param user		The user whose account is being updated.
	 * @param password	The new password.	
	 * @param email		The new email.
	 */
	public static void updateUser( User user, String password, String email ) {
		try {
			connect();
			
			statement = connection.createStatement();
			
			//Update user table.
			statement.executeUpdate("UPDATE user SET email = '" + email 
												+ "', password = '" + password 												
												+ "' WHERE user_id = " + user.myID);
			
			closeConnect();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	} 
	
	/**
	 * Returns an array containing the four reviewers' User objects. 
	 * 
	 * @author 			John Jackson
	 * @param paper 	The paper being submitted.
	 * @return			An array of users.
	 */
	public static User[] getReviewers( Paper paper ) {
		User[] returnable = new User[4];
		
		connect();
		
		int index = -1;
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery("SELECT user_id FROM review WHERE paper_id = " + paper.myID);
			
			while( res.next() ) {
				index++;
				returnable[index] = getUser( res.getInt( "user_id" ));
			}
				
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		//If there are less than 4 reviewers assigned, the remaining spots are filled with
		//"filler" Users representing no Reviewer assigned yet.
		while( index < 3 ) {
			index++;
			returnable[index] = new User();
		}
	
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns all Reviews of a paper.
	 * 
	 * @author					John Jackson
	 * @param paper				The paper that reviews are being requested of.
	 * @return returnable		An array of Review objects.
	 * @throws SQLException
	 */
	public static List<Review> getReviews( Paper paper ) {
		List<Review> returnable = new LinkedList<Review>();
		
		connect();
		
		try {
			statement = connection.createStatement();
			
			ResultSet res =	statement.executeQuery( "SELECT review_id FROM review WHERE paper_id = " + paper.myID);
			
			if( res.next() ) {
				List<Integer> review_ids = new ArrayList<Integer>();
				review_ids.add(res.getInt("review_id"));
				
				while( res.next() ) {
					review_ids.add( res.getInt("review_id") );
				}
			
				for( int i = 0; i < review_ids.size(); i++ ) {
					returnable.add( getReview( review_ids.get( i)));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Commits a Review object to memory.
	 * 
	 * @author					John Jackson
	 * @param review			The Review being committed.
	 * @param the_new_reviewer	The Reviewer assigned to the paper.
	 * @param the_old_reviewer	A previous Reviewer that may be getting replaced.  A no arg 
	 * 							User user = new User() instantiation may be passed if no 
	 * 							previous Reviewer was assigned.
	 * @param paper				The Paper the Review corresponds to.
	 */
	public static void commitReview( Review review, User the_new_reviewer, User the_old_reviewer, Paper paper ) {
		if( review == null ) {
			insertReview( the_new_reviewer.myID, the_new_reviewer.getConferenceID(), paper.myID );
		} else {
			updateReview( review, the_new_reviewer.myID, the_new_reviewer.getConferenceID() );
		}
		
		OBSERVABLE.notifyMainView();
	}

	/**
	 * Inserts a new entry into the review table.
	 * 
	 * @author 				John Jackson
	 * @param reviewer_id	The ID of the reviewer completing this review.
	 * @param conference_id The ID of the conference the paper is in. 
	 * @param paper_id		The ID of the paper this review is for.
	 */
	private static void insertReview( int reviewer_id, int conference_id, int paper_id ) {
		connect();
		try {
			statement = connection.createStatement();
		
			statement.executeUpdate("INSERT INTO review VALUES ( " + null 
																 + ", " + paper_id
																 + ", " + reviewer_id
																 + ", " + conference_id
																 + ", 0,'" + "" +"',0,0,0,0,0,0,0,0,0)");
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
	}

	/**
	 * Assigns a paper to a Subprogram Chair.
	 * 
	 * @author 				John Jackson
	 * @param the_pc		The Program Chair assigning the paper.
	 * @param the_new_spc	The Subprogram Chair being assigned the paper.
	 * @param the_paper		The paper being assigned.
	 */
	public static void assignSPC( User the_pc, User the_new_spc, Paper the_paper) {
		
		SPCReview spcreview = getSPCReview( the_paper.myID );
		
		if (spcreview.myID == 0) {
			spcreview = null;
		}
		
		the_new_spc.setRole(3);
		the_new_spc.setConference( the_pc.getConferenceID() );
	
		commitSPCReview( spcreview, the_new_spc, the_paper );
			
		updateUserRolesAtConference( the_new_spc, true );
	}
	
	/**
	 * Creates an entry indicating a user has a role in a conference.
	 * 
	 * @author		John Jackson
	 * @param user	The user being added to the conference.
	 */
	private static void insertUserIntoUserConference( User user ) {
		connect();
		
		try {
			statement = connection.createStatement();
			
			statement.executeUpdate( "INSERT INTO conference_user VALUES ( " + user.getConferenceID() 
																	  	   + ", " + user.myID
																	       + ", 0, 0, 0, 0 )");

		} catch (SQLException e) {
			e.printStackTrace();
		}
								 
		closeConnect();
	}
	
	/**
	 * Returns a boolean indicating a user has a role in a conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The user in question.
	 * @return				A boolean indicating a user has a role in a conference.
	 * @throws SQLException
	 */
	private static boolean isUserInUserConference( User user ) throws SQLException {
		boolean returnable = false;
		
		connect();
		
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery("SELECT user_id FROM conference_user"
												+ " WHERE user_id = " + user.myID
												+ " AND conference_id = " + user.getConferenceID());
		if( res.next() ) {
			returnable = true;
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Updates a user's role.  
	 * 
	 * @author 				John Jackson
	 * @param user			The user whose role is being updated
	 * @param active		Whether or not the role should be active.
	 * @throws SQLException
	 */
	public static void updateUserRolesAtConference( User user, boolean active ) {
		
		int role_value = active ? 1 : 0;
		int role_id = user.getRoleID();
		String role = "";
		
		if( role_id == 4 ) {
			role = "pc_role";
		} else if( role_id == 3 ) {
			role = "spc_role";
		} else if( role_id == 2 ) {
			role = "reviewer_role";
		} else if( role_id == 1 ){
			role = "author_role";
		}	
		
		boolean user_exists = false;
		
		try {
			user_exists = isUserInUserConference( user );
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		if( !user_exists ) {
			insertUserIntoUserConference( user );
		}
		
		connect();
		
		try {
			
		statement = connection.createStatement();
		
		statement.executeUpdate( "UPDATE conference_user SET " + role + " = " + role_value 
								 + " WHERE (user_id = " + user.myID 
								 + " AND conference_id = " + user.getConferenceID() +")");
		} catch (SQLException e)  {
			e.printStackTrace();
		}
								 
		closeConnect();
	}
	
	/**
	 * Returns true if SPC has not reached conference limit of being assigned 4 papers.
	 * 
	 * @author 				John Jackson
	 * @param user_id		ID of potential assignee.
	 * @param conference_id ID of conference paper is in.
	 * @return				Validity of SPC accepting assignment to another paper.
	 * @throws SQLException
	 */
	private static boolean hasMaxSPC( int user_id, int conference_id ) throws SQLException {
		
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery( "SELECT spcreview_id FROM spcreview"
												+ " WHERE user_id = " + user_id 
												+ " AND conference_id = " + conference_id );
		int paper_count = 0;
		
		while( res.next() ) {
			paper_count++;
		}
		
		return paper_count > 3;
	}
	
	/**
	 * Returns a list of potential Subprogram Chairs in the current 
	 * 
	 * @author 			John Jackson
	 * @param paper		The paper a Subprogram Chair is to be assigned to.		
	 * @return			A List<User> of potential SPCs.
	 */
	public static List<User> getPotentialSPCs( Conference conference ) {
		List<User> returnable = new ArrayList<User>();
		
		connect();
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_user"
													+ " WHERE (reviewer_role = 1 OR pc_role = 1) AND spc_role != 1 AND conference_id = " + conference.myID );
								
			while(res.next()) {
				returnable.add(getUser(res.getInt("user_id")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a boolean indicating whether a Reviewer has the max number of papers 
	 * allowed per conference.
	 * 
	 * @author 				John Jackson
	 * @param user			The potential reviewer of a paper.
	 * @param conference_id The conference the paper was submitted in.
	 * @return				A boolean indicating whether a Reviewer has the max number of
	 * 						papers allowed per conference.
	 */
	private static boolean hasMaxReviewer( int user_id, int conference_id ) {
		int count = 0;
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery("SELECT review_id FROM review WHERE user_id = " + user_id
																	       	   + " AND conference_id = " + conference_id);
			
			while( res.next() ) {
				count++;
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return count > 3;
	} 
	
	/**
	 * Returns a List<User> containing all users currently available to review
	 * the paper passed.
	 * 
	 * @author 			John Jackson
	 * @param paper		The paper in need of reviewers.
	 * @return			A List<User> representing the available to review the 
	 * 					paper.
	 */
	public static List<User> getAvailableReviewers( Paper paper ) {
		List<User> returnable = new ArrayList<User>();
		
		int author_id = -1;
		int conference_id = -1;
		
		try {
			conference_id = getConference( paper );
			author_id = getAuthorID( paper );
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		connect();
		
		List<Integer> ids = new ArrayList<Integer>();
		
		try {
			
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_user"
												    + " WHERE user_id != " + author_id 
												    + " AND conference_id = " + conference_id
												    + " AND reviewer_role = 1");
			
			while( res.next() ) {
				ids.add(res.getInt( "user_id" ));
			}
			
			//Check that a user is not already a reviewer for the paper. 
			User[] reviewers = getReviewersPrivate( paper );
			
			for( int i : ids ) {
				boolean already_reviewing = false;
				
				for( User u : reviewers ) {
					if( i == u.myID ) {
						already_reviewing = true;
						
					}
				}
				
				//Verify business rules being adhered to.
				if( !hasMaxReviewer(i, conference_id) && !already_reviewing) {
					returnable.add( getUser(i) );
				}
			}			
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a list of all SPC that can be assigned this paper.
	 * @param paper The paper to be assigned.
	 * @return A list of all eligible SPC's.
	 * 
	 * @author 			John Jackson
	 * @param paper		The Paper SPCs are being sought for.
	 * @return			List<User> of the SPCs available to be assigned the paper.
	 */
	public static List<User> getAvailableSPCs( Paper paper ){
		List<User> returnable = new ArrayList<User>();
		
		int author_id = -1;
		int conference_id = -1;
		
		try {
			author_id = getAuthorID( paper );
			conference_id = getConference( paper );
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		connect();
		
		List<Integer> ids = new ArrayList<Integer>();
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_user"
													+ " WHERE user_id != " + author_id 
													+ " AND conference_id = " + conference_id 
													+ " AND (spc_role = 1 OR pc_role = 1)" );
			
			while( res.next() ) {
				ids.add(res.getInt("user_id"));
			}
			
			for( int i : ids ) {
				//Check the business rule constaraints.
				if( !hasMaxSPC( i, conference_id ) ) {
					returnable.add( getUser( i ));
				}
			}
			
		} catch( SQLException e ) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a list of all SPC's at this current conference.
	 * @param conference The conference the SPCs belong to.
	 * @return A list of Users who are SPCs in the conference.
	 * 
	 * @author 				John Jackson
	 * @param conference	The conference we are searching.
	 * @return				List<User> of SPCs currently approved for this conference.
	 */
	public static List<User> getCurrentSPCs( Conference conference ) {
		List<User> returnable = new ArrayList<User>();
		
		connect();
		
		try {
			statement = connection.createStatement();
		
			ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_user"
													+ " WHERE conference_id = " + conference.myID
													+ " AND spc_role = 1");
			
			while( res.next() ) {
				returnable.add( getUser( res.getInt( "user_id" )));
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a List<User> of all Reviewers in the passed conferences.
	 * 
	 * @author 				John Jackson
	 * @param conference	The conference in question.
	 * @return				A List<User> of all Reviewers in the passed conference.
	 */
	public static List<User> getCurrentReviewers( Conference conference ) {
		List<User> returnable = new ArrayList<User>();
		
		connect();
		
		try {
			statement = connection.createStatement();
		
			ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_user"
													+ " WHERE conference_id = " + conference.myID
													+ " AND reviewer_role = 1" );
			
			while( res.next() ) {
				returnable.add( getUser( res.getInt( "user_id" )));
			}

		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a List<User> of users available for promotion to Reviewer status.
	 * 
	 * @author 			John Jackson
	 * @conference		The conference for which these users may gain a role in.
	 * @return			A List<User> of users available for promotion to Reviewer status.
	 */
	public static List<User> getPotentialReviewers( Conference conference ) {
		List<User> returnable = new ArrayList<User>();
		List<User> current_rev = getCurrentReviewers( conference );
		List<Integer> current_rev_ids = new ArrayList<Integer>();
		
		for( User u : current_rev ) {
			current_rev_ids.add(u.myID);
		}
		
		try {
			connect();	
			
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT user_id FROM user" );
		
			while( res.next() ) {
				int id = res.getInt( "user_id" );
				
				if( !current_rev_ids.contains( id )) {
					returnable.add( getUser( id ));
				}
			}	
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	
	}
	
	/**
	 * Returns the Reviewers of a given paper.
	 * 
	 * @author 		John Jackson
	 * @param paper The paper being reviewed.
	 * @return		List<User>.  The Reviewers of the paper.  
	 */
	private static User[] getReviewersPrivate(Paper paper) {
		User[] returnable = new User[4];
		
		int index = -1;
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery("SELECT user_id FROM review WHERE paper_id = " + paper.myID);
			
			while( res.next() ) {
				index++;
				returnable[index] = getUser( res.getInt( "user_id" ));
			}
				
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		while( index < 3 ) {
			index++;
			returnable[index] = new User();
		}
		
		return returnable;
	}

	/**
	 * Returns the ID of the author of a paper.  Returns -1 if no author is found.
	 * 
	 * @author 				John Jackson
	 * @param paper			Paper whom the author is in question.
	 * @return				ID of the paper's author.
	 * @throws SQLException
	 */
	private static int getAuthorID( Paper paper ) throws SQLException {
		int author_id = -1;
		
		connect();
		
		statement = connection.createStatement();
		ResultSet res = statement.executeQuery( "SELECT user_id FROM conference_paper  "
												+ "WHERE paper_id = " + paper.myID);
		
		if( res.next() ) {
			author_id = res.getInt("user_id");
		}
		
		closeConnect();
		
		return author_id;
	}
	
	/**
	 * Commits a SPCReview object's information to memory.
	 * 
	 * @author 			John Jackson
	 * @param spcreview	The completed SPCReview object.
	 * @param spc		The SPC User object.
	 * @param paper		The Paper object the SPCReview corresponds to.
	 */
	public static void commitSPCReview( SPCReview spcreview, User spc, Paper paper ) {
		
		if( spcreview == null ) {
			insertSPCReview( spc.myID, spc.getConferenceID(), paper.myID );
		}  else {
			updateSPCReview( spcreview, spc.myID, spc.getConferenceID() );
		}
	
		OBSERVABLE.notifyMainView();
	}
	
	/**
	 * Returns the conference of a given paper.
	 * 
	 * @author 				John Jackson
	 * @param paper			The paper whose conference is in question.
	 * @return				The ID of the conference the paper was submitted.
	 * @throws SQLException
	 */
	private static int getConference( Paper paper ) throws SQLException {
		int returnable = -1;
		
		connect();
		
		
		statement = connection.createStatement();
			
		ResultSet res = statement.executeQuery( "SELECT conference_id FROM conference_paper "
												+ "WHERE paper_id = " + paper.myID );
			
		if( res.next() ) {
			returnable = res.getInt("conference_id");
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Updates a SPC Review entry in memory.
	 *
	 * @author 				John Jackson
	 * @param spc_review	The updated SPCReview object.
	 * @param spc_id		ID of the SPC.
	 * @param conf_id		ID of the conference.
	 */
	private static void updateSPCReview( SPCReview spc_review, int spc_id, int conf_id ) {
		connect();
		
		try {
			statement = connection.createStatement();	
			statement.executeUpdate( "UPDATE spcreview SET user_id = " + spc_id 
								 				  + ", conference_id = " + conf_id
								 				  + ", paper_id = " + spc_review.myPaperID
								 				  + ", spc_rating = " + spc_review.myRating 
								 				  + ", spc_comment = '" + spc_review.myComment
								 				  + "', pc_rating = " + spc_review.myPCRating 
								 				  + " WHERE spcreview_id = " + spc_review.myID );
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		closeConnect();
		
	}
	
	/**
	 * Inserts a SPC Review into the database.
	 * 
	 * @author 				John Jackson
	 * @param spc_user_id	ID of the SPC.
	 * @param conference_id	ID of the conference.
	 * @param paper_id		ID of the paper for which the SPC Review is being created.
	 */
	private static void insertSPCReview( int spc_user_id, int conference_id, int paper_id ) {
		connect();
		
		try { 
			statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO spcreview VALUES ( " + null 
																+ ", " + spc_user_id
																+ ", " + conference_id
																+ ", " + paper_id
																+ ", 0, '', 0)");
		}catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		closeConnect();
	}

	/**
	 * Returns the SPCReview corresponding the ID passed.  Returns a null SPCReview o
	 * object if ID not found.
	 * 
	 * @author 					John Jackson	
	 * @param paper_id			ID of paper for which the SPC Review is being requested.
	 * @return					The SPCReview corresponding to the ID passed.
	 * @throws SQLException
	 */
	public static SPCReview getSPCReview( int paper_id ) {
		
		SPCReview returnable = new SPCReview();
		
		connect();
		
		try {
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT * FROM spcreview"
													+ " WHERE paper_id = " + paper_id);
			if( res.next() ) {	
				returnable = new SPCReview( res.getInt("spcreview_id"), res.getInt("user_id"), 
											res.getInt("conference_id"), res.getInt("paper_id"), 
											res.getInt("spc_rating"), res.getString("spc_comment"), 
											res.getInt("pc_rating") );
			}
			
		} catch (SQLException e) {
				e.printStackTrace();
		} 
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a string representing the status of a paper.
	 * 
	 * @author 		John Jackson
	 * @author 		Jesse Bostic
	 * @param paper	The paper for which status is in question.
	 * @return		A string representing the status of a paper.
	 */
	public static String getPaperStatus( Paper paper ) {
		String returnable = "Under Review";
		List<Review> reviews = getReviews( paper );
		boolean reviews_complete = true;
		
		for( Review r : reviews ) {
			if( r.myRatings[9] == 0 ) {
				reviews_complete = false;
			}
		}
	
		if( reviews_complete ) {
				returnable = "Under Review By SPC";
		}
		
		for (User rev : getReviewers(paper)) {
			if (rev.myID==0) {
				returnable = "Awaiting Reviewer Assignment";
			}
		}
		
		SPCReview spcreview = getSPCReview(paper.myID);
		if( spcreview.myRating != 0 ) {
			returnable = "Awaiting PC Decision";
		} else if (getSPCByPaper(paper).myID == 0) {
			returnable = "Awaiting SPC assignment";
		}
		
		int pc_status = spcreview.myPCRating;
		if( pc_status != 0 ) {
			returnable = pc_status == 1 ? "Accepted" : "Rejected";
		}		
		
		return returnable;
	}
	
	/**
	 * Moderates SPC and PC ability to rate (and plays a part in author editing permissions).  
	 * 
	 * 0=still reviewing, 1 = spc decision waiting, 2 = pc decision pending 3 = pc decision finished.
	 * 
	 * @author Jesse Bostic
	 * @param paper the paper to assess
	 * @return int corresponding to the three final stages of review
	 */
	public static int getIntPaperStatus( Paper paper ) {
		int returnable = 0;
		
		List<Review> reviews = getReviews( paper );

		boolean reviews_complete = true;
		int count = 0;
		for( Review r : reviews ) {
			count++;
			if( r.myRatings[9] == 0 ) {
				reviews_complete = false;
			}
		}
	
		if( reviews_complete && count > 3) {
			returnable = 1;
		}
		
		SPCReview spcreview = getSPCReview(paper.myID);
		if( spcreview.myRating != 0 ) {
			returnable = 2;
		}
		if (spcreview.myPCRating != 0){
			returnable = 3;
		}
		return returnable;
	}
	
	/**
	 * Updates the Program Chair's acceptance status of a paper in memory.
	 * 
	 * @author 			John Jackson
	 * @param paper		The paper the decision is being made on.
	 * @param accept	The decision of the Program Chair.
	 */
	public static void makeDecision( Paper paper, boolean accept ) {
		int rating = accept ? 1 : -1;
		
		connect();
	
		try {
			statement = connection.createStatement();
		
			statement.executeUpdate( "UPDATE spcreview SET pc_rating = " + rating
									 + " WHERE paper_id = " + paper.myID );
			
		} catch ( SQLException e ) {
			e.printStackTrace();
		}
		
		closeConnect();
		OBSERVABLE.notifyMainView();
	}
	
	/**
	 * Returns a User object that corresponds to the user_id provided.  If user is not found, a null 
	 * User object is returned.
	 * 
	 * @author 					John Jackson
	 * @param user_id			The id of the user whose information is being sought.
	 * @return					A User object corresponding to the user_id.
	 * @throws SQLException
	 */
	private static User getUser( int user_id ) throws SQLException {
		User returnable = null;
		
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery( "SELECT * FROM user WHERE user_id = " + user_id );
		
		if( res.next() ) {
			returnable = new User( res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), 
								   res.getString("email"), res.getString("username"), res.getString("password") );
		}
		
		return returnable;
	}
	
	/**
	 * Gets all the paper ID by the author ID.
	 * 
	 * @author James Nance
	 * @param the_author_id The author whose papers we are getting.
	 * @return A list of paper IDs.
	 * @throws SQLException
	 */
	private static List<Integer> getAllPapersIdsByAuthor(int the_author_id, int the_conference_id) throws SQLException
	{	
		connect();
		statement = connection.createStatement();
		ResultSet res = statement.executeQuery("SELECT * FROM conference_paper WHERE user_id = " + the_author_id
												+ " AND conference_id = " + the_conference_id);
		List<Integer> returnable = new ArrayList<Integer>();
		
		while(res.next())
		{
			returnable.add(res.getInt("paper_id"));
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns a list of all paper IDs relative to the conference ID passed.
	 * 
	 * @author James Nance
	 * @param the_conference_id ID of the conference in question.
	 * @return A list of all paper IDs in the conference.
	 * @throws SQLException
	 */
	private static List<Integer> getAllPapersIdsByConference(int the_conference_id) throws SQLException
	{	
		connect();
		statement = connection.createStatement();
		
		ResultSet res = statement.executeQuery("SELECT * FROM conference_paper WHERE conference_id = " + the_conference_id);
		List<Integer> returnable = new ArrayList<Integer>();
		
		while(res.next())
		{
			returnable.add(res.getInt("paper_id"));
		}
		
		closeConnect();
		
		return returnable;
	}
	
	
	/**
	 * Gets all papers by a list of papers by a list of paper ID's.
	 * 
	 * @author James Nance
	 * @param the_paper_ids A list of Ids.
	 * @return A list of Papers.
	 * @throws SQLException
	 */
	private static List<Paper> getAllPapersByPaperId(List<Integer> the_paper_ids) throws SQLException
	{
		connect();
		StringBuilder sb = new StringBuilder();
		List<Paper> returnable = new ArrayList<Paper>();
		
		//Checking to make sure papers were handed in, If not this would generate a bad SQL query so we skip
		//everything and hand back an empty list.
		if (the_paper_ids.size() != 0)
		{
			String sqlStatement = "SELECT * FROM paper WHERE paper_id = ";
			sb.append(sqlStatement);
			sb.append(the_paper_ids.get(0));
			
			//Appending OR statements to get more papers.
			for (int i = 1; i < the_paper_ids.size(); i++) 
			{
				sb.append(" OR paper_id = ");
				sb.append(the_paper_ids.get(i));
			}
			
			statement = connection.createStatement();
			ResultSet res = statement.executeQuery(sb.toString());
			
			while (res.next())
			{
				int the_id = res.getInt("paper_id");
				
				String the_author = res.getString("author");
				
				String the_title = res.getString("title");
				
				String the_abstract = res.getString("abstract");
				
				String the_catagory = res.getString("category");
				
				String the_file_path = res.getString("file_path");
				
				returnable.add(new Paper(the_id, the_author, the_title, the_abstract, the_catagory, the_file_path));
				
			}
		}
		
		closeConnect();
		return returnable;
	}
	
	/**
	 * Handles the logic to make compound calls that return list of papers for user, role, conference.
	 * 
	 * @author Jesse Bostic
	 * @param theUser The user using the system.	
	 * @return List<Paper> of Papers
	 */
	public static List<Paper> getPapers(User theUser) {
		List<Paper> theList = new ArrayList<Paper>();
		int theRoleID = theUser.getRoleID();
		
		try {
			if (theRoleID == 1) {
				theList = getAllPapersByPaperId(getAllPapersIdsByAuthor(theUser.myID, theUser.getConferenceID()));
			} else if (theRoleID == 2) {
				theList = getPapersByReviewer( theUser.myID, theUser.getConferenceID() );
			} else if (theRoleID == 3) {
				theList = getPapersBySPC( theUser.myID, theUser.getConferenceID() );
			} else if (theRoleID == 4) {
				theList = getAllPapersByPaperId(getAllPapersIdsByConference(theUser.getConferenceID()));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return theList;
	}
	
	/**
	 * This method returns a count of papers by an author in a conference.
	 * 
	 * @author James Nance
	 * @param the_author_id The author whose papers we are looking for.
	 * @param the_conference_id The conference where the papers were submitted.
	 * @return An integer representing the count.  If a -1 is returned something went wrong with the SQL query.
	 */
	private static int GetPaperCountByAuthorAndConference(int the_author_id, int the_conference_id)
	{
		int returnable = 0;
		
		connect();
		
		try 
		{
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT paper_id FROM conference_paper"
													+ " WHERE user_id = " + the_author_id
													+ " AND conference_id = " + the_conference_id);
							
			while( res.next() ) {
				returnable++;
			}
			
		} catch (SQLException e) 
		{
			
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}

	/**
	 * This method gets a count of all the SPC reviews by an SPC at a specific conference.
	 * 
	 * @author James Nance
	 * @param the_spc_id the SPC to be checked.
	 * @param the_conference_id The conference the SPC belongs to.
	 * @return The number of SPC reviews by the SPC at this conference.
	 */
	private static int getSpcReviewCountByUserId(int the_spc_id, int the_conference_id)
	{
		int returnable = 0;
		
		connect();
		
		try 
		{
			statement = connection.createStatement();
			
			ResultSet res = statement.executeQuery( "SELECT paper_id FROM conference_paper"
													+ " WHERE user_id = " + the_spc_id
													+ " AND conference_id = " + the_conference_id);
							
			while( res.next() ) {
				returnable++;
			}
			
		} catch (SQLException e) 
		{
			
			e.printStackTrace();
		}
		
		closeConnect();
		
		return returnable;
	}
	
	/**
	 * Returns the SPC of a paper by referencing the paper's corresponding 
	 * SPC Review.
	 * 
	 * @author 			John Jackson
	 * @param paper		ID of the paper the sought SPC was assigned.
	 * @return			User object of the SPC.
	 */
	public static User getSPCByPaper( Paper paper ) {
		User returnable = new User();
		
		connect();
		
		try {
			statement = connection.createStatement();
			
			//Get user_id corresponding to the paper.
			ResultSet res = statement.executeQuery( "SELECT user_id FROM spcreview"
													+ " WHERE paper_id = " + paper.myID);
			
			//Get a user corresponding to the ID found and add it to the returnable
			//list.
			if( res.next() ) {
				returnable = getUser( res.getInt( "user_id" ));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
			
		closeConnect();
		
		return returnable;
	}	
	
	/**
	 * WARNING, USE UNIQUE TITLES WITH THIS METHOD
	 * This method takes a paper, the conference ID, and an author ID and inserts the relevant information into
	 * the paper table and then the conference_paper table.
	 * 
	 * @author James Nance
	 * @param the_paper The paper object to be added to the paper table.
	 * @param the_author_id The author id to be added to the conference_paper table.
	 * @param the_conference_id The conference id to be added to the conference_paper table.
	 * @throws SQLException 
	 */
	private static void insertPaper(Paper the_paper, int the_author_id, int the_conference_id) throws SQLException
	{
		int paper_id = -1;
		
		connect();
			
		statement = connection.createStatement();
		//putting the paper into the paper table
		statement.executeUpdate("INSERT INTO paper VALUES (" + null + ", '" + the_paper.myAuthor + "', '"+ the_paper.myTitle + "', '"+ the_paper.myAbstract +"', '"+ the_paper.myCategory+"', '"+ the_paper.myPath + "')");
	
		//ResultSet res = statement.getGeneratedKeys(); Oh I wished you worked
		ResultSet res = statement.executeQuery("SELECT * FROM paper WHERE title = '" + the_paper.myTitle + "'");
	
		while (res.next())
		{
			paper_id = res.getInt("paper_id");
		}
		//putting the shared foreign keys into the conference paper table
		statement.executeUpdate("INSERT INTO conference_paper VALUES (" + the_conference_id + ", "+ paper_id +", " + the_author_id +")");
		
		res = statement.executeQuery("SELECT * FROM conference_user WHERE user_id =" + the_author_id + " AND conference_id = " + the_conference_id);
		//We are checking if the author is already associated with this conference.  If so we just update their author role field.
		//If not we need to create a row in conference_user for them. 
		if (res.next())
		{
			statement.executeUpdate("UPDATE conference_user SET author_role = 1 WHERE user_id = " + the_author_id + " AND conference_id = " + the_conference_id);
		}
		else
		{
			statement.executeUpdate("INSERT INTO conference_user VALUES(" + the_conference_id +", " + the_author_id + ", 1, 0, 0, 0)");
		}
		
		closeConnect();
		OBSERVABLE.notifyMainView();
	}
	
	/**
	 * Updates a paper entry in the database.
	 * 
	 * @author 				John Jackson
	 * @param paper			The paper entry to be changed.		
	 * @throws SQLException
	 */
	private static void updatePaper( Paper paper ) throws SQLException {
		connect();
		
		statement = connection.createStatement();
		
		//Update the entry in the database.
		statement.executeUpdate( "UPDATE paper SET author = '"+ paper.myAuthor
								 + "', title = '" + paper.myTitle
								 + "', abstract = '" + paper.myAbstract
								 + "', category = '" + paper.myCategory
								 + "', file_path = '" + paper.myPath 
								 + "' WHERE paper_id = " + paper.myID );
		
		closeConnect();
	}
	
	/**
	 * Returns a list of papers for an Subprogram Chair in a specified conference.
	 * 
	 * @author 					John Jackson
	 * @param spc_user_id		ID of the Subprogram Chair.
	 * @param conference_id		ID of the Conference.
	 * @return					A list of papers.
	 * @throws SQLException
	 */
	private static List<Paper> getPapersBySPC(int spc_user_id, int conference_id) throws SQLException {
		List<Integer> paper_ids = new ArrayList<Integer>();
		
		connect();
		
		statement = connection.createStatement();

		//Find paper_id(s) where the user_id and the conference_id match.
		ResultSet res = statement.executeQuery( "SELECT paper_id FROM spcreview"
												+ " WHERE user_id = " + spc_user_id
												+ " AND conference_id = " + conference_id);
		
		//Add the ID(s) to a list of IDs
		while( res.next() ) {
			paper_ids.add(  res.getInt( "paper_id" ));
			
		}
		
		closeConnect();
		
		//Return a list of Paper objects for the ID(s) found.
		return getAllPapersByPaperId( paper_ids );
	}
	
	/**
	 * Returns a list of papers this reviewer is responsible for in this conference.
	 * 
	 * @author				John Jackson
	 * @param user			The reviewer.
	 * @return				List<Paper> of papers assigned to user for review.
	 * @throws SQLException
	 */
	private static List<Paper> getPapersByReviewer( int reviewer_id, int conference_id ) throws SQLException {
		connect();
		
		statement = connection.createStatement();
		
		//Find paper_id where the user_id and conference_id match in the table.
		ResultSet res = statement.executeQuery( "SELECT paper_id FROM review"
												+ " WHERE user_id = " + reviewer_id 
												+ " AND conference_id = " + conference_id );
		
		List<Integer> paper_ids = new LinkedList<Integer>();
		
		//Add the ID to the list.
		if( res.next() ) {
			paper_ids.add( res.getInt( "paper_id" ));
		}
		
		closeConnect();
		
		//Return the list of Paper objects.
		return getAllPapersByPaperId( paper_ids );
	}
	
	/**
	 * The method for inserting or updating a paper.
	 * 
	 * @author Jesse Bostic
	 * @param thePaper The paper being committed.
	 * @param userID The user committing the paper.
	 * @param conferenceID The conference the paper is being committed to.
	 */
	public static void commitPaper(Paper thePaper, User theUser) {
		try {
			if(paperExists(thePaper)) {
				updatePaper(thePaper);
			} else {
				insertPaper(thePaper, theUser.myID, theUser.getConferenceID());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	
		
		OBSERVABLE.notifyMainView();
	}
	
	/**
	 * Attempt to overcome non-static restrictions of observable interface.
	 * 	(Called with "OBSERVABLE.notifyMainView()")
	 * 
	 * @author Jesse Bostic
	 */
	public void notifyMainView() {
		OBSERVABLE.setChanged();
		OBSERVABLE.notifyObservers();
	}
	
	/**
	 * Returns a boolean indicating an author has exceeded the number of allowable entries per conference.
	 * 
	 * @author James Nance
	 * @param the_user_id The user ID of the author.
	 * @param the_conference_id	The ID of the conference to be submitted to.
	 * @return A boolean indicating whether an author has exceeded the number of allowable 
	 * 		   entries per conference.
	 */
	public static boolean checkAuthorLimit(int the_user_id, int the_conference_id)
	{
		return GetPaperCountByAuthorAndConference(the_user_id, the_conference_id) < AUTHOR_PAPER_LIMIT;
	}
	
	/**
	 * Checks to see if the spc can have more papers assigned to them
	 * 
	 * @author James Nance
	 * @param the_spc_id the SPC being checked.
	 * @param the_conference_id The conference the SPC belongs to.
	 * @return True if the SPC is under the SPC review limit, false otherwise.
	 */
	public static boolean checkSpcReviewLimit(int the_spc_id, int the_conference_id)
	{
		return getSpcReviewCountByUserId(the_spc_id, the_conference_id) < SPC_REVIEW_LIMIT;
	}
}
