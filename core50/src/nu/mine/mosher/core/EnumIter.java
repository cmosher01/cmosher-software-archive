package nu.mine.mosher.core;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Adapts an Enumeration so it can be used
 * as an Iterator.
 * @param <T> 
 * 
 * @author Chris Mosher
 */
public class EnumIter<T> implements Iterator<T>
{
    private final Enumeration<T> e;

    /**
     * @param e
     */
    public EnumIter(final Enumeration<T> e)
    {
        this.e = e;
    }

    /**
     * @return
     */
    public boolean hasNext()
    {
        return e.hasMoreElements();
    }

    /**
     * @return
     * @throws NoSuchElementException
     */
    public T next() throws NoSuchElementException
    {
        return e.nextElement();
    }

    /**
     * @throws UnsupportedOperationException
     */
    public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
