/*
 * Created on April 13, 2004
 */
package nu.mine.mosher.util.text;

import nu.mine.mosher.util.text.exception.IllegalQuoteException;
import nu.mine.mosher.util.text.exception.UnmatchedQuoteException;

/**
 * Parses a comma-separated value (CSV) line.
 * The CSV line is of the format of a record in an
 * Excel-style CSV file. Typically, each record in
 * the file will be passed to the constructor.
 * Then, while <code>getNextPos</code> returns a
 * non-negative value, call <code>getOneValue</code>
 * to get each field.
 * 
 * The format of the line is zero or more
 * fields separated with commas. Each field
 * can optionally be surrounded with
 * quotation marks (U+0022) (this would typically be
 * used for a field that contain commas or quotes itself).
 * A field that is surrounded with quotes can optionally
 * contain two quotation marks in a row, which signifies
 * just one quotation mark in the field. Other uses
 * of quotation marks are illegal and cause exceptions
 * to be thrown.
 * 
 * Example fields:
 * <ul>
 * <li>abc is returned as abc</li>
 * <li>"abc" is returned as abc</li>
 * <li>"ab,c" is returned as ab,c</li>
 * <li>"ab""c" is returned as ab"c</li>
 * </ul>
 * The following fields are invalid:
 * <ul>
 * <li>"abc</li>
 * <li>ab"c</li>
 * <li>"abc"c"</li>
 * </ul>
 * 
 * @author Chris Mosher
 */
public class ExcelCSVParser
{
    private static final char COMMA = ',';
    private static final char QUOTE = '\"';
    private static final char EOL = '\uFFFF';

    private static enum STATE
    {
        /** start */ START,
        /** in a normal field */ FIELD,
        /** in a quoted string */ IN_QUOTE,
        /** escaping a quote within a quoted string */ ESCAPING_QUOTE,
        /** end of finite-state machine */ END,
    }





    private final StringBuilder str;

    private STATE state = STATE.START;
    private int pos;
    private char charNext;



    /**
     * Initializes a parser object to parse the
     * CSV line (given as a <code>StringBuilder</code>). The
     * <code>StringBuilder</code> is parsed starting at the
     * beginning.
     * @param str the CSV line to be parsed
     */
    public ExcelCSVParser(final StringBuilder str)
    {
        this(str,0);
    }

    /**
     * 
     * Initializes a parser object to parse the
     * CSV line (given as a <code>StringBuilder</code>). The
     * <code>StringBuilder</code> is parsed starting at the
     * given starting position.
     * @param str the CSV line to be parsed
     * @param start the position within <code>str</code> to start parsing
     * @throws IllegalStateException if <code>start</code> is negative
     * or greater than the length of <code>str</code>.
     */
    public ExcelCSVParser(final StringBuilder str, final int start)
    {
        if (start < 0 || str.length() < start)
        {
            throw new IllegalStateException();
        }
        this.pos = start;
        this.str = str;
    }



    /**
     * Returns the <code>StringBuilder</code> that was passed
     * into the constructor.
     * @return the <code>StringBuilder</code> passed into the constructor
     */
    public StringBuilder getBuffer()
    {
        return this.str;
    }

    /**
     * The current position within the buffer
     * that parsing will continue at.
     * @return the next position to be parsed
     */
    public int getNextPos()
    {
        return this.pos;
    }

    /**
     * Parses the buffer at the current position,
     * appends the next field to the given <code>StringBuilder</code>,
     * and advances the current position to the
     * following field.
     * @param appendTo the buffer to which the next field is appended
     * @throws IllegalQuoteException
     * @throws UnmatchedQuoteException if a field starts with a
     * quotation mark and is valid except for having no ending
     * quotation mark.
     */
    public void appendOneField(final StringBuilder appendTo) throws IllegalQuoteException, UnmatchedQuoteException
    {
        while (this.state != STATE.END)
        {
            next();
            transition(appendTo);
        }
        this.state = STATE.START;
    }

    /**
     * Convenience method that calls <code>appendOneField</code>
     * with a new <code>StringBuilder</code> and returns the buffer as a String.
     * @return the next field
     * @throws IllegalQuoteException
     * @throws UnmatchedQuoteException
     */
    public String getOneValue() throws IllegalQuoteException, UnmatchedQuoteException
    {
        final StringBuilder s = new StringBuilder(256);
        appendOneField(s);
        return s.toString();
    }



    protected void next()
    {
        if (this.pos == -1 || this.str.length() <= this.pos)
        {
            this.pos = -1;
            this.charNext = EOL;
        }
        else
        {
            this.charNext = this.str.charAt(this.pos++);
        }
    }

    protected void transition(final StringBuilder appendTo) throws IllegalQuoteException, UnmatchedQuoteException
    {
        switch (this.state)
        {
            case START:
            {
                if (this.charNext == COMMA || this.charNext == EOL)
                {
                    this.state = STATE.END;
                }
                else if (this.charNext == QUOTE)
                {
                    this.state = STATE.IN_QUOTE;
                }
                else
                {
                    appendTo.append(this.charNext);
                    this.state = STATE.FIELD;
                }
            }
            break;
            case FIELD:
            {
                if (this.charNext == COMMA || this.charNext == EOL)
                {
                    this.state = STATE.END;
                }
                else if (this.charNext == QUOTE)
                {
                    throw new IllegalQuoteException(near());
                }
                else
                {
                    appendTo.append(this.charNext);
                }
            }
            break;
            case IN_QUOTE:
            {
                if (this.charNext == QUOTE)
                {
                    this.state = STATE.ESCAPING_QUOTE;
                }
                else if (this.charNext == EOL)
                {
                    throw new UnmatchedQuoteException(near());
                }
                else
                {
                    appendTo.append(this.charNext);
                }
            }
            break;
            case ESCAPING_QUOTE:
            {
                if (this.charNext == COMMA || this.charNext == EOL)
                {
                    this.state = STATE.END;
                }
                else if (this.charNext == QUOTE)
                {
                    appendTo.append(this.charNext);
                    this.state = STATE.IN_QUOTE;
                }
                else
                {
                    throw new IllegalQuoteException(near());
                }
            }
            break;
            default:
            {
                assert false : "Invalid state of parser.";
            }
        }
    }

    protected String near()
    {
        int posEnd = this.pos;
        if (posEnd == -1 || this.str.length() < posEnd)
        {
            posEnd = this.str.length();
        }
        int posStart = posEnd-10;
        if (posStart < 0)
        {
            posStart = 0;
        }

        if (posStart > posEnd)
        {
            return "";
        }

        return this.str.substring(posStart,posEnd);
    }
}
