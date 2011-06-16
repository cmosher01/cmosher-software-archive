/*
 * Created on Nov 19, 2007
 */
package util;

import junit.framework.TestCase;

public class UtilTest extends TestCase
{
	public void testDivideRound()
	{
		assertEquals(1,Util.divideRound(7,7));
		assertEquals(1,Util.divideRound(7,6));
		assertEquals(1,Util.divideRound(7,5));
		assertEquals(2,Util.divideRound(7,4));
		assertEquals(2,Util.divideRound(7,3));
		assertEquals(4,Util.divideRound(7,2));
		assertEquals(7,Util.divideRound(7,1));

		assertEquals(1,Util.divideRound(1,1));
		assertEquals(2,Util.divideRound(2,1));
		assertEquals(3,Util.divideRound(3,1));

		assertEquals(2,Util.divideRound(3,2));

		assertEquals(3,Util.divideRound(349,100));
		assertEquals(4,Util.divideRound(351,100));

		assertEquals(3,Util.divideRound(353,101));
		assertEquals(4,Util.divideRound(354,101));
	}
}
