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
    public String getName()
    {
        return "";
    }

    public String getId()
    {
    	PersistenceManager pm;
        return pm.getObjectId(this).toString();
    }
}
