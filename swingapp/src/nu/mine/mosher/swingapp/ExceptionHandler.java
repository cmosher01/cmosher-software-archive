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
    private static final ExceptionHandler mSingleton = new ExceptionHandler();

    private final CubbyHole mException = new CubbyHole();

    private ExceptionHandler() throws IllegalStateException
    {
        if (mSingleton != null)
        {
            throw new IllegalStateException();
        }
    }



    /**
     * Waits for another thread to send this thread
     * an exception, then throws it.
     * 
     * @throws Throwable
     */
    public static void waitFor() throws Throwable
    {
        throw (Throwable)mSingleton.mException.remove();
    }

    /**
     * Sends an exception to the program's main thread
     * for handling.
     * 
     * @param exception
     */
    public static void send(Throwable exception)
    {
        mSingleton.mException.put(exception);
    }
}
