import nu.mine.mosher.ja2.ApplicationAborting;
import nu.mine.mosher.ja2.SwingApplication;
import nu.mine.mosher.ja2.CommandLineArgHandler;
import nu.mine.mosher.ja2.ExceptionHandler;
import nu.mine.mosher.ja2.SwingGUI;

/*
 * Created on Aug 31, 2004
 */

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that the program can be run with the following command:
 * <code>java Ja2 [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Ja2
{
    /**
     * This class is never instantiated.
     */
    private Ja2()
    {
        assert false : "Cannot instantiate Ja2 class.";
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
