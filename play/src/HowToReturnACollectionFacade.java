import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/*
 * Created on Aug 3, 2005
 */
public class HowToReturnACollectionFacade
{
	// not really all that good, because once we read thru the stream the
	// first time, we expend it... this will cause problems
	// But you get the idea:
	// you can implement a method: Collection<T> getAsCollection()
	// even if you don't store a (whole) Collection<T> internally
	public class Coll extends AbstractCollection<Integer> implements Collection<Integer>
	{
		public Coll()
		{
			super();
		}

		@Override
		public int size()
		{
			try
			{
				return HowToReturnACollectionFacade.this.stream.available();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			try
			{
				return HowToReturnACollectionFacade.this.stream.available() > 0;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			return false;
		}

		@Override
		public Iterator<Integer> iterator()
		{
			return new Iter(HowToReturnACollectionFacade.this.stream);
		}
		public class Iter implements Iterator<Integer>
		{
			private final InputStream strm;
			private int nextByte;

			Iter(final InputStream strm)
			{
				this.strm = strm;
				try
				{
					this.nextByte = this.strm.read();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
			}

			public boolean hasNext()
			{
				return this.nextByte >= 0;
			}

			public Integer next() throws NoSuchElementException
			{
				if (!hasNext())
				{
					throw new NoSuchElementException();
				}
				final int x = this.nextByte;
				try
				{
					this.nextByte = this.strm.read();
				}
				catch (final IOException e)
				{
					e.printStackTrace();
				}
				return x;
			}

			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		}

		@Override
		public boolean add(Integer o)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Object o)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(Collection c)
		{
			return false;
		}

		@Override
		public boolean addAll(Collection c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection c)
		{
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear()
		{
			throw new UnsupportedOperationException();
		}
	}



	final InputStream stream;

	public HowToReturnACollectionFacade(final File fileAsCollectionOfIntegers) throws FileNotFoundException
	{
		this.stream = new FileInputStream(fileAsCollectionOfIntegers);
	}

	public Collection<Integer> getAsCollection()
	{
		return new Coll();
	}



	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
	}
}
