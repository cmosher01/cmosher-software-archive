/*
 * Created on Mar 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.util.Iterator;
import java.util.List;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Item
{
	private String name;
	private List rmmThing;

	public Item()
	{
	}

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

	public Iterator rThings()
	{
		return new Iter(rmmThing);
	}

	private static class Iter implements Iterator
	{
		private final Iterator iter;

		public Iter(List rmmThing)
		{
			this.iter = rmmThing.iterator();
		}

        public boolean hasNext()
        {
        	return this.iter.hasNext();
        }

        public Object next()
        {
        	return ((Rel)this.iter.next()).getThing();
        }

		public void remove() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}
	}
}
