import nu.mine.mosher.swingapp.ApplicationAborting;
import nu.mine.mosher.swingapp.CommandLineArgHandler;
import nu.mine.mosher.swingapp.ExceptionHandler;
import nu.mine.mosher.swingapp.SwingApplication;
import nu.mine.mosher.swingapp.SwingGUI;

/*
 * Created on Feb 8, 2005
 */

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class MainPrototype
{
    /**
     * This class is never instantiated.
     */
    private MainPrototype()
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
