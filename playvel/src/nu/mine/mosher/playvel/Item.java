package nu.mine.mosher.playvel;

import javax.jdo.JDOUserException;
import javax.jdo.PersistenceManager;

public class Item extends PermItemDefault implements PermItem
{
	private String name;
	private int attrib;

	public Item()
	{
		name = "";
	}

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

	public static Item getById(PersistenceManager pm, String id) throws JDOUserException
	{
		return (Item)pm.getObjectById(pm.newObjectIdInstance(Item.class,id),true);
	}

	public int getAttrib()
	{
		return this.attrib;
	}

	public void setAttrib(int attrib)
	{
		this.attrib = attrib;
	}
}
