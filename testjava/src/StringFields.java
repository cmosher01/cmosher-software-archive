import java.util.Iterator;

public class SF
{
    private final String s;
    private int pos;

    public SF(String s)
    {
        this.s = s;
    }

    public Iterator iterator()
    {
        return new Iter();
    }

    private class Iter implements Iterator
    {
        public boolean hasNext()
        {
            return pos <= s.length();
        }

        public Object next()
        {
            return null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
