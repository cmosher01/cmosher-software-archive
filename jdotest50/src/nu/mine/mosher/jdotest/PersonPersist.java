import java.util.Properties;

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

	private List<Integer> rid = new ArrayList<Integer>(SIZE);

	public PersonPersist()
	{
		try
		{
			Properties props = new Properties();
			props.setProperty("javax.jdo.PersistenceManagerFactoryClass","com.prismt.j2ee.jdo.PersistenceManagerFactoryImpl");
			pmf = JDOHelper.getPersistenceManagerFactory(props);
			pmf.setConnectionFactory(createConnectionFactory());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
	}
}
