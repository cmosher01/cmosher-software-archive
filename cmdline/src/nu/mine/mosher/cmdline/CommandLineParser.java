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
    private final String[] mrArg;

    public CommandLineParser(String[] rArg)
    {
        mrArg = rArg;
    }

    public Iterator getArguments()
    {
        return new Iter();
    }

    private static class Iter implements Iterator
    {
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
}
