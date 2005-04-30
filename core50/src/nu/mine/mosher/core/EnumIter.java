package nu.mine.mosher.core;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Adapts an Enumeration so it can be used
 * as an Iterator.
 * 
 * @author Chris Mosher
 */
public class EnumIter<T> implements Iterator<T>
{
    private final Enumeration<T> e;

    public EnumIter(final Enumeration<T> e)
    {
        this.e = e;
    }

    public boolean hasNext()
    {
        return e.hasMoreElements();
    }

    public T next() throws NoSuchElementException
    {
        return e.nextElement();
    }

    public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
