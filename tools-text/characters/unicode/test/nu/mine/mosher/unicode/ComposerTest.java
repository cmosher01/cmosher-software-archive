package nu.mine.mosher.unicode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.TestCase;

/**
 * @author Chris Mosher
 */
public class ComposerTest extends TestCase
{
    public ComposerTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ComposerTest.class);
    }

    public void testComposer() throws IOException
    {
    	Composer cm = new Composer();
    	cm.readFromFiles();

		assertComposesTo("\u00c5","\u212B",cm,false);
		assertDecomposesTo("\u0041\u030a","\u212B",cm,false);
		assertDecomposesTo("\u0041\u030a","\u00c5",cm,false);

		// from Unicode tr15 annex 1:
		// 1e0a D-dot_above
		// 1e0c D-dot_below
		// 0307 dot_above
		// 0323 dot_below
		// 031b horn
		// a
		assertDecomposesTo("D\u0307","\u1e0a",cm,false);
		assertDecomposesTo("D\u0307","\u1e0a",cm,true);
		assertComposesTo("\u1e0a","\u1e0a",cm,false);
		assertComposesTo("\u1e0a","\u1e0a",cm,true);
		// b
		assertDecomposesTo("D\u0307","D\u0307",cm,false);
		assertDecomposesTo("D\u0307","D\u0307",cm,true);
		assertComposesTo("\u1e0a","D\u0307",cm,false);
		assertComposesTo("\u1e0a","D\u0307",cm,true);
		// c
		assertDecomposesTo("D\u0323\u0307","\u1e0c\u0307",cm,false);
		assertDecomposesTo("D\u0323\u0307","\u1e0c\u0307",cm,true);
		assertComposesTo("\u1e0c\u0307","\u1e0c\u0307",cm,false);
		assertComposesTo("\u1e0c\u0307","\u1e0c\u0307",cm,true);
		// d
		assertDecomposesTo("D\u0323\u0307","\u1e0a\u0323",cm,false);
		assertDecomposesTo("D\u0323\u0307","\u1e0a\u0323",cm,true);
		assertComposesTo("\u1e0c\u0307","\u1e0a\u0323",cm,false);
		assertComposesTo("\u1e0c\u0307","\u1e0a\u0323",cm,true);
		// e
		assertDecomposesTo("D\u0323\u0307","D\u0307\u0323",cm,false);
		assertDecomposesTo("D\u0323\u0307","D\u0307\u0323",cm,true);
		assertComposesTo("\u1e0c\u0307","D\u0307\u0323",cm,false);
		assertComposesTo("\u1e0c\u0307","D\u0307\u0323",cm,true);
		// f
		assertDecomposesTo("D\u031b\u0323\u0307","D\u0307\u031b\u0323",cm,false);
		assertDecomposesTo("D\u031b\u0323\u0307","D\u0307\u031b\u0323",cm,true);
		assertComposesTo("\u1e0c\u031b\u0307","D\u0307\u031b\u0323",cm,false);
		assertComposesTo("\u1e0c\u031b\u0307","D\u0307\u031b\u0323",cm,true);

        assertDecomposesTo("\ue000","\ue000",cm,true);
        assertDecomposesTo("\ue000","\ue000",cm,false);


		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("NormalizationTest.txt"))));
		String32[] rCol = new String32[5];
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
					String32 seq = parseHexString(tok);
					rCol[iCol++] = seq;
				}

//				#    NFC
//				#      c2 ==  NFC(c1) ==  NFC(c2) ==  NFC(c3)
//				#      c4 ==  NFC(c4) ==  NFC(c5)
//				#
//				#    NFD
//				#      c3 ==  NFD(c1) ==  NFD(c2) ==  NFD(c3)
//				#      c5 ==  NFD(c4) ==  NFD(c5)
//				#
//				#    NFKC
//				#      c4 == NFKC(c1) == NFKC(c2) == NFKC(c3) == NFKC(c4) == NFKC(c5)
//				#
//				#    NFKD
//				#      c5 == NFKD(c1) == NFKD(c2) == NFKD(c3) == NFKD(c4) == NFKD(c5)

				assertComposesTo(rCol[1],rCol[0],cm,false);
				assertComposesTo(rCol[1],rCol[0],cm,false);
				assertComposesTo(rCol[1],rCol[1],cm,false);
				assertComposesTo(rCol[1],rCol[2],cm,false);
				assertComposesTo(rCol[3],rCol[3],cm,false);
				assertComposesTo(rCol[3],rCol[4],cm,false);

				assertDecomposesTo(rCol[2],rCol[0],cm,false);
				assertDecomposesTo(rCol[2],rCol[1],cm,false);
				assertDecomposesTo(rCol[2],rCol[2],cm,false);
				assertDecomposesTo(rCol[4],rCol[3],cm,false);
				assertDecomposesTo(rCol[4],rCol[4],cm,false);

				assertComposesTo(rCol[3],rCol[0],cm,true);
				assertComposesTo(rCol[3],rCol[1],cm,true);
				assertComposesTo(rCol[3],rCol[2],cm,true);
				assertComposesTo(rCol[3],rCol[3],cm,true);
				assertComposesTo(rCol[3],rCol[4],cm,true);

				assertDecomposesTo(rCol[4],rCol[0],cm,true);
				assertDecomposesTo(rCol[4],rCol[1],cm,true);
				assertDecomposesTo(rCol[4],rCol[2],cm,true);
				assertDecomposesTo(rCol[4],rCol[3],cm,true);
				assertDecomposesTo(rCol[4],rCol[4],cm,true);
				cDone++;
			}
		}
		in.close();
		System.out.println("processed NormalizationText.txt lines: "+cDone);
    }

	/**
     * @param start
     * @return
     */
    private boolean isHexDigit(char c)
    {
        return
            ('0' <= c && c <= '9') ||
            ('a' <= c && c <= 'f') ||
            ('A' <= c && c <= 'F');
    }

    private void assertComposesTo(String sexpectedPost, String spre, Composer cm, boolean compat)
	{
		String32 pre = String32.fromUTF16(spre);
		String32 expectedPost = String32.fromUTF16(sexpectedPost);
		assertEquals(expectedPost,cm.compose(pre,compat));
	}

	private void assertDecomposesTo(String sexpectedPost, String spre, Composer cm, boolean compat)
	{
		String32 pre = String32.fromUTF16(spre);
		String32 expectedPost = String32.fromUTF16(sexpectedPost);
		assertEquals(expectedPost,cm.decompose(pre,compat));
	}

	private void assertComposesTo(String32 expectedPost, String32 pre, Composer cm, boolean compat)
	{
		assertEquals(expectedPost,cm.compose(pre,compat));
	}

    private void assertDecomposesTo(String32 expectedPost, String32 pre, Composer cm, boolean compat)
    {
		assertEquals(expectedPost,cm.decompose(pre,compat));
    }

	private static String32 parseHexString(String sHex)
	{
		return parseHexString(new StringTokenizer(sHex));
	}

	private static String32 parseHexString(StringTokenizer st)
	{
		List listInteger = new ArrayList(10);
		while (st.hasMoreTokens())
		{
			String tok = st.nextToken();
			listInteger.add(new Integer(Integer.parseInt(tok,16)));
		}
		int[] r = intListToArray(listInteger);
		return new String32(r);
	}

	private static int[] intListToArray(List listInteger)
	{
		final int n = listInteger.size();

		int[] r = new int[n];
		for (int i = 0; i < n; ++i)
		{
			Integer j = (Integer)listInteger.get(i);
			r[i] = j.intValue();
		}

		return r;
	}
//	private static String tohex(String s)
//	{
//		StringBuffer sb = new StringBuffer(s.length()*6);
//
//		char[] rc = s.toCharArray();
//		for (int i = 0; i < rc.length; ++i)
//		{
//			char c = rc[i];
//			sb.append(Integer.toHexString(c));
//			sb.append(" ");
//		}
//		return sb.toString();
//	}
}
