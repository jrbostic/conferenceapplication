package junittest;

import static org.junit.Assert.*;
import objects.Conference;

import org.junit.Before;
import org.junit.Test;

public class ConferenceTest {
	
	private Conference myConference; 
	private final int myID = 21;
	private final String myName = "A Conference";

	@Before
	public void setUp() throws Exception {
		myConference = new Conference(myID, myName);
	}

	@Test
	public void testConference() {
		assertEquals("ID", myConference.myID, myID);
		assertEquals("name", myConference.myName, myName);
		
	}

	@Test
	public void testToString() {
		assertEquals("toString", myConference.toString(), "my ID: 21, my name: A Conference");
	}

}
