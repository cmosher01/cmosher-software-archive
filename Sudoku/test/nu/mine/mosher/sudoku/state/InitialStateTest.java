/*
 * Created on Oct 8, 2005
 */
package nu.mine.mosher.sudoku.state;

import nu.mine.mosher.sudoku.state.InitialState;
import junit.framework.TestCase;

public class InitialStateTest extends TestCase
{
	/*
	 * Test method for 'nu.mine.mosher.sudoku.InitialState.createFromString(String)'
	 */
	public void testSimpleString()
	{
		final String stringRep = "001002003004005006007008009010020030040050060070080090100200300400500600700800900";
		final InitialState state = InitialState.createFromString(stringRep);
		assertEquals(stringRep,state.toString());
	}
}
