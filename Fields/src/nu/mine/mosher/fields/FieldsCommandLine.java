/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.ja2;

/**
 * Handles any command line arguments for the application
 * 
 * @author Chris Mosher
 */
public class CommandLineArgHandler
{
    private final String[] mrArg;

    public CommandLineArgHandler(String[] rArg)
    {
        mrArg = rArg;
    }

    /**
     * @throws InvalidCommandLine
     */
    public void parse() throws InvalidCommandLine
    {
        if (mrArg.length == 0)
        {
            throw new InvalidCommandLine();
        }
    }
}
