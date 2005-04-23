/*
 * Created on Apr 23, 2005
 */
package nu.mine.mosher.rand.seed;

import nu.mine.mosher.swingapp.ApplicationAborting;
import nu.mine.mosher.swingapp.CommandLineArgHandler;
import nu.mine.mosher.swingapp.ExceptionHandler;
import nu.mine.mosher.swingapp.SwingApplication;
import nu.mine.mosher.swingapp.SwingGUI;


/**
 * TODO
 *
 * @author Chris Mosher
 */
public class RandSeed
{
    /**
     * This class is never instantiated.
     */
    private RandSeed()
    {
        assert false : "Cannot instantiate main class.";
    }

    /**
     * @param args
     * @throws ApplicationAborting
     */
    public static void main(String[] args) throws ApplicationAborting
    {
        ExceptionHandler eh = new ExceptionHandler();
        CommandLineArgHandler ch = new CommandLineArgHandler(args);
        SwingGUI gui = new SwingGUI();

        SwingApplication app = new SwingApplication(eh,ch,gui);

        app.run();
    }
}
