/*
 * Created on Mar 14, 2004
 */
package nu.mine.mosher.jdotest;

import javax.jdo.PersistenceManager;

/**
 * @author Chris
 */
public class NamedDefault implements Named
{
	private PersistenceManager pm;

    public String getName()
    {
        return "";
    }

    public String getId()
    {
    	PersistenceManager pm = null;
        return pm.getObjectId(this).toString();
    }

	public void setPerm(PersistenceManager pm)
	{
		this.pm = pm;
	}
}
