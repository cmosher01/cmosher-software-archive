package nu.mine.mosher.gedcom;

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

	public static String guessCharset(InputStream in)
	{
		SortedMap mc = Charset.availableCharsets();
		for (Iterator i = mc.entrySet().iterator(); i.hasNext();)
        {
            Map.Entry entry = (Map.Entry)i.next();
            String csn = (String)entry.getKey();
            Charset cs = Charset.forName(csn);
            Set als = cs.aliases();
            for (Iterator j = als.iterator(); j.hasNext();)
            {
                String al = (String)j.next();
                System.out.println("    "+al);
            }
        }
		return "test";
	}
}
