/*
 * Created on Mar 6, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package nu.mine.mosher.jdotest;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Perm
{
	private PersistenceManagerFactory pmf;

	public Perm()
	{
		Properties props = new Properties();
		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
		props.setProperty("javax.jdo.option.ConnectionDriverName","org.jpox.driver.JPOXDriver");
		props.setProperty("javax.jdo.option.ConnectionURL","jpox:comp/env/jdbc/TestDB");
		props.setProperty("javax.jdo.option.ConnectionUserName","root");
		props.setProperty("javax.jdo.option.ConnectionPassword","");
		props.setProperty("org.jpox.autoCreateTables","true");
		props.setProperty("org.jpox.validateTables","false");
		props.setProperty("org.jpox.validateConstraints","false");

		this.pmf = JDOHelper.getPersistenceManagerFactory(props);
	}
}
