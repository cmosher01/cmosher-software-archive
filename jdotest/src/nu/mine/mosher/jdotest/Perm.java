/*
 * Created on Mar 6, 2004
 */
package nu.mine.mosher.jdotest;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * @author Chris Mosher
 */
public class Perm
{
	private static final Properties props = new Properties();
	static
	{
		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
		props.setProperty("javax.jdo.option.ConnectionDriverName","org.jpox.driver.JPOXDriver");
		props.setProperty("javax.jdo.option.ConnectionURL","jpox:comp/env/jdbc/TestDB");
		props.setProperty("javax.jdo.option.ConnectionUserName","tomcat");
		props.setProperty("javax.jdo.option.ConnectionPassword","tomcat");
		props.setProperty("org.jpox.autoCreateTables","true");
		props.setProperty("org.jpox.validateTables","false");
		props.setProperty("org.jpox.validateConstraints","false");
	}

	private PersistenceManagerFactory pmf;

	public Perm()
	{
		this.pmf = JDOHelper.getPersistenceManagerFactory(props);
	}

	
}
