/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.ja2;

import javax.swing.SwingUtilities;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class SwingApplication
{
//    private Ja2GUI mGUI;
    private static SwingApplication me;
    private ExceptionHandler mExceptionHandler;
    private CommandLineArgHandler mCommandLineArgHandler;
    private GUI mGUI;



    public /*static*/ void thrown(Throwable e)
    {
        if (me == null)
        {
            RuntimeException re = new IllegalStateException("Application has not been initialized.");
            re.initCause(e);
            throw re;
        }
        me.mExceptionHandler.send(e);
    }

    /**
     * @param eh
     * @param ch
     */
    public SwingApplication(ExceptionHandler eh, CommandLineArgHandler ch, GUI gui)
    {
        me = this;

        this.mExceptionHandler = eh;
        this.mCommandLineArgHandler = ch;
        this.mGUI = gui;

        if (
            this.mExceptionHandler == null ||
            this.mCommandLineArgHandler == null ||
            this.mGUI == null)
        {
            throw new IllegalStateException("Arguments cannot be null.");
        }
    }

    /**
     * @throws Throwable
     */
    public void run() throws Throwable
    {
        mCommandLineArgHandler.parse();

        /*
         * Start the GUI, making sure all Swing calls
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
                    createGUI();
                }
                catch (Throwable th)
                {
                    thrown(th);
                }
            }
        });

        /*
         * Wait for an exception (from another thread), and throw it.
         * This also prevents the main thread from exiting before
         * Swing's event-dispatching thread starts (which would have
         * caused the program to exit immediately without doing
         * anything) (a race condition).
         */
        mExceptionHandler.waitFor();
    }

    protected void createGUI()
    {
        mGUI.create();
    }

    public CommandLineArgHandler getCommandLineArgHandler()
    {
        return mCommandLineArgHandler;
    }
}
