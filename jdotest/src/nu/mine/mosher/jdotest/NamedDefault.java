/*
 * Created on Mar 14, 2004
 */
package nu.mine.mosher.jdotest;

import javax.jdo.JDOHelper;

/**
 * @author Chris
 */
public abstract class NamedDefault implements Named
{
    public String getId()
    {
        return JDOHelper.getObjectId(this).toString();
    }

	public abstract String getName();
}
