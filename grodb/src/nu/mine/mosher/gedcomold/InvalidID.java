package nu.mine.mosher.gedcomold;

public class InvalidID extends GedcomParseException
{
	public InvalidID(String rawGedcomLine, GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line has an invalid ID",rawGedcomLine,parsedGedcomLine);
	}
}
