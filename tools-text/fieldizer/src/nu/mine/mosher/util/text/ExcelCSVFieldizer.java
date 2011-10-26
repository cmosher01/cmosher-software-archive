/*
 * Created on April 9, 2004
 */
package nu.mine.mosher.util.text;

import java.util.Collection;
import nu.mine.mosher.util.text.exception.IllegalQuoteException;
import nu.mine.mosher.util.text.exception.UnmatchedQuoteException;

/**
 * Uses <code>ExcelCSVParser</code> to parse a String.
 * Typically you use this class by passing the CSV line
 * into the constructor, then calling <code>iterator</code>
 * to get an <code>Iterator</code> that iterates over
 * each field.
 * 
 * @author Chris Mosher
 */
public class ExcelCSVFieldizer
{
    private final String sCSVRow;



    /**
     * Initializes the <code>ExcelCSVFieldizer</code> to parse the given <code>String</code>.
     * @param sCSVRow the CSV line to be parsed.
     */
    public ExcelCSVFieldizer(final String sCSVRow)
    {
        this.sCSVRow = sCSVRow;
    }

    /**
     * Returns the original string (passed into the constructor).
     * @return the String
     */
    public String getString()
    {
        return this.sCSVRow;
    }

    /**
     * Parses the CSV line passed into the constructor and adds the
     * resulting fields to the given <code>Collection</code>.
     * @param addTo
     * @throws IllegalQuoteException
     * @throws UnmatchedQuoteException
     */
    public void getFields(final Collection<String> addTo) throws IllegalQuoteException, UnmatchedQuoteException
    {
    	final StringBuilder str = new StringBuilder(this.sCSVRow);
        final ExcelCSVParser parser = new ExcelCSVParser(str);
        while (parser.getNextPos() >= 0)
        {
	        addTo.add(parser.getOneValue());
        }
    }
}
