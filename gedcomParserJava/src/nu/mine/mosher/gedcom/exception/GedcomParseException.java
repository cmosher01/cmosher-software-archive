package nu.mine.mosher.gedcom.exception;

import nu.mine.mosher.gedcom.GedcomLine;

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

	public GedcomParseException(String message, Throwable cause)
	{
		super(message,cause);
		this.rawGedcomLine = "";
		this.parsedGedcomLine = null;
	}

	public String getRawGedcomLine()
	{
		return this.rawGedcomLine;
	}

	public GedcomLine getParsedGedcomLine()
	{
		return this.parsedGedcomLine;
	}
}
