import java.util.Iterator;

public class SF /* implements Iterable */
{
    private final String s;

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
            return false;
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
