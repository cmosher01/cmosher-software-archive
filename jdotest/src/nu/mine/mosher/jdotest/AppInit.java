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
	private static final Properties props = new Properties();

    public void contextInitialized(ServletContextEvent arg0)
    {
		try
		{
			URL urlProps = Perm.class.getClassLoader().getResource("nu/mine/mosher/jdotest/jdo.properties");
			props.load(new FileInputStream(new File(urlProps.getFile())));
		}
		catch (Throwable e)
		{
			e.printStackTrace();
		}
    }

    public void contextDestroyed(ServletContextEvent arg0)
    {
    }
}
