package nu.mine.mosher.charsets;

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Chris Mosher
 */
public class GedcomAnselCharsetProvider extends CharsetProvider
{
	private Charset charset;

    public GedcomAnselCharsetProvider()
    {
		charset = new GedcomAnselCharset();
    }

    public Iterator charsets()
    {
		Set set = new HashSet(1);
		set.add(charset);
		return set.iterator();
    }

    public Charset charsetForName(String charsetName)
    {
    	Charset cs = null;

		if (charsetName.equalsIgnoreCase(GedcomAnselCharset.name))
			cs = charset;

		return cs;
    }
}
