/*
 * Created on Mar 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class AppInit implements ServletContextListener
{
	private static Properties props;

    public void contextInitialized(ServletContextEvent sce)
    {
		try
		{
			props = new Properties();
			InputStream inProps = sce.getServletContext().getResourceAsStream("WEB-INF/jdo.properties");
			props.load(inProps);
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
    }

    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
