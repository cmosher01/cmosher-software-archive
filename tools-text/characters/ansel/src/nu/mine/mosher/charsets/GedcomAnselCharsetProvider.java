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
	private final Charset charset;

    /**
     * 
     */
    public GedcomAnselCharsetProvider()
    {
		this.charset = new GedcomAnselCharset();
    }

    @Override
	public Iterator<Charset> charsets()
    {
		final Set<Charset> set = new HashSet<Charset>(1,1);
		set.add(this.charset);
		return set.iterator();
    }

    @Override
	public Charset charsetForName(final String charsetName)
    {
		if (!charsetName.equalsIgnoreCase(GedcomAnselCharset.name))
		{
			return null;
		}

		return this.charset;
    }
}
