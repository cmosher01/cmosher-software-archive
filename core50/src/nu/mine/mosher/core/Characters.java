package nu.mine.mosher.core;

/**
 * This class consists exclusively of static methods that
 * deal with characters.
 *
 * @author Chris Mosher
 */
public final class Characters
{
	private Characters()
	{
		throw new UnsupportedOperationException();
	}

    public static boolean isHexDigit(char c)
    {
    	return
    		('0' <= c && c <= '9') ||
    		('a' <= c && c <= 'f') ||
    		('A' <= c && c <= 'F');
    }
}
