/*
 * Created on Mar 14, 2004
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
public class NamedDefault implements Named
{
    public String getName()
    {
        return "";
    }

    public String getId()
    {
        return pm.getObjectId(this).toString();
    }
}
