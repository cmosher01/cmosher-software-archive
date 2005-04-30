package nu.mine.mosher.core;

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
	public static<T extends Comparable<T>> void compare(final SortedSet<T> setOld, final SortedSet<T> setNew, final Comparator<T> c, final Updater<T> upd) throws UpdateException
	{
		if (!setOld.comparator().equals(setNew.comparator()))
		{
			throw new IllegalArgumentException("the two sets must have comparators that are equal");
		}

		compare(setOld.iterator(),setNew.iterator(),c,upd);

//		final Have<T> have = new Have<T>();
//		final Need need = new Need();
//
//		while (need.needEither())
//		{
//			if (need.needOld())
//			{
//				have.setOld(getNext(iOld));
//			}
//			if (need.needNew())
//			{
//				have.setNew(getNext(iNew));
//			}
//
//			need.clearBoth();
//			if (have.haveEither())
//			{
//				final int cmp = have.compareUsing(c);
//				if (cmp < 0)
//				{
//					need.setOld();
//					upd.delete(have.getOld());
//				}
//				else if (cmp > 0)
//				{
//					need.setNew();
//					upd.insert(have.getNew());
//				}
//				else
//				{
//					need.setBoth();
//					upd.update(have.getOld(),have.getNew());
//				}
//			}
//		}

//		/*
//		 * objOld and objNew must be maintained across loop
//		 * iterations, so must be defined here, outside
//		 * the loop, not inside the loop.
//		 */
//		T objOld = null;
//		T objNew = null;
//		final Need need = new Need();
//		while (need.needEither())
//		{
//			if (need.needOld())
//			{
//				objOld = getNext(iOld);
//			}
//			if (need.needNew())
//			{
//				objNew = getNext(iNew);
//			}
//			need.clearBoth();
//			if (objOld != null || objNew != null)
//			{
//				final int cmp = compareObjects(c,objOld,objNew);
//				if (cmp < 0)
//				{
//					need.setOld();
//					upd.delete(objOld);
//				}
//				else if (cmp > 0)
//				{
//					need.setNew();
//					upd.insert(objNew);
//				}
//				else
//				{
//					need.setBoth();
//					upd.update(objOld,objNew);
//				}
//			}
//		}

//		boolean needOld = true;
//		boolean needNew = true;
//		while (needOld || needNew)
//		{
//			if (needOld)
//			{
//				objOld = getNext(iOld);
//			}
//			if (needNew)
//			{
//				objNew = getNext(iNew);
//			}
//			needOld = needNew = false;
//			if (objOld != null || objNew != null)
//			{
//				final int cmp = compareObjects(c,objOld,objNew);
//				if (cmp < 0)
//				{
//					needOld = true;
//					upd.delete(objOld);
//				}
//				else if (cmp > 0)
//				{
//					needNew = true;
//					upd.insert(objNew);
//				}
//				else
//				{
//					needOld = true;
//					needNew = true;
//					upd.update(objOld,objNew);
//				}
//			}
//		}
	}

	/**
	 * @param <T>
	 * @param iOld
	 * @param iNew
	 * @param c
	 * @param upd
	 * @throws UpdateException
	 */
	public static<T extends Comparable<T>> void compare(final Iterator<T> iOld, final Iterator<T> iNew, final Comparator<T> c, final Updater<T> upd) throws UpdateException
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

	/**
	 * @param cmp
	 * @param have
	 * @param upd
	 * @throws UpdateException
	 */
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

	/**
	 * @param cmp
	 * @param need
	 */
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

	/**
	 * @param iOld
	 * @param iNew
	 * @param have
	 * @param need
	 */
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
		public boolean haveEither() { return objOld != null || objNew != null; }
		public T getOld() { return objOld; }
		public T getNew() { return objNew; }
		public int compareUsing(Comparator<T> c) { return compareObjects(c,objOld,objNew); }
	}

	private static final class Need
	{
		private boolean needOld = true;
		private boolean needNew = true;
		public boolean needEither() { return needOld || needNew; }
		public boolean needOld() { return needOld; }
		public boolean needNew() { return needNew; }
		public void setOld() { needOld = true; }
		public void setNew() { needNew = true; }
//		public void setBoth() { needOld = needNew = true; }
		public void clear() { needOld = needNew = false; }
	}
	/**
	 * @param <T>
	 * @param c
	 * @param objOld
	 * @param objNew
	 * @return positive for greater than, negative for less than
	 */
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

//private static class CompareInts implements Comparator
//	{
//		public int compare(Object o1, Object o2)
//		{
//            System.err.println("comparing "+o1+" to "+o2);
//			int i = ((Integer)o1).intValue();
//			int j = ((Integer)o2).intValue();
//			return i-j;
//		}
//		public boolean equals(Object obj)
//		{
//			return obj instanceof CompareInts;
//		}
//
//	}
//	public static void main(String[] rArg) throws Throwable
//	{
//		List listOld = new ArrayList();
//		List listNew = new ArrayList();
//
//		listOld.add(new Integer(1));
//		listOld.add(new Integer(2));
//		listOld.add(new Integer(5));
//		listOld.add(new Integer(7));
//		listOld.add(new Integer(8));
//
//		listNew.add(new Integer(2));
//		listNew.add(new Integer(6));
//		listNew.add(new Integer(7));
//
//		SortedSet s1 = new TreeSet(new CompareInts());
//		s1.addAll(listOld);
//		SortedSet s2 = new TreeSet(new CompareInts());
//		s2.addAll(listNew);
//		compare(s1,s2,new CompareInts(),
//			new Updater()
//			{
//
//			public void insert(Object objNew)
//			{
//				System.err.println("insert "+objNew);
//			}
//			public void update(Object objOld, Object objNew)
//			{
//				System.err.println("update "+objOld+" to "+objNew);
//			}
//			public void delete(Object objOld)
//			{
//				System.err.println("delete "+objOld);
//			}
//		});
//	}
}
