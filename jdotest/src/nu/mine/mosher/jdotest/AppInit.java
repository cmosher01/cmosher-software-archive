/*
 * Created on Mar 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
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
    	ServletContext ctx = sce.getServletContext();
		try
		{
			props = new Properties();
			InputStream inProps = ctx.getResourceAsStream("WEB-INF/jdo.properties");
			props.load(inProps);
		}
		catch (Throwable e)
		{
			ctx.log("Could not initialize JDO properties.",e);
		}
    }

	public Properties getJDOProperties()
	{
		return props;
	}

    public void contextDestroyed(ServletContextEvent sce)
    {
    }
}
