package nu.mine.mosher.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringFieldizer implements Iterable<String>
{
	private final String s;

	public StringFieldizer(String s)
	{
		this.s = s;
	}

	public Iterator<String> iterator()
	{
		return new Iter(s);
	}

	private static class Iter implements Iterator<String>
	{
		private final String s;
		private int pos;

		private Iter(String s) throws UnsupportedOperationException
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

		public String next() throws NoSuchElementException
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

		public void remove() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}
	}

	public String getResidue(Iterator<String> i)
	{
		int pos = ((Iter)i).pos;
	}
}
