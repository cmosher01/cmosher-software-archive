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

	private PersistenceManagerFactory pmf;

	public Perm() throws NamingException
	{
		this.pmf = JDOHelper.getPersistenceManagerFactory(props);
	}

	public Iterator getList()
	{
		PersistenceManager pm = this.pmf.getPersistenceManager();
		return pm.getExtent(Item.class,true).iterator();
	}

	public String getIDof(Object obj)
	{
		PersistenceManager pm = this.pmf.getPersistenceManager();
		return pm.getObjectId(obj).toString();
	}

	public void put(Object obj)
	{
		PersistenceManager pm = this.pmf.getPersistenceManager();
		pm.currentTransaction().begin();
		pm.makePersistent(obj);
		pm.currentTransaction().commit();
	}

	public Object get(Class permClass, String id)
	{
		PersistenceManager pm = this.pmf.getPersistenceManager();
		return pm.getObjectById(pm.newObjectIdInstance(permClass,id),true);
	}

	public PersistenceManager begin()
	{
		PersistenceManager pm = this.pmf.getPersistenceManager();
		pm.currentTransaction().begin();
		return pm;
	}

	public void commit(PersistenceManager pm)
	{
		pm.currentTransaction().commit();
	}
}
