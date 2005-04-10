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
		assert false : "can't instantiate";
	}

    /**
     * Returns <code>true</code> if the given <code>char</code>
     * is 0-9 or A-F or a-f (a hexadecimal digit).
     * @param c
     * @return <code>true</code> if <code>c</code> is a hex digit
     */
    public static boolean isHexDigit(char c)
    {
    	return
    		('0' <= c && c <= '9') ||
    		('a' <= c && c <= 'f') ||
    		('A' <= c && c <= 'F');
    }
}
