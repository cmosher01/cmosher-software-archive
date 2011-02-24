package nu.mine.mosher.gedcom;

public class IllegalLevel extends GedcomParseException
{
	public IllegalLevel(final String rawGedcomLine, final GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line has an invalid level number",rawGedcomLine,parsedGedcomLine);
	}
}
