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
public class Ja2
{
//    private Ja2GUI mGUI;
    private static Ja2 me;
    private ExceptionHandler mExceptionHandler;
    private CommandLineArgHandler mCommandLineArgHandler;

    /**
     * @param eh
     * @param ch
     */
    public Ja2(ExceptionHandler eh, CommandLineArgHandler ch)
    {
        me = this;
        this.mExceptionHandler = eh;
        this.mCommandLineArgHandler = ch;
    }

    /**
     * @throws Throwable
     * 
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
                    Ja2.thrown(th);
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

    public static void thrown(Throwable e)
    {
        me.mExceptionHandler.send(e);
    }

    protected void createGUI()
    {
//        mGUI = new Ja2GUI();
    }
}
