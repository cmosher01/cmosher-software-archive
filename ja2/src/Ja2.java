import nu.mine.mosher.ja2.Application;
import nu.mine.mosher.ja2.CommandLineArgHandler;
import nu.mine.mosher.ja2.ExceptionHandler;
import nu.mine.mosher.ja2.GUI;

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
    private Ja2() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws Throwable
    {
        ExceptionHandler eh = new ExceptionHandler();
        CommandLineArgHandler ch = new CommandLineArgHandler(args);
        GUI gui = new GUI();

        Application app = new Application(eh,ch,gui);

        app.run();
    }
}
