package nu.mine.mosher.gedcom;

public class IllegalLevel extends GedcomParseException
{
	public IllegalLevel(String rawGedcomLine, GedcomLine parsedGedcomLine)
	{
		super("GEDCOM line has an invalid level number",rawGedcomLine,parsedGedcomLine);
	}
}
