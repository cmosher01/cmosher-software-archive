/*
 * Created on April 13, 2004
 */
package nu.mine.mosher.util.text;

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



    private static final int START = 0;
    private static final int FIELD = 1;
    private static final int IN_QUOTE = 2;
    private static final int ESCAPING_QUOTE = 3;
    private static final int END = 4;



    private final StringBuffer sb;

    private int state = START;
    private int pos;
    private char c;



    /**
     * Initializes a parser object to parse the
     * CSV line (given as a StringBuffer). The
     * StringBuffer is parsed starting at the
     * beginning.
     * @param sb the CSV line to be parsed
     */
    public ExcelCSVParser(StringBuffer sb)
    {
        this.sb = sb;
    }

    /**
     * 
     * Initializes a parser object to parse the
     * CSV line (given as a StringBuffer). The
     * StringBuffer is parsed starting at the
     * given starting position.
     * @param sb the CSV line to be parsed
     * @param start the position within <code>sb</code> to start parsing
     * @throws IllegalStateException if <code>start</code> is negative
     * or greater than the length of <code>sb</code>.
     */
    public ExcelCSVParser(StringBuffer sb, int start)
    {
        if (start < 0 || sb.length() < start)
        {
            throw new IllegalStateException();
        }
        this.pos = start;
        this.sb = sb;
    }



    /**
     * Returns the StringBuffer that was passed
     * in to the constructor.
     * @return the <code>StringBuffer</code> passed in to the constructor
     */
    public StringBuffer getBuffer()
    {
        return sb;
    }

    /**
     * The current position within the buffer
     * that parsing will continue at.
     * @return the next position to be parsed
     */
    public int getNextPos()
    {
        return pos;
    }

    /**
     * Parses the buffer at the current position,
     * appends the next field to the given StringBuffer,
     * and advances the current position to the
     * following field.
     * @param s the buffer to which the next field is appended
     * @throws IllegalQuoteException
     * @throws UnmatchedQuoteException if a field starts with a
     * quotation mark and is valid except for having no ending
     * quotation mark.
     */
    public void appendOneField(StringBuffer s) throws IllegalQuoteException, UnmatchedQuoteException
    {
        while (state != END)
        {
            next();
            transition(s);
        }
        state = START;
    }

    /**
     * Convenience method that calls <code>appendOneField</code>
     * with a new StringBuffer and returns the buffer as a String.
     * @return the next field
     * @throws IllegalQuoteException
     * @throws UnmatchedQuoteException
     */
    public String getOneValue() throws IllegalQuoteException, UnmatchedQuoteException
    {
        StringBuffer s = new StringBuffer(256);
        appendOneField(s);
        return s.toString();
    }



    protected void next()
    {
        if (pos >= sb.length() || pos == -1)
        {
            pos = -1;
            c = EOL;
        }
        else
        {
            c = sb.charAt(pos++);
        }
    }

    protected void transition(StringBuffer s) throws IllegalQuoteException, UnmatchedQuoteException
    {
        switch (state)
        {
            case START:
            {
                if (c == COMMA || c == EOL)
                {
                    state = END;
                }
                else if (c == QUOTE)
                {
                    state = IN_QUOTE;
                }
                else
                {
                    s.append(c);
                    state = FIELD;
                }
            }
            break;
            case FIELD:
            {
                if (c == COMMA || c == EOL)
                {
                    state = END;
                }
                else if (c == QUOTE)
                {
                    throw new IllegalQuoteException(near());
                }
                else
                {
                    s.append(c);
                }
            }
            break;
            case IN_QUOTE:
            {
                if (c == QUOTE)
                {
                    state = ESCAPING_QUOTE;
                }
                else if (c == EOL)
                {
                    throw new UnmatchedQuoteException(near());
                }
                else
                {
                    s.append(c);
                }
            }
            break;
            case ESCAPING_QUOTE:
            {
                if (c == COMMA || c == EOL)
                {
                    state = END;
                }
                else if (c == QUOTE)
                {
                    s.append(c);
                    state = IN_QUOTE;
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
        int posEnd = pos;
        if (posEnd > sb.length() || posEnd == -1)
        {
            posEnd = sb.length();
        }
        int posStart = posEnd-10;
        if (posStart < 0)
        {
            posStart = 0;
        }
        if (posStart <= posEnd)
        {
            return sb.substring(posStart, posEnd);
        }
        else
        {
            return "";
        }
    }
}
