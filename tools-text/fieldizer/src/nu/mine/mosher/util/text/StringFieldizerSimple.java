package nu.mine.mosher.util.text;

/**
 * Breaks a <code>String</code> into fields. This class is similar to <code>StringTokenizer</code>,
 * but this class is able to return empty fields, whereas the <code>StringTokenizer</code>
 * class will skip over multiple occurrences of delimiters. Also, the
 * implementation of this class is much simpler than <code>StringTokenizer</code>'s.
 * 
 * @author Chris Mosher
 */
public class StringFieldizerSimple
{
    private final String string;
    private final char delim;
    private int pos;



    /**
     * Constructs a <code>StringFieldizer</code> that uses a comma for
     * the delimiter.
     * @param s the String to break into fields
     */
    public StringFieldizerSimple(final String s)
    {
        this(s,',');
    }

    /**
     * Constructs a <code>StringFieldizer</code> that breaks up the given <code>String</code>,
     * using the given character as a delimiter of fields.
     * @param s the <code>String</code> to break into fields
     * @param delim the <code>char</code> that delimits the fields
     */
    public StringFieldizerSimple(final String s, final char delim)
    {
        this.string = s;
        this.delim = delim;
    }

    /**
     * Checks to see if <code>nextToken</code> can be called
     * successfully at least one more time.
     * @return true if any fields exist, false otherwise
     */
    public boolean hasMoreTokens()
    {
        return this.pos <= this.string.length();
    }

    /**
     * Returns the next field of the string.
     * @return the field, or an empty string if the field is empty
     */
    public String nextToken()
    {
    	final int i = nextPos();
    	final String tok = this.string.substring(this.pos,i);
        this.pos = i+1;
        return tok;
    }

    /**
     * Returns the current position.
     * @return the current position, that is, the position in
     * the string of the start of the field that would be returned
     * by the next call to <code>nextToken</code>.
     */
    public int getPosition()
    {
        return this.pos;
    }

    /**
     * A convenience method that returns the rest of the string.
     * For a <code>StringFieldizer f</code>, <code>f.getResidue()</code> is equivalent to
     * <code>f.getString().substring(f.getPosition())</code>.
     * @return a <code>String</code>, the rest of the given string
     */
    public String getResidue()
    {
        return this.string.substring(this.pos);
    }

    /**
     * Returns the original <code>String</code> (passed into the constructor).
     * @return the <code>String</code>
     */
    public String getString()
    {
        return this.string;
    }

    /**
     * Returns the delimiter (passed into the constructor).
     * @return the delimiter <code>char</code>
     */
    public char getDelimiter()
    {
        return this.delim;
    }

    protected int nextPos()
    {
        int i = this.string.indexOf(this.delim,this.pos);
        if (i == -1)
        {
            i = this.string.length();
        }
        return i;
    }
}
