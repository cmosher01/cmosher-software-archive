import java.util.Iterator;

public class StringFields /* implements Iterable */
{
    private final Iterator i;
    
    public StringFields(String s)
    {
        i = new Iter(s);
    }

    public Iterator iterator()
    {
        return i;
    }

    private static class Iter implements Iterator
    {
        private final String s;
        private int pos;

        private Iter(String s)
        {
            if (this.s != null)
            {
                throw new UnsupportedOperationException();
            }
            this.s = s;
        }

        public boolean hasNext()
        {
            return pos <= s.length();
        }

        public Object next()
        {
            int i = s.indexOf(',',pos);
            if (i == -1)
            {
                i = s.length();
            }
            String tok = s.substring(pos,i);
            pos = i+1;
            return tok;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
