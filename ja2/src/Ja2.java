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
     * @throws UnsupportedOperationException
     */
    private Ja2() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
