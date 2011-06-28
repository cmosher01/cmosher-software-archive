/*
 * Created on Feb 8, 2005
 */

import nu.mine.mosher.fields.FieldsCommandLine;
import nu.mine.mosher.fields.FieldsGUI;
import nu.mine.mosher.swingapp.ApplicationAborting;
import nu.mine.mosher.swingapp.CommandLineArgHandler;
import nu.mine.mosher.swingapp.ExceptionHandler;
import nu.mine.mosher.swingapp.SwingApplication;
import nu.mine.mosher.swingapp.SwingGUI;

/**
 * Contains the "main" method (external entry point) for
 * the application. This class is in the default package
 * so that the program can be run with the following command:
 * <code>java Fields [arguments]</code>
 * 
 * @author Chris Mosher
 */
public final class Fields
{
    /**
     * This class is never instantiated.
     */
    private Fields()
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
        CommandLineArgHandler ch = new FieldsCommandLine(args);
        SwingGUI gui = new FieldsGUI();

        SwingApplication app = new SwingApplication(eh,ch,gui);

        app.run();
    }
}
