package nu.mine.mosher.gedcom;

public class GedcomParseException extends Exception
{
	private String rawGedcomLine;
	private GedcomLine parsedGedcomLine;

	public GedcomParseException(String message, String rawGedcomLine, GedcomLine parsedGedcomLine)
	{
		super(message);
		this.rawGedcomLine = rawGedcomLine;
		this.parsedGedcomLine = parsedGedcomLine;
	}

	public GedcomParseException(String message, Exception cause)
	{
		super(message,cause);
		this.rawGedcomLine = "";
		this.parsedGedcomLine = null;
	}

	public String getRawGedcomLine()
	{
		return rawGedcomLine;
	}

	public GedcomLine getParsedGedcomLine()
	{
		return parsedGedcomLine;
	}
}
