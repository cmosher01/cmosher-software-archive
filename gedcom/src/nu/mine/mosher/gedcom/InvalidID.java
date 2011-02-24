package nu.mine.mosher.gedcom;

public class InvalidID extends GedcomParseException
{
	public InvalidID(final String rawGedcomLine, final GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line has an invalid ID",rawGedcomLine,parsedGedcomLine);
	}
}
