package com.surveysampling.util;

/**
 * Breaks a string into fields. This class is similar to StringTokenizer,
 * but this class is able to return empty fields, whereas the StringTokenizer
 * class will skip over multiple occurrences of delimiters. Also, the
 * implementation of this class is much simpler than StringTokenizer's.
 * 
 * @author Chris Mosher
 */
public class StringFieldizer
{
    private final String s;
    private final char delim;
    private int pos;

    /**
     * Constructs a StringFieldizer that uses a comma for
     * the delimiter.
     * @param s the String to break into fields
     */
    public StringFieldizer(String s)
    {
        this(s,',');
    }

    /**
     * Constructs a StringFieldizer that breaks up the given String,
     * using the given character as a delimiter of fields.
     * @param s the String to break into fields
     * @param delim the character that delimits the fields
     */
    public StringFieldizer(String s, char delim)
    {
        this.s = s;
        this.delim = delim;
    }

    /**
     * Checks to see if nextToken can be called at least one more time.
     * @return true if any fields exist, false otherwise
     */
    public boolean hasMoreTokens()
    {
        return pos <= s.length();
    }

    /**
     * Returns the next field of the string.
     * @return the field, or an empty string if the field is empty
     */
    public String nextToken()
    {
        int i = nextPos();
        String tok = s.substring(pos,i);
        pos = i+1;
        return tok;
    }

    /**
     * Returns the current position.
     * @return the current position, that is, the position in
     * the string of the start of the field that would be returned
     * by the next call to nextToken.
     */
    public int getPosition()
    {
        return pos;
    }

    /**
     * A convenience method that returns the rest of the string.
     * For a StringFieldizer f, f.getResidue() is equivalent to
     * f.getString().substring(f.getPosition()).
     * @return a String, the rest of the given string
     */
    public String getResidue()
    {
        return s.substring(pos);
    }

    /**
     * Returns the original string (passed into the constructor).
     * @return the String
     */
    public String getString()
    {
        return s;
    }

    /**
     * Returns the delimiter (passed into the constructor).
     * @return the delimiter character
     */
    public char getDelimiter()
    {
        return delim;
    }

    protected int nextPos()
    {
        int i = s.indexOf(delim,pos);
        if (i == -1)
        {
            i = s.length();
        }
        return i;
    }
}
