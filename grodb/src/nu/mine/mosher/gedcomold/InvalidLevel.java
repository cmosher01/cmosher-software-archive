/*
 * Created on Jul 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.gedcom;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InvalidLevel extends Exception
{
    public InvalidLevel(GedcomLine line)
    {
        super("GEDCOM line has invalid level number at line: "+line);
    }
}
