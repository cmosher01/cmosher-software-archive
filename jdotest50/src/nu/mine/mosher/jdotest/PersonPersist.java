import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

/**
 * @author Chris Mosher
 * Created: Feb 18, 2004
 */
public class PersonPersist
{
	private final static int SIZE = 3;
	private PersistenceManagerFactory pmf;
	private PersistenceManager pm;
	private Transaction transaction;

	private Person[] people;

	private List rid = new ArrayList(SIZE);

	public PersonPersist()
	{
		Properties props = new Properties();
		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
		props.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
		props.setProperty("javax.jdo.option.ConnectionURL","jdbc:mysql:///jdotest");
		props.setProperty("javax.jdo.option.ConnectionUserName","root");
		props.setProperty("javax.jdo.option.ConnectionPassword","rootroot");
		props.setProperty("org.jpox.autoCreateTables","true");
		props.setProperty("org.jpox.validateTables","false");
		props.setProperty("org.jpox.validateConstraints","false");

		this.pmf = JDOHelper.getPersistenceManagerFactory(props);
		this.pm = this.pmf.getPersistenceManager();
	}
}
