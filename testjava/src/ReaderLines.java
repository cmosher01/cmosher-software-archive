import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReaderLines /* TODO implements Iterable */
{
    private final Iterator i;
    
    public ReaderLines(Reader r) throws IOException
    {
        i = new Iter(r);
    }

    public Iterator iterator() /* TODO SimpleIterator */
    {
        return i;
    }

    private static class Iter implements Iterator /* TODO SimpleIterator */
    {
        private final BufferedReader r;
        private String nextLine;

        private Iter(Reader r) throws IOException
        {
            if (this.r != null)
            {
                throw new UnsupportedOperationException();
            }
            if (r instanceof BufferedReader)
            {
                this.r = (BufferedReader)r;
            }
            else
            {
                this.r = new BufferedReader(r);
            }
            nextLine();
        }

        private void nextLine() throws IOException
        {
            nextLine = this.r.readLine();
        }

        public boolean hasNext()
        {
            return nextLine != null;
        }

        public Object next()
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            int i = s.indexOf(',',pos);
            if (i < 0)
            {
                i = s.length();
            }
            String tok = s.substring(pos,i);
            pos = i+1;
            return tok;
        }

        public void remove() /* TODO for SimpleIterator, don't provide this method */
        {
            throw new UnsupportedOperationException();
        }
    }
}
