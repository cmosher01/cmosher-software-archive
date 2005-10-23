/*
 * Created on Oct 17, 2005
 */
package nu.mine.mosher.sudoku.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

public class ScriptTest extends TestCase
{
	public void testSome() throws IOException
	{
		final BufferedReader sr = new BufferedReader(new StringReader(createTestInputString()));
		final Script s = new Script(sr,'#');
		final List<String> rLine = new ArrayList<String>();
		s.appendLines(rLine);
		assertEquals(6,rLine.size());
		assertEquals("abc",rLine.get(0));
		assertEquals("def",rLine.get(1));
		assertEquals("ghi",rLine.get(2));
		assertEquals("jkl",rLine.get(3));
		assertEquals("mno",rLine.get(4));
		assertEquals("p q r",rLine.get(5));
	}

	private String createTestInputString()
	{
		return
			"   abc   # comment\n"+
			"def\n"+
			"   ghi\n"+
			"jkl   \n"+
			"   # ignore this line   \n"+
			"mno#junk\n"+
			" p q r # ok ";
	}
}
