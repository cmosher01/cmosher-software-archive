/*
 * Created on July 15, 2004
 */
package nu.mine.mosher.cmdline;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class CommandLineParser
{
    public CommandLineParser()
    {
    }

    private static class Iter implements Iterator
    {
        private final String[] mArg;

        public Iter(String[] arg)
        {
            mArg = arg;
        }

        public boolean hasNext()
        {
            return false;
        }

        public Object next() throws NoSuchElementException
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            return null;
        }

        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * @param rArg
     */
    public Iterator parse(String[] rArg)
    {
        return new Iter(rArg);
    }
}
