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
            int i = nextPos();
            String tok = s.substring(pos,i);
            pos = i+1;
            return tok;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        protected int nextPos()
        {
            int i = s.indexOf(',',pos);
            if (i == -1)
            {
                i = s.length();
            }
            return i;
        }
    }
}
