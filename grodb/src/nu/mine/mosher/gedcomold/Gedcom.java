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
		int b0 = in.read();
		int b1 = in.read();

		int w0 = b1;
		w0 <<= 8;
		w0 |= b0;

		if (w0==0xfeff || w0==0x0030)
		{
			return "UTF-16LE";
		}

		if (w0==0xfffe || w0==0x3000)
		{
			return "UTF-16BE";
		}

		SortedMap mc = Charset.availableCharsets();
		for (Iterator i = mc.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            String csn = (String)entry.getKey();
			System.out.println(csn);
            Charset cs = Charset.forName(csn);
            Set als = cs.aliases();
            for (Iterator j = als.iterator(); j.hasNext();)
            {
                String al = (String)j.next();
                System.out.println("    "+al);
            }
        }
		return "test";
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
