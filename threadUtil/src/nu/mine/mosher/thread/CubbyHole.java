/*
 * Created on Jul 15, 2004
 */
package nu.mine.mosher.thread;

/**
 * A thread-safe "cubby hole" container for one <code>Object</code>.
 * 
 * @author Chris Mosher
 */
public class CubbyHole
{
    private Object mContents;
    private boolean mHasContents;

    /**
     * Waits for another thread to put and object into this
     * cubbyhole, then (this thread) removes the object from
     * this cubbyhole and returns it.
     * @return the Object; may be null;
     */
    public synchronized Object get()
    {
        Object contents = null;
        try
        {
            while (!mHasContents)
            {
                wait();
            }
            contents = mContents;
            mHasContents = false;
            notifyAll();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        mContents = null;

        return contents;
    }

    /**
     * Puts the specified object into this cubbyhole, and
     * notifies threads waiting to get from this cubbyhole.
     * 
     * @param value the Object to put into this cubbyhole; can be null
     */
    public synchronized void put(Object value)
    {
        try
        {
            while (mHasContents)
            {
                wait();
            }
            mContents = value;
            mHasContents = true;
            notifyAll();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
