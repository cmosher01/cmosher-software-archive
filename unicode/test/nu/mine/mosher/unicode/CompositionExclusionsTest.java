package nu.mine.mosher.unicode;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class CompositionExclusionsTest extends TestCase
{
    private CompositionExclusions ce;

    public CompositionExclusionsTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(CompositionExclusionsTest.class);
    }

    protected void setUp() throws Exception
    {
        super.setUp();
        ce = new CompositionExclusions(new File("CompositionExclusions.txt"));
        ce.readFromFile();
    }

    public void testReadFrom() throws IOException
    {
		assertTrue(ce.is(0x0958));
		assertTrue(ce.is(0x0FB9));
		assertTrue(ce.is(0xFB38));
		assertTrue(ce.is(0x2ADC));
		assertTrue(ce.is(0x1D164));
		assertTrue(ce.is(0x1D1C0));

		assertFalse(ce.is(0x0));
		assertFalse(ce.is(0x10000));
		assertFalse(ce.is(0x2ADD));
		assertFalse(ce.is(0xFFFF));
		assertFalse(ce.is(0xFFFFFFFF));
		assertFalse(ce.is(0x7FFFFFFF));
		assertFalse(ce.is(0x80000000));
    }
}
