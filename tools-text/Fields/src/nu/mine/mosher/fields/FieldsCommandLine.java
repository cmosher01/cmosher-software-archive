/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.fields;

import nu.mine.mosher.swingapp.CommandLineArgHandler;
import nu.mine.mosher.swingapp.InvalidCommandLine;

/**
 * Handles any command line arguments for the application
 * 
 * @author Chris Mosher
 */
public class FieldsCommandLine extends CommandLineArgHandler
{
    /**
     * Initializes this <code>FieldsCommandLine</code> to parse
     * the given arguments (usually the arguments specified on
     * the command line that executed this program).
     * @param rArg
     */
    public FieldsCommandLine(String[] rArg)
    {
        super(rArg);
    }

    /**
     * Parses the command line. In this case, we do not allow
     * any arguments on the command line.
     * @throws InvalidCommandLine
     */
    public void parse() throws InvalidCommandLine
    {
        if (mrArg.length > 0)
        {
            throw new InvalidCommandLine();
        }
    }
}
