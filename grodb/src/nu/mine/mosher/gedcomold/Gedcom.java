package nu.mine.mosher.gedcom;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

public class Gedcom
{
	private Gedcom()
	{
	}

	public static String guessCharset(InputStream in) throws IOException
	{
		// read first four bytes of input stream
		int b0 = in.read();
		int b1 = in.read();
		int b2 = in.read();
		int b3 = in.read();

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
//EF BB BF
		if (b0==0xef && b1==0xbb && b2==0xbf)
		{
			return "UTF-8";
		}
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
