/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Rel
{
	private Item item;
	private Thing thing;
	public Rel()
	{
	}
    /**
     * @return
     */
    public Item getItem()
    {
        return item;
    }

    /**
     * @return
     */
    public Thing getThing()
    {
        return thing;
    }

    /**
     * @param item
     */
    public void setItem(Item item)
    {
        this.item = item;
    }

    /**
     * @param thing
     */
    public void setThing(Thing thing)
    {
        this.thing = thing;
    }

}
