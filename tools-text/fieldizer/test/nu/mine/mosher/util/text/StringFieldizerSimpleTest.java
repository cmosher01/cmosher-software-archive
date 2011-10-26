package nu.mine.mosher.util.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Chris Mosher
 */
public class StringFieldizerSimpleTest
{
    @Test
    public void testStringFieldizerSimpleString()
    {
    	StringFieldizerSimple sf;

		sf = new StringFieldizerSimple("");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple("test");
		assertTrue(sf.hasMoreTokens());
		assertEquals("test",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple(",");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple(",,,");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple("thisisatest,");
		assertTrue(sf.hasMoreTokens());
		assertEquals("thisisatest",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple(",thisisanothertest");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("thisisanothertest",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple("a,b,c");
		assertTrue(sf.hasMoreTokens());
		assertEquals("a",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("b",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("c",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizerSimple(",this,is,,a,test,");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("this",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("is",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("a",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("test",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());
    }
}
