package nu.mine.mosher.gedcom;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

/**
 * Handles reading in a GEDCOM file and parsing into an
 * internal representation.
 * 
 * Still a work-in-progress.
 *
 * @author Chris Mosher
 */
public class Gedcom
{
	private Gedcom()
	{
		throw new UnsupportedOperationException();
	}

	public static void analyze(InputStream in) throws IOException
	{
		List<Integer> curline = new ArrayList<Integer>(256);
		boolean hasStrange = false;
		BufferedInputStream bis = new BufferedInputStream(in);
		int c = bis.read();
		while (c != -1)
		{
			if (c==0x0d || c==0x0a)
			{
				if (hasStrange)
				{
					dumpLine(curline);
				}
				curline.clear();
				hasStrange = false;
			}

			curline.add(c);
			if (c >= 0x80)
			{
				hasStrange = true;
			}

			c = bis.read();
		}
	}

	/**
     * @param curline
     */
    private static void dumpLine(List<Integer> curline)
    {
		for (final int c : curline)
		{
			System.out.print((char)c);
		}
		System.out.println();
		for (final int c : curline)
        {
            System.out.print(Integer.toHexString(c));
            System.out.print(" ");
        }
		System.out.println();
    }

    public static String guessCharset(InputStream in) throws IOException
	{
		// read first four bytes of input stream
		int b0 = in.read();
		if (b0==-1)
			return "";
		int b1 = in.read();
		if (b1==-1)
			return "";
		int b2 = in.read();
		if (b2==-1)
			return "";
		int b3 = in.read();
		if (b3==-1)
			return "";

		// build a word from the first two bytes,
		// assuming little-endian byte order
		int w0 = b1;
		w0 <<= 8;
		w0 |= b0;

		// build a longword from the first four bytes,
		// assuming little-endian byte order
		int i0 = b3;
		i0 <<= 8;
		i0 |= b2;
		i0 <<= 8;
		i0 |= b1;
		i0 <<= 8;
		i0 |= b0;

		if (i0==0x0000feff || i0==0x00000030)
		{
			return "UTF-32LE";
		}

		if (i0==0xfffe0000 || i0==0x30000000)
		{
			return "UTF-32BE";
		}

		if (w0==0xfeff || w0==0x0030)
		{
			return "UTF-16LE";
		}

		if (w0==0xfffe || w0==0x3000)
		{
			return "UTF-16BE";
		}

		if (b0==0xef && b1==0xbb && b2==0xbf)
		{
			return "UTF-8";
		}
		return "";
//		SortedMap mc = Charset.availableCharsets();
//		for (Iterator i = mc.entrySet().iterator(); i.hasNext();)
//        {
//            Map.Entry entry = (Map.Entry)i.next();
//            String csn = (String)entry.getKey();
//			System.out.println(csn);
//            Charset cs = Charset.forName(csn);
//            Set als = cs.aliases();
//            for (Iterator j = als.iterator(); j.hasNext();)
//            {
//                String al = (String)j.next();
//                System.out.println("    "+al);
//            }
//        }

		/*
		 * windows-1252   IBM WINDOWS, ANSI
		 * Cp850          IBM DOS, IMBPC
		 * MacRoman       MACINTOSH
		 * x-gedcom-ansel ANSEL
		 * UTF-16BE       (detected automatically)
		 * UTF-16LE       (detected automatically)
		 */
	}
}
