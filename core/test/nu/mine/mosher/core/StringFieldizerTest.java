package nu.mine.mosher.core;

import junit.framework.TestCase;

/**
 * @author Chris Mosher
 */
public class StringFieldizerTest extends TestCase
{

    /**
     * Constructor for StringFieldizerTest.
     * @param arg0
     */
    public StringFieldizerTest(String arg0)
    {
        super(arg0);
    }

    /*
     * Test for void StringFieldizer(String)
     */
    public void testStringFieldizerString()
    {
    	StringFieldizer sf;

		sf = new StringFieldizer("");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer("test");
		assertTrue(sf.hasMoreTokens());
		assertEquals("test",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer(",");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer(",,,");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer("thisisatest,");
		assertTrue(sf.hasMoreTokens());
		assertEquals("thisisatest",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer(",thisisanothertest");
		assertTrue(sf.hasMoreTokens());
		assertEquals("",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("thisisanothertest",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer("a,b,c");
		assertTrue(sf.hasMoreTokens());
		assertEquals("a",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("b",sf.nextToken());
		assertTrue(sf.hasMoreTokens());
		assertEquals("c",sf.nextToken());
		assertFalse(sf.hasMoreTokens());

		sf = new StringFieldizer(",this,is,,a,test,");
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
