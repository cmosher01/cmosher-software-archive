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
	}

	public void persistPeople()
	{
		// create an array of Person's
		this.people = new Person[SIZE];

		// create three people
		this.people[0] = new Person("test 1");
		this.people[1] = new Person("test 2");
		this.people[2] = new Person("test 3");

		// persist the array of people
		PersistenceManager pm = this.pmf.getPersistenceManager();
		Transaction transaction = pm.currentTransaction();
		pm.makePersistentAll(this.people);
		transaction.commit();
		// retrieve the object ids for the persisted objects
		for (int i = 0; i < this.people.length; i++)
		{
			this.rid.add(pm.getObjectId(this.people[i]));
		}
		// close current persistence manager to ensure that
		// objects are read from the db not the persistence
		// manager's memory cache.
		pm.close();
	}

	public void display()
	{
		Person person;
		// get a new persistence manager
		PersistenceManager pm = this.pmf.getPersistenceManager();
		// retrieve objects from datastore and display
		for (int i = 0; i < SIZE; i++)
		{
			person = (Person)pm.getObjectById(rid.get(i),false);
			System.out.println("Name      : " + person.getName());
			System.out.println("Address   : " + person.getAddress());
			System.out.println("SSN       : " + person.getSsn());
			System.out.println("Email     : " + person.getEmail());
			System.out.println("Home Phone: " + person.getHomePhone());
			System.out.println("Work Phone: " + person.getWorkPhone());
		}
		pm.close();
	}
}
