/*
 * Created on January 8, 2004
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
     * Waits for another thread to put an <code>Object</code> into this
     * <code>CubbyHole</code>, then (this thread) removes the <code>Object</code> from
     * this <code>CubbyHole</code> and returns it.
     * @return the <code>Object</code>; may be null;
     */
    public synchronized Object remove()
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
     * Waits for this <code>CubbyHole</code> to be empty, then
     * puts the specified <code>Object</code> into this <code>CubbyHole</code>, and
     * notifies threads waiting to get from this <code>CubbyHole</code>.
     * 
     * @param value the <code>Object</code> to put into this <code>CubbyHole</code>; can be null
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
