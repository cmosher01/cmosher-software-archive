/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.fields;

import nu.mine.mosher.swingapp.InvalidCommandLine;

/**
 * Handles any command line arguments for the application
 * 
 * @author Chris Mosher
 */
public class FieldsCommandLine
{
    private final String[] mrArg;

    public FieldsCommandLine(String[] rArg)
    {
        mrArg = rArg;
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
