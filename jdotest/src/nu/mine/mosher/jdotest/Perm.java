/*
 * Created on Mar 6, 2004
 */
package nu.mine.mosher.jdotest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.naming.NamingException;

/**
 * @author Chris Mosher
 */
public class Perm
{
	private static final Properties props = new Properties();

	static
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

	public static PersistenceManager pm()
	{
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
		return pmf.getPersistenceManager();
	}
}
