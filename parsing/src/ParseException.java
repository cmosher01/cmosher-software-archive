/*
 * Created on Sep 23, 2005
 */
public class ParseException extends Exception
{
	public ParseException(final String expected, final String actual, int at)
	{
		super("expected: "+expected+"; found: "+actual+"; at position "+at);
	}
}
