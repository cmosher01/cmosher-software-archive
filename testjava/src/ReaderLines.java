import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ReaderLines /* TODO implements Iterable */
{
    private final Iterator i;
    
    public ReaderLines(Reader r) throws NoSuchElementException
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

        private Iter(Reader r) throws NoSuchElementException
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

        private void nextLine() throws NoSuchElementException
        {
            try
            {
                this.nextLine = this.r.readLine();
            }
            catch (IOException cause)
            {
                this.nextLine = null;
                NoSuchElementException e = new NoSuchElementException();
                e.initCause(cause);
                throw e;
            }
        }

        public boolean hasNext()
        {
            return nextLine != null;
        }

        public Object next() throws NoSuchElementException
        {
            if (!hasNext())
            {
                throw new NoSuchElementException();
            }
            String thisLine = this.nextLine;
            nextLine();
            return thisLine;
        }

        public void remove() /* TODO for SimpleIterator, don't provide this method */
        {
            throw new UnsupportedOperationException();
        }
    }
}
