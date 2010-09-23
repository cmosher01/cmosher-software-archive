package smcsolution;

public class ParseException extends Exception
{
	public ParseException(String s, String t)
	{
		super(s+" "+t);
	}
}
