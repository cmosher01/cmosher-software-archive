/*
 * Created on Mar 14, 2004
 */
package nu.mine.mosher.jdotest;

import javax.jdo.PersistenceManager;

/**
 * @author Chris
 */
public abstract class NamedDefault implements Named
{
	private transient PersistenceManager pm;

    public String getId()
    {
        return this.pm.getObjectId(this).toString();
    }

	public void setPerm(PersistenceManager pm)
	{
		this.pm = pm;
	}

	public abstract String getName();
}
