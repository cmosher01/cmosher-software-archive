package org.unicode.reports.tr15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

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
	public void testNormalizationTestTxt() throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("NormalizationTest.txt"))));
		String[] rCol = new String[5];
		int cDone = 0;
		for (String lin = in.readLine(); lin != null; lin = in.readLine())
		{
			char start = lin.charAt(0);
			if (isHexDigit(start))
			{
				int iCol = 0;
				for (StringTokenizer st = new StringTokenizer(lin,";"); iCol < 5; )
				{
					String tok = st.nextToken();
					rCol[iCol++] = hexToString(tok);
				}
				assertNormalize(rCol[1],rCol[0]);
				assertNormalize(rCol[1],rCol[1]);
				assertNormalize(rCol[1],rCol[2]);
				assertNormalize(rCol[3],rCol[3]);
				assertNormalize(rCol[3],rCol[4]);
				cDone++;
			}
		}
		in.close();
		System.out.println("processed NormalizationText.txt lines: "+cDone);
	}

    /**
     * @param tok
     * @return
     */
    private static String hexToString(String source)
    {
		StringBuffer sb = new StringBuffer();

		for (StringTokenizer st = new StringTokenizer(source); st.hasMoreTokens(); )
		{
			String tok = st.nextToken();
			sb.append((char)Integer.parseInt(tok,16));
		}

		return sb.toString();
    }

    private static boolean isHexDigit(char start)
    {
		return
			('0' <= start && start <= '9') ||
			('a' <= start && start <= 'f') ||
			('A' <= start && start <= 'F');
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
