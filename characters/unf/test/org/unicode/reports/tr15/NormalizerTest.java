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
					System.out.print(tok);
					System.out.print(";");
					rCol[iCol++] = hexToString(tok);
				}
				System.out.println();
				assertNormalize(rCol[0],rCol[1]);
				assertNormalize(rCol[1],rCol[1]);
				assertNormalize(rCol[2],rCol[1]);
				assertNormalize(rCol[3],rCol[3]);
				assertNormalize(rCol[4],rCol[3]);
				cDone++;
			}
		}
		in.close();
		System.out.println("processed NormalizationText.txt lines: "+cDone);
	}

	private static void normTestTxt() throws Exception
	{
		Normalizer n = new Normalizer();
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
//					System.out.print(tok);
//					System.out.print(";");
					rCol[iCol++] = hexToString(tok);
				}
//				dumphex(rCol[0]);
				String norm = n.normalize(rCol[0]);
//				dumphex(norm);
//				dumphex(rCol[1]);
				if (!rCol[1].equals(norm))
				{
					System.out.println("ERROR: ");
					for (int i = 0; i < rCol.length; ++i)
						dumphex(rCol[i]);
					String bad = n.normalize(rCol[0]);
					dumphex(bad);
					System.out.println();
				}
//				System.out.println();
				cDone++;
//if (cDone>100)
//throw new Exception("stop");
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
			int cint = Integer.parseInt(tok,16);
			if (cint > 0x10FFFF)
				System.err.println("VERY BAD: we cannot convert to UTF-16 correctly: "+tok);
			if (cint > 0xffff)
			{
				// special UTF-16 encoding
				int uprime = cint - 0x10000;
				int w1 = 0xD800;
				int w2 = 0xDC00;
				w2 |= uprime & 0x3ff;
				uprime >>= 10;
				w1 |= uprime & 0x3ff;
				sb.append((char)w1);
				sb.append((char)w2);
			}
			else
			{
				sb.append((char)cint);
			}
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
		dumphex(pre);
		String norm = n.normalize(pre);
		dumphex(norm);
		dumphex(comp);
		assertEquals(comp,norm);
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

	public static void main(String[] rArg) throws Throwable
	{
		normTestTxt();
	}
}
