package nu.mine.mosher.core;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringFieldizer implements Iterable<String>
{
	private final String s;
	private final char delim;



	public StringFieldizer(String s)
	{
		this(s,',');
	}

	public StringFieldizer(String s, char delim)
	{
		this.s = s;
		this.delim = delim;
	}



	public Iter iterator()
	{
		return new Iter(s,delim);
	}



	public static class Iter implements Iterator<String>
	{
		private final String s;
		private final char delim;
		private int pos;

		private Iter(String s, char delim) throws UnsupportedOperationException
		{
			if (this.s != null)
			{
				throw new UnsupportedOperationException();
			}
			this.s = s;
			this.delim = delim;
		}

		/**
		 * Checks to see if <code>next</code> can be called at least one more time.
		 * @return true if any fields exist, false otherwise
		 */
		public boolean hasNext()
		{
			return pos <= s.length();
		}

		/**
		 * Returns the next field of the string.
		 * @return the field, or an empty string if the field is empty
		 */
		public String next() throws NoSuchElementException
		{
			if (!hasNext())
			{
				throw new NoSuchElementException();
			}
			int i = s.indexOf(delim,pos);
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

		/**
		 * Returns the current position.
		 * @return the current position, that is, the position in
		 * the string of the start of the field that would be returned
		 * by the next call to nextToken.
		 */
		public int getPosition()
		{
			return pos;
		}

		/**
		 * A convenience method that returns the rest of the string.
		 * For a StringFieldizer f, f.getResidue() is equivalent to
		 * f.getString().substring(f.getPosition()).
		 * @return a String, the rest of the given string
		 */
		public String getResidue()
		{
			return s.substring(pos);
		}

		/**
		 * Returns the original string (passed into the constructor).
		 * @return the String
		 */
		public String getString()
		{
			return s;
		}

		/**
		 * Returns the delimiter (passed into the constructor).
		 * @return the delimiter character
		 */
		public char getDelimiter()
		{
			return delim;
		}
	}
}
