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
    public FieldsCommandLine(String[] rArg)
    {
        super(rArg);
    }

    /**
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
