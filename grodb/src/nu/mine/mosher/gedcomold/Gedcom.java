package nu.mine.mosher.gedcomold;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Gedcom
{
	private Gedcom()
	{
	}

	public static void analyze(InputStream in) throws IOException
	{
		List curline = new ArrayList(256);
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

			curline.add(new Integer(c));
			if (c >= 0x80)
				hasStrange = true;

			c = bis.read();
		}
	}

	/**
     * @param curline
     */
    private static void dumpLine(List curline)
    {
		for (Iterator i = curline.iterator(); i.hasNext();)
		{
			Integer c = (Integer)i.next();
			System.out.print((char)c.intValue());
		}
		System.out.println();
    	for (Iterator i = curline.iterator(); i.hasNext();)
        {
            Integer c = (Integer)i.next();
            System.out.print(Integer.toHexString(c.intValue()));
            System.out.print(" ");
        }
		System.out.println();
    }

    public static String guessGedcomCharset(InputStream in) throws IOException
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

		// check BOM's for UTF-32,-16,-8 encodings
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
		 * Charset:       CHAR values in GEDCOM file:
		 * windows-1252   IBM WINDOWS, ANSI
		 * Cp850          IBM DOS, IMBPC
		 * MacRoman       MACINTOSH
		 * x-gedcom-ansel ANSEL
		 */
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"US-ASCII"));
		bin.readLine(); // ignore rest of header line

		String s = bin.readLine();
		while (s != null && s.length() > 0 && s.charAt(0) != '0')
		{
			if (s.startsWith("1 CHAR"))
			{
				s = s.toUpperCase();
				if (s.indexOf("WIN",6) >= 0)
				{
					return "windows-1252";
				}
				if (s.indexOf("ANSI",6) >= 0)
				{
					return "windows-1252";
				}
				if (s.indexOf("DOS",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("PC",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("OEM",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("ASCII",6) >= 0)
				{
					return "Cp850";
				}
				if (s.indexOf("MAC",6) >= 0)
				{
					return "MacRoman";
				}
				if (s.indexOf("ANSEL",6) >= 0)
				{
					return "x-gedcom-ansel";
				}
			}
			s = bin.readLine();
		}

		return "";
	}
}
