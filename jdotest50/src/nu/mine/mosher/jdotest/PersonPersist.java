package nu.mine.mosher.jdotest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.jpox.store.QueryResult;

/**
 * @author Chris Mosher
 * Created: Feb 18, 2004
 */
public class PersonPersist
{
	private final static int SIZE = 3;
	private PersistenceManagerFactory pmf;

	private List rid = new ArrayList(SIZE);

	public PersonPersist()
	{
		Properties props = new Properties();
		props.setProperty("javax.jdo.PersistenceManagerFactoryClass","org.jpox.PersistenceManagerFactoryImpl");
		props.setProperty("javax.jdo.option.ConnectionDriverName","com.mysql.jdbc.Driver");
		props.setProperty("javax.jdo.option.ConnectionURL","jdbc:mysql:///jdotest");
		props.setProperty("javax.jdo.option.ConnectionUserName","root");
		props.setProperty("javax.jdo.option.ConnectionPassword","");
		props.setProperty("org.jpox.autoCreateTables","true");
		props.setProperty("org.jpox.validateTables","false");
		props.setProperty("org.jpox.validateConstraints","false");

		this.pmf = JDOHelper.getPersistenceManagerFactory(props);
	}

	public void persistPeople()
	{
		// create an array of Person's
		Person[] people = new Person[SIZE];

		// create three people
		people[0] = new Person("test 1");
		people[1] = new Person("test 2");
		people[2] = new Person("test 3");

		// persist the array of people
		PersistenceManager pm = this.pmf.getPersistenceManager();
		Transaction transaction = pm.currentTransaction();
		transaction.begin();
		pm.makePersistentAll(people);
		transaction.commit();
		// retrieve the object ids for the persisted objects
		for (int i = 0; i < people.length; i++)
		{
			this.rid.add(pm.getObjectId(people[i]));
		}
		// close current persistence manager to ensure that
		// objects are read from the db not the persistence
		// manager's memory cache.
		pm.close();
	}

	public void display()
	{
		// get a new persistence manager
		PersistenceManager pm = this.pmf.getPersistenceManager();
		Transaction transaction = pm.currentTransaction();
		transaction.begin();

		Query q = pm.newQuery(Person.class,"");
		Collection result = (Collection)q.execute();
		for (Iterator i = result.iterator(); i.hasNext(); )
		{
			i.next();
		}
	
}
		System.out.println(result.getClass().getName());


		// retrieve objects from datastore and display
//		for (int i = 0; i < SIZE; i++)
//		{
//			Object oid = this.rid.get(i);
//			Person person = (Person)pm.getObjectById(oid,false);
//			System.out.println("person ID "+oid+": "+person.getName());
//		}

		transaction.commit();
		pm.close();
	}
}
