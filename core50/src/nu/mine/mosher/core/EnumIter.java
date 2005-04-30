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
public class EnumIter implements Iterator
{
    private final Enumeration e;

    public EnumIter(Enumeration e)
    {
        this.e = e;
    }

    public boolean hasNext()
    {
        return e.hasMoreElements();
    }

    public Object next() throws NoSuchElementException
    {
        return e.nextElement();
    }

    public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
