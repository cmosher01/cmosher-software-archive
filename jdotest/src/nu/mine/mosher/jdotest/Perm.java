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
//	private static final Properties props = new Properties();
//	static
//	{
//		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
//		props.setProperty("javax.jdo.option.ConnectionDriverName","org.jpox.driver.JPOXDriver");
//		props.setProperty("javax.jdo.option.ConnectionURL","jpox:java:comp/env/jdbc/jdotest");
//		props.setProperty("javax.jdo.option.NontransactionalRead","true");
//		props.setProperty("org.jpox.autoCreateTables","true");
//		props.setProperty("org.jpox.validateTables","false");
//		props.setProperty("org.jpox.validateConstraints","false");
//	}

	private final PersistenceManager pm;

	public Perm() throws NamingException, FileNotFoundException, IOException
	{
		URL urlProps = Perm.class.getClassLoader().getResource("nu/mine/mosher/jdotest/jdo.properties");
		System.err.println("------------------------------------");
		System.err.println(urlProps.getFile());
		System.err.println("------------------------------------");
		Properties props = new Properties();
		props.load(new FileInputStream(new File(urlProps.getFile())));
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(props);
		this.pm = pmf.getPersistenceManager();
	}

	public PersistenceManager pm()
	{
		return this.pm;
	}
}
