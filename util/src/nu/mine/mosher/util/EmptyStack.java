/*
 * Created on Jun 18, 2004
 */
package nu.mine.mosher.util;

import java.util.EmptyStackException;

/**
 * An empty, immutable implementation of
 * the <code>Stack</code> interface.
 * 
 * @author Chris Mosher
 */
public class EmptyStack implements Stack
{
    /**
     * @throws UnsupportedOperationException
     * @see nu.mine.mosher.util.Stack#push(java.lang.Object)
     */
    public void push(Object obj)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws EmptyStackException
     * @see nu.mine.mosher.util.Stack#pop()
     */
    public Object pop()
    {
        throw new EmptyStackException();
    }

    /**
     * Returns true.
     * @see nu.mine.mosher.util.Stack#isEmpty()
     */
    public boolean isEmpty()
    {
        return true;
    }

    /**
     * @throws EmptyStackException
     * @see nu.mine.mosher.util.Stack#peek()
     */
    public Object peek()
    {
        throw new EmptyStackException();
    }

    /**
     * Returns zero.
     * @see nu.mine.mosher.util.Stack#size()
     */
    public int size()
    {
        return 0;
    }
}
