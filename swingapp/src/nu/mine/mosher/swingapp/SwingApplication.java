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
    private final String[] rArg;

    public Ja2(String[] rArg)
    {
        this.rArg = rArg;
    }

    /**
     * 
     */
    public void run()
    {
        // We don't allow any command line arguments.
        if (rArg.length > 0)
        {
            throw new IllegalArgumentException("No arguments allowed.");
        }

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
                    MetadataViewer.sendException(th);
                }
            }
        });

        /*
         * Wait for an exception (from another thread), and throw it.
         * This also prevents the main thread from exiting before
         * Swing's event-dispatching thread starts (which would have
         * caused the program to exit immeiately without doing
         * anything) (a race conditon).
         */
        waitForException();
    }
}
