/*
 * Created on Mar 6, 2004
 */
package nu.mine.mosher.jdotest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

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
		return JDOHelper.getPersistenceManagerFactory(props).getPersistenceManager();
	}
}
