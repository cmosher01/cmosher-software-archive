package nu.mine.mosher.gedcom;

public class InvalidLevel extends Exception
{
    public InvalidLevel(final GedcomLine line)
    {
        super("GEDCOM line has invalid level number at line: "+line);
    }
}
