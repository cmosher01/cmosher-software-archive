import java.util.Iterator;
import java.util.NoSuchElementException;

public class StringFieldizer implements Iterable, Immutable
{
	private final String s;

	public StringFieldizer(String s)
	{
		this.s = s;
	}

	public SimpleIterator iterator()
	{
		return new SimpleIterator<String>(s)
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
		};
	}
}
