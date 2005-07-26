package nu.mine.mosher.compare;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

/**
 * Has a method to compare two <code>SortedSet</code>s and report differences.
 *
 * @author Chris Mosher
 */
public final class Comparer
{
	private Comparer()
	{
		assert false : "can't instantiate";
	}

	/**
	 * @param <T>
	 * @param setOld
	 * @param setNew
	 * @param c
	 * @param upd
	 * @throws UpdateException
	 */
	public static<T> void compare(final SortedSet<T> setOld, final SortedSet<T> setNew, final Comparator<T> c, final Updater<T> upd) throws UpdateException
	{
		if (!setOld.comparator().equals(setNew.comparator()))
		{
			throw new IllegalArgumentException("the two sets must have comparators that are equal");
		}

		compare(setOld.iterator(),setNew.iterator(),c,upd);
	}

	/**
	 * @param <T>
	 * @param iOld
	 * @param iNew
	 * @param c
	 * @param upd
	 * @throws UpdateException
	 */
	public static<T> void compare(final Iterator<T> iOld, final Iterator<T> iNew, final Comparator<T> c, final Updater<T> upd) throws UpdateException
	{
		final Have<T> have = new Have<T>();
		final Need need = new Need();
		while (need.needEither())
		{
			getNext(need,iOld,iNew,have);

			need.clear();
			if (have.haveEither())
			{
				final int cmp = have.compareUsing(c);
				update(cmp,have,upd);
				setNeed(cmp,need);
			}
		}
	}

	private static<T> void update(final int cmp, final Have<T> have, final Updater<T> upd) throws UpdateException
	{
		if (cmp < 0)
		{
			upd.delete(have.getOld());
		}
		else if (cmp > 0)
		{
			upd.insert(have.getNew());
		}
		else
		{
			upd.update(have.getOld(),have.getNew());
		}
	}

	private static void setNeed(final int cmp, final Need need)
	{
		if (cmp <= 0)
		{
			need.setOld();
		}
		if (cmp >= 0)
		{
			need.setNew();
		}
	}

	private static<T> void getNext(final Need need, final Iterator<T> iOld, final Iterator<T> iNew, final Have<T> have)
	{
		if (need.needOld())
		{
			final T tOld = nextIf(iOld);
			have.setOld(tOld);
		}
		if (need.needNew())
		{
			final T tNew = nextIf(iNew);
			have.setNew(tNew);
		}
	}

	private static final class Have<T>
	{
		private T objOld;
		private T objNew;
		public void setOld(T objOld) { this.objOld = objOld; }
		public void setNew(T objNew) { this.objNew = objNew; }
		public boolean haveEither() { return this.objOld != null || this.objNew != null; }
		public T getOld() { return this.objOld; }
		public T getNew() { return this.objNew; }
		public int compareUsing(Comparator<T> c) { return compareObjects(c,this.objOld,this.objNew); }
	}

	private static final class Need
	{
		private boolean needOld = true;
		private boolean needNew = true;
		public boolean needEither() { return this.needOld || this.needNew; }
		public boolean needOld() { return this.needOld; }
		public boolean needNew() { return this.needNew; }
		public void setOld() { this.needOld = true; }
		public void setNew() { this.needNew = true; }
		public void clear() { this.needOld = this.needNew = false; }
	}

	private static<T> int compareObjects(final Comparator<T> c, final T objOld, final T objNew)
	{
		if (objOld == null)
		{
			return +1;
		}
		if (objNew == null)
		{
			return -1;
		}
		return c.compare(objOld,objNew);
	}

	private static<T> T nextIf(final Iterator<T> i)
	{
		if (!i.hasNext())
		{
			return null;
		}

		return i.next();
	}
}
