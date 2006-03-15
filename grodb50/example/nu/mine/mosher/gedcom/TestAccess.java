/*
 * Created on Dec 2, 2005
 */
package nu.mine.mosher.gedcom;

import java.io.File;
import nu.mine.mosher.grodb2.datatype.Source;
import nu.mine.mosher.grodb2.datatype.SourceIDBinding;
import nu.mine.mosher.grodb2.datatype.SourceRel;
import nu.mine.mosher.grodb2.datatype.SourceRelBinding;
import nu.mine.mosher.grodb2.datatype.SourceRelChild;
import nu.mine.mosher.grodb2.datatype.SourceRelParent;
import nu.mine.mosher.grodb2.datatype.UUIDBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.ForeignKeyDeleteAction;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class TestAccess
{
	private static final String ENV_DIR = "testGroDB2";

	public static final Transaction NO_PARENT = null;



	public static Environment openEnvironment(final boolean allowWrite) throws DatabaseException
	{
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setTransactional(true);
		conf.setReadOnly(!allowWrite);

		return new Environment(new File(ENV_DIR),conf);
	}

	public static void closeEnvironment(final Environment environment)
	{
		try
		{
			environment.cleanLog();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
		try
		{
			environment.close();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
		}
	}

	public static Database openDatabaseSource(final Environment env, boolean allowWrite) throws DatabaseException
	{
		final TransactionConfig confTxn = new TransactionConfig();
		confTxn.setReadCommitted(true);
		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);

		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setReadOnly(!allowWrite);

        final Database db = env.openDatabase(txn,Source.class.getSimpleName(),confDB);
        // TODO catch exceptions and txn.abort()
        txn.commit();

        return db;
	}

	public static boolean environmentExists()
	{
		final File env = new File(ENV_DIR);
		return env.isDirectory();
	}

	public static void createEnvironment() throws DatabaseException
	{
		final File envdir = new File(ENV_DIR);
		envdir.mkdirs();
		
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setAllowCreate(true);
		conf.setTransactional(true);

		Environment env = null;
		try
		{
			env = new Environment(new File(ENV_DIR),conf);
			createDatabases(env);
		}
		finally
		{
			if (env != null)
			{
				try
				{
					env.cleanLog();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
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
	}

	private static void createDatabases(final Environment env) throws DatabaseException
	{
		final TransactionConfig confTxn = new TransactionConfig();
		confTxn.setReadCommitted(true);
		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);



		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setAllowCreate(true);
        final Database dbSource = env.openDatabase(txn,Source.class.getSimpleName(),confDB);



        final Database dbSourceRel = env.openDatabase(txn,SourceRel.class.getSimpleName(),confDB);

        final SecondaryConfig confDB2 = new SecondaryConfig();
        confDB2.setTransactional(true);
        confDB2.setAllowCreate(true);
        confDB2.setSortedDuplicates(true);
        confDB2.setAllowPopulate(true);
        confDB2.setImmutableSecondaryKey(true);
        confDB2.setForeignKeyDatabase(dbSource);
        confDB2.setForeignKeyDeleteAction(ForeignKeyDeleteAction.CASCADE);
        confDB2.setKeyCreator(new SourceRelChild(new SourceRelBinding(new SourceIDBinding(new UUIDBinding()))));
        final SecondaryDatabase idxSourceRelChild = env.openSecondaryDatabase(txn,SourceRelChild.class.getSimpleName(),dbSourceRel,confDB2);
        confDB2.setKeyCreator(new SourceRelParent(new SourceRelBinding(new SourceIDBinding(new UUIDBinding()))));
        final SecondaryDatabase idxSourceRelParent = env.openSecondaryDatabase(txn,SourceRelParent.class.getSimpleName(),dbSourceRel,confDB2);



        txn.commit();



        idxSourceRelParent.close();
        idxSourceRelChild.close();
        dbSourceRel.close();
        dbSource.close();
	}

	public static void put(final DatabaseEntry key, final DatabaseEntry data, final Database db, final Transaction txn) throws DatabaseException
	{
		final OperationStatus status = db.put(txn,key,data);

		if (!status.equals(OperationStatus.SUCCESS))
		{
			System.err.println(status);
			throw new DatabaseException();
		}
	}

}
