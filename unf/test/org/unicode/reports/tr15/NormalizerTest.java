package org.unicode.reports.tr15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
    }

	/*
	 * Unicode Normalization Test Suite (see http://www.unicode.org/Public/UNIDATA/UCD.html re NormalizationTest)
	 */
	public void testNormalizationTestTxt()
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("NormalizationTest.txt"))));
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
