/*
 * Created on Mar 11, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
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
	private static final Properties props;

    public void contextInitialized(ServletContextEvent sce)
    {
		try
		{
			props = new Properties();
			URL urlProps = Perm.class.getClassLoader().getResource("nu/mine/mosher/jdotest/jdo.properties");
			FileInputStream inProps = null;
			try
			{
				inProps = new FileInputStream(new File(urlProps.getFile()));
				props.load(inProps);
			}
			finally
			{
				if (inProps != null)
				{
					try
					{
						inProps.close();
					}
					catch (Throwable ignore)
					{
						ignore.printStackTrace();
					}
				}
			}
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
