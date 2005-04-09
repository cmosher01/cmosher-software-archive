package nu.mine.mosher.core;

import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public final class Comparer
{
	private Comparer()
	{
		assert false : "can't instantiate";
	}

	public static<T extends Comparable<T>> void compare(SortedSet<T> setOld, SortedSet<T> setNew, Comparator<T> c, Updater<T> upd) throws UpdateException
	{
		if (!setOld.comparator().equals(setNew.comparator()))
		{
			throw new IllegalArgumentException("the two sets must have comparators that are equal");
		}

		Iterator<T> iOld = setOld.iterator();
		Iterator<T> iNew = setNew.iterator();
		T objOld = null;
		T objNew = null;
		boolean needOld = true;
		boolean needNew = true;
		while (needOld || needNew)
		{
			if (needOld)
			{
				objOld = getNext(iOld);
			}
			if (needNew)
			{
				objNew = getNext(iNew);
			}
			needOld = needNew = false;
			if (objOld != null || objNew != null)
			{
				int cmp;
				if (objOld == null)
				{
					cmp = 1;
				}
				else if (objNew == null)
				{
					cmp = -1;
				}
				else
				{
					cmp = c.compare(objOld,objNew);
				}

				if (cmp < 0)
				{
					needOld = true;
					upd.delete(objOld);
				}
				else if (cmp > 0)
				{
					needNew = true;
					upd.insert(objNew);
				}
				else
				{
					needOld = true;
					needNew = true;
					upd.update(objOld,objNew);
				}
			}
		}
	}

	private static<T> T getNext(Iterator<T> i)
	{
		T obj;

		if (i.hasNext())
		{
			obj = i.next();
		}
		else
		{
			obj = null;
		}

		return obj;
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
