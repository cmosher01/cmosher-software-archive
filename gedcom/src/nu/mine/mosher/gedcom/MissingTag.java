package nu.mine.mosher.gedcom;

public class MissingTag extends GedcomParseException
{
	public MissingTag(String rawGedcomLine, GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line does not have any tag",rawGedcomLine,parsedGedcomLine);
	}
}
