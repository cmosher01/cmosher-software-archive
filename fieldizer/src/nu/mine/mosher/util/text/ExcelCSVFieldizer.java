/*
 * TODO
 *
 * Created on Apr 9, 2004
 */
package nu.mine.mosher.util.text;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Uses <code>ExcelCSVParser</code> to parse a String.
 * Typically you use this class by passing the CSV line
 * into the constructor, then calling <code>iterator</code>
 * to get an <code>Iterator</code> that iterates over
 * each field.
 */
public class ExcelCSVFieldizer
{
    private final String sCSVRow;



    /**
     * Initializes the fieldizer to parse the given String.
     * @param sCSVRow the CSV line to be parsed.
     */
    public ExcelCSVFieldizer(String sCSVRow)
    {
        this.sCSVRow = sCSVRow;
    }

    /**
     * Returns the original string (passed into the constructor).
     * @return the String
     */
    public String getString()
    {
        return sCSVRow;
    }

    /**
     * Returns an <code>Iterator</code> that iterates over
     * each field.
     * 
     * Any exceptions that occur during parsing will be
     * nested inside a <code>NoSuchElementException</code>
     * exception when calling the iterator's <code>next</code> method.
     * 
     * @return Iterator that iterates over each field
     */
    public Iterator iterator()
    {
        return new FieldIterator(new StringBuffer(sCSVRow));
    }

    private static class FieldIterator implements Iterator
    {
        private final ExcelCSVParser parser;

        public FieldIterator(StringBuffer s)
        {
            parser = new ExcelCSVParser(s);
        }

        public boolean hasNext()
        {
            return parser.getNextPos() >= 0;
        }

        public Object next() throws NoSuchElementException
        {
            try
            {
                return parser.getOneValue();
            }
            catch (Throwable cause)
            {
                NoSuchElementException e = new NoSuchElementException();
                e.initCause(cause);
                throw e;
            }
        }

        public void remove() throws UnsupportedOperationException
        {
            throw new UnsupportedOperationException();
        }
    }
}
