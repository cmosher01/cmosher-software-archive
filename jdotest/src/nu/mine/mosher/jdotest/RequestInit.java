/*
 * Created on Mar 12, 2004
 */
package nu.mine.mosher.jdotest;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @author Chris Mosher
 */
public class RequestInit implements ServletRequestListener
{
    public void requestInitialized(ServletRequestEvent rre)
    {
		rre.getServletRequest().setAttribute("nu.mine.mosher.jdotest.Perm",new Perm());
    }

	public void requestDestroyed(ServletRequestEvent rre)
	{
	}
}
