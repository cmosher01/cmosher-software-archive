package nu.mine.mosher.gedcom;

public class InvalidLevel extends Exception
{
    public InvalidLevel(GedcomLine line)
    {
        super("GEDCOM line has invalid level number at line: "+line);
    }
}
