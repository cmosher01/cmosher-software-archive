public class StringFieldizer
{
    private final String s;
    private final char delim;
    private int pos;

    public StringFieldizer(String s)
    {
        this(s,',');
    }

    public StringFieldizer(String s, char delim)
    {
        this.s = s;
        this.delim = delim;
    }

    public boolean hasMoreTokens()
    {
        return pos <= s.length();
    }

    public String nextToken()
    {
        int i = nextPos();
        String tok = s.substring(pos,i);
        pos = i+1;
        return tok;
    }

    public int getPosition()
    {
        return pos;
    }

    public char getDelimiter()
    {
        return delim;
    }

    public String getRemainder()
    {
        return s.substring(pos);
    }

    public String getString()
    {
        return s;
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
