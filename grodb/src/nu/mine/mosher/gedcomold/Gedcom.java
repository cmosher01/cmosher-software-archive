package nu.mine.mosher.gedcom;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
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
            String cs = (String)entry.getKey();
            System.out.println(cs);
        }
		return "test";
	}
}
