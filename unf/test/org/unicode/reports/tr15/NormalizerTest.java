package org.unicode.reports.tr15;

import junit.framework.TestCase;

public class NormalizerTest extends TestCase
{
    public NormalizerTest(String name)
    {
        super(name);
    }

    /*
     * Test for StringBuffer normalize(String, StringBuffer)
     */
    public void testNormalizeStringStringBuffer()
    {
    	Normalizer n = new Normalizer();
    	String s = "\u0041\u030a";
    	String sn = n.normalize(s);
    	dumphex(sn);
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
}
