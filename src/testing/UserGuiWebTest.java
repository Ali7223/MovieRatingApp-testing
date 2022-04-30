package testing;

import org.junit.Before;
import org.junit.Test;

import net.sourceforge.jwebunit.junit.WebTester;

/**
 * This class performs a system test on the GuestGUI using JWebUnit.
 * 
 * @author swe.uni-due.de
 *
 */
public class UserGuiWebTest {

	private WebTester tester;

	/**
	 * Create a new WebTester object that performs the test.
	 */
	@Before
	public void prepare() {
		tester = new WebTester();
		tester.setBaseUrl("http://localhost:8080/MRApp/");
	}

	@Test
	public void testBrowseHolidayOffers() {
		// Start testing for user
		
		tester.beginAt("registered.jsp");

		// Check all components of the search form
		tester.assertTitleEquals("Registered webpage");
		tester.assertFormPresent("browse");
		tester.assertTextPresent("Add movies");
		tester.assertTextPresent("Rate movies");
		tester.assertTextPresent("Home");
		tester.assertSubmitButtonPresent("browsemovies");
		tester.clickButton("browse");
		


		// Check the representation of the table for an empty result
		tester.assertTablePresent("movielist");
		String[][] tableHeadings = { { "Name", "Main Actor", "Director", "Genre","Date", "Rating" } };
		tester.assertTableEquals("movielist", tableHeadings);
	
	}

}

