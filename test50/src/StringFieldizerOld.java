import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringFieldizer implements Iterable<String>
{
	private final SimpleIterator<String> i;
	
	public StringFieldizer(String s)
	{
		i = new Iter(s);
	}

	public SimpleIterator<String> iterator()
	{
		return i;
	}

	private static class Iter implements SimpleIterator<String>
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
