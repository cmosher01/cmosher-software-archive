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
     * @return if has a next item to get
     */
    @Override
	public boolean hasNext()
    {
        return this.e.hasMoreElements();
    }

    /**
     * Gets next item.
     * @return next item
     * @throws NoSuchElementException
     */
    @Override
	public T next() throws NoSuchElementException
    {
        return this.e.nextElement();
    }

    /**
     * @throws UnsupportedOperationException
     */
    @Override
	public void remove() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }
}
