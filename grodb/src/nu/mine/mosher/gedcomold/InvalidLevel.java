package nu.mine.mosher.gedcom;

public class InvalidLevel extends GedcomParseException
{
	public InvalidLevel(String rawGedcomLine, GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line has an invalid level number",rawGedcomLine,parsedGedcomLine);
	}
}
