package org.unicode.reports.tr15;

import junit.framework.TestCase;

public class NormalizerTest extends TestCase
{
	Normalizer n;

    public NormalizerTest(String name)
    {
        super(name);
    }

    /*
     * Test for StringBuffer normalize(String, StringBuffer)
     */
    public void testNormalizeStringStringBuffer()
    {
		assertNormalize("","");
		assertNormalize("a","a");
		assertNormalize("Chris","Chris");
		assertNormalize("\u0041\u030a","\u00c5");
		assertNormalize("\u212b","\u00c5");
		assertNormalize("Henry \u2163","Henry \u2163");
		assertNormalize("\u212b","\u00c6");
    }

	protected void assertNormalize(String pre, String comp)
	{
		assertEquals(n.normalize(pre),comp);
	}

    protected void setUp() throws Exception
    {
        super.setUp();
        n = new Normalizer();
    }

    protected void tearDown() throws Exception
    {
		n = null;
        super.tearDown();
    }
}
