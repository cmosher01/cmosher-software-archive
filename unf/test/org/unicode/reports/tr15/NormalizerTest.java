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
    }

	protected void assertNormalize(String pre, String comp)
	{
		assertEquals(n.normalize(pre),comp);
	}

	private static void dumphex(String s)
	{
		char[] rc = s.toCharArray();
		for (int i = 0; i < rc.length; ++i)
		{
			char c = rc[i];
			String h = Integer.toHexString(c);
			System.out.print(h);
			System.out.print(" ");
		}
		System.out.println();
	}

	public static void main(String[] rArg) throws Throwable
	{
		Normalizer n = new Normalizer();
		String s = "\u212bChris";
		dumphex(s);
		String sn = n.normalize(s);
		dumphex(sn);
	}

    protected void setUp() throws Exception
    {
        super.setUp();
        n = new Normalizer();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception
    {
        // TODO Auto-generated method stub
        super.tearDown();
    }

}
