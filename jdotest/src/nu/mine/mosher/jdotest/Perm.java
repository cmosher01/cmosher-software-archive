/*
 * Created on Mar 6, 2004
 */
package nu.mine.mosher.jdotest;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

/**
 * @author Chris Mosher
 */
public class Perm
{
	public static PersistenceManager pm()
	{
		return JDOHelper.getPersistenceManagerFactory(AppInit.getJDOProperties()).getPersistenceManager();
	}
}
