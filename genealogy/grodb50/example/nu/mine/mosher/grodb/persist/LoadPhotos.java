/*
 * Created on Jun 30, 2006
 */
package nu.mine.mosher.grodb.persist;

import java.io.File;
import java.text.ParseException;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import nu.mine.mosher.grodb.persist.key.AssertionID;
import nu.mine.mosher.grodb.persist.key.EventID;
import nu.mine.mosher.grodb.persist.key.PersonaID;
import nu.mine.mosher.grodb.persist.key.PlaceID;
import nu.mine.mosher.grodb.persist.key.RoleID;
import nu.mine.mosher.grodb.persist.key.SearchID;
import nu.mine.mosher.grodb.persist.key.SourceID;
import nu.mine.mosher.grodb.persist.proxy.Proxies;
import nu.mine.mosher.grodb.persist.types.EventType;
import nu.mine.mosher.grodb.persist.types.LatLong;
import nu.mine.mosher.grodb.persist.types.RoleType;
import nu.mine.mosher.grodb.persist.types.Surety;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.uuid.UUIDFactory;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.persist.EntityStore;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.model.AnnotationModel;
import com.sleepycat.persist.model.EntityModel;

public class LoadPhotos
{
	/**
	 * @param args
	 * @throws DatabaseException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws DatabaseException
	{
		Environment env = null;
		EntityStore store = null;
		try
		{
			env = initEnvironment();
			store = initEntityStore(env);
		}
		finally
		{
			closeEntityStore(store);
			closeEnvironment(env);
		}

	}

	private static Environment initEnvironment() throws DatabaseException
	{
		final EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setTransactional(true);

		return new Environment(new File("persistdb"),envConfig);
	}

	private static void closeEnvironment(final Environment env)
	{
		if (env != null)
		{
			try
			{
				env.close();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}
	}

	private static EntityStore initEntityStore(final Environment env) throws DatabaseException
	{
		final EntityModel model = new AnnotationModel();
		Proxies.register(model);

		final StoreConfig storeConfig = new StoreConfig();
		storeConfig.setModel(model);
		storeConfig.setAllowCreate(true);
		storeConfig.setTransactional(true);

		return new EntityStore(env,"persistdb",storeConfig);
	}

	private static void closeEntityStore(final EntityStore store)
	{
		if (store != null)
		{
			try
			{
				store.close();
			}
			catch (final Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
}
