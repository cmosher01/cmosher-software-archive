/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.ja2;

import nu.mine.mosher.thread.CubbyHole;

/**
 * Handles exceptions for the main application.
 * 
 * @author Chris Mosher
 */
public class ExceptionHandler
{
    private final CubbyHole mException = new CubbyHole();

    /**
     * Sends an exception to the program's main thread
     * for handling.
     * 
     * @param exception
     */
    public void send(Throwable exception)
    {
        mException.put(new ApplicationAborting("The application is aborting",exception));
    }

    /**
     * Waits for another thread to send this thread
     * an exception, then throws it.
     * 
     * @throws ApplicationAborting
     */
    public void waitFor() throws ApplicationAborting
    {
        throw (ApplicationAborting)mException.remove();
    }
}
