import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringFieldizer /* TODO implements Iterable */
{
	private final Iterator i;
	
	public StringFieldizer(String s)
	{
		i = new Iter(s);
	}

	public Iterator iterator() /* TODO SimpleIterator */
	{
		return i;
	}

	private static class Iter implements Iterator /* TODO SimpleIterator */
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

		public Object next() throws NoSuchElementException
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
