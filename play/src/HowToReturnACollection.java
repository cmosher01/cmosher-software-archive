import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/*
 * Created on Aug 3, 2005
 */
public class HowToReturnACollection
{
	private final Collection<Long> rLong = new ArrayList<Long>();
	// we assume that the Object being held (Long, in this case) is immutable



	public HowToReturnACollection()
	{
		rLong.add(1L);
		rLong.add(2L);
		rLong.add(3L);
	}

	public String toString()
	{
		final StringBuffer sb = new StringBuffer();
		for (Long x: this.rLong)
		{
			sb.append(x);
			sb.append(" ");
		}
		return sb.toString();
	}



	public Iterator<Long> getUsingIterator()
	{
		return Collections.unmodifiableCollection(this.rLong).iterator();
	}



	public Long[] getAsNewArray()
	{
		return this.rLong.toArray(new Long[this.rLong.size()]);
	}


	public List<Long> getAsList() // <------------------------------------------ (alternative) BEST
	{
		return Collections.unmodifiableList((List<Long>)this.rLong);
	}

	public Collection<Long> getAsCollection() // <------------------------------------------ BEST
	{
		return Collections.unmodifiableCollection(this.rLong);
	}

	public void addToCollection(final Collection<Long> addTo)
	{
		addTo.addAll(this.rLong);
	}



	public static void main(final String[] rArg)
	{
		//now let's try calling it

		final HowToReturnACollection i = new HowToReturnACollection();
		System.out.println(i);



		// if I want to iterator over them:



		for (final Iterator<Long> usingIterator = i.getUsingIterator(); usingIterator.hasNext();)
		{
			Long x = usingIterator.next();
			System.out.println("get iterator: "+x);
		}



		final Long[] rUsingArray = i.getAsNewArray();
		for (int j = 0; j < rUsingArray.length; j++)
		{
			Long x = rUsingArray[j];
			System.out.println("get array: "+x);
		}



		for (Long x: i.getAsCollection()) // <------------------------------------------ BEST
		{
			System.out.println("get collection: "+x);
		}



		final Collection<Long> rx = new HashSet<Long>();
		i.addToCollection(rx);
		for (Long x: rx)
		{
			System.out.println("add to my set: "+x);
		}



		// if I want a whole TreeSet of them



		final SortedSet<Long> rs = new TreeSet<Long>();
		for (final Iterator<Long> usingIterator = i.getUsingIterator(); usingIterator.hasNext();)
		{
			rs.add(usingIterator.next());
		}



		final SortedSet<Long> rs2 = new TreeSet<Long>(Arrays.asList(i.getAsNewArray()));



		final SortedSet<Long> rs3 = new TreeSet<Long>(i.getAsCollection()); // <------------------------------------------ BEST



		final SortedSet<Long> rs4 = new TreeSet<Long>();
		i.addToCollection(rs4);
	}
}
