/*
 * Created on Mar 6, 2004
 */
package nu.mine.mosher.jdotest;

import java.util.Iterator;
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
		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
		props.setProperty("javax.jdo.option.ConnectionDriverName","org.jpox.driver.JPOXDriver");
		props.setProperty("javax.jdo.option.ConnectionURL","jpox:java:comp/env/jdbc/jdotest");
		props.setProperty("javax.jdo.option.NontransactionalRead","true");
		props.setProperty("org.jpox.autoCreateTables","true");
		props.setProperty("org.jpox.validateTables","false");
		props.setProperty("org.jpox.validateConstraints","false");
	}

	private final PersistenceManager pm;

	public Perm() throws NamingException
	{
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
		this.pm = pmf.getPersistenceManager();
	}

	public PersistenceManager pm()
	{
		return this.pm;
	}
}
