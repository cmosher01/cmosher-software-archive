/*
 * Created on Mar 12, 2004
 */
package nu.mine.mosher.jdotest;

import javax.jdo.JDOHelper;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @author Chris Mosher
 */
public class RequestInit implements ServletRequestListener
{
    public void requestInitialized(ServletRequestEvent rre)
    {
        rre.getServletRequest().setAttribute(
			"nu.mine.mosher.jdotest.Perm",
        	JDOHelper.getPersistenceManagerFactory(AppInit.getJDOProperties()).getPersistenceManager());
    }

	public void requestDestroyed(ServletRequestEvent rre)
	{
	}
}
