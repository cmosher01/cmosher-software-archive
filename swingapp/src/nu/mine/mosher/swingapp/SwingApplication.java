/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.swingapp;

import javax.swing.SwingUtilities;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class SwingApplication
{
    private ExceptionHandler mExceptionHandler;
    private CommandLineArgHandler mCommandLineArgHandler;
    private SwingGUI mGUI;



    /**
     * @param eh
     * @param ch
     * @param gui
     */
    public SwingApplication(ExceptionHandler eh, CommandLineArgHandler ch, SwingGUI gui)
    {
        this.mExceptionHandler = eh;
        this.mCommandLineArgHandler = ch;
        this.mGUI = gui;

        assert this.mExceptionHandler != null :
            "Argument eh to SwingApplication constructor cannot be null";
        assert this.mCommandLineArgHandler != null :
            "Argument ch to SwingApplication constructor cannot be null";
        assert this.mGUI != null :
            "Argument gui to SwingApplication constructor cannot be null";
    }

    /**
     * @throws ApplicationAborting
     */
    public void run() throws ApplicationAborting
    {
        try
        {
            /*
             * Parse the command line (possibly doing
             * some other processing).
             */
            getCommandLineArgHandler().parse();

            /*
             * Start the SwingGUI, making sure all Swing calls
             * are done on the event-dispatching thread.
             * Catch any exceptions and pass them back to
             * the main thread.
             */
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    try
                    {
                        getGUI().create();
                    }
                    catch (Throwable e)
                    {
                        getExceptionHandler().send(e);
                    }
                }
            });

        }
        catch (Throwable e)
        {
            getExceptionHandler().send(e);
        }

        /*
         * Wait for an exception (from another thread), and throw it.
         * This also prevents the main thread from exiting before
         * Swing's event-dispatching thread starts (which would have
         * caused the program to exit immediately without doing
         * anything) (a race condition).
         */
        getExceptionHandler().waitFor();
    }

    public SwingGUI getGUI()
    {
        return this.mGUI;
    }

    public ExceptionHandler getExceptionHandler()
    {
        return this.mExceptionHandler;
    }

    public CommandLineArgHandler getCommandLineArgHandler()
    {
        return this.mCommandLineArgHandler;
    }
}
