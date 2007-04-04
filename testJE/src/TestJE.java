import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

/*
 * Created on Nov 7, 2005
 */

/**
 * Testing Berkeley DB, Java Edition
 *
 * @author Chris Mosher
 */
public class TestJE
{
	private static final Transaction NO_PARENT = null;
	private static final TransactionConfig READ_COMMITTED = TransactionConfig.DEFAULT;
	static
	{
		READ_COMMITTED.setReadCommitted(true);
	}



	/**
	 * @param args
	 * @throws DatabaseException 
	 */
	public static void main(String[] args) throws DatabaseException
	{
		//createDatabase();
		readDatabase();
	}

	private static void readDatabase() throws DatabaseException
	{
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setTransactional(true);
		conf.setReadOnly(true);

		Environment env = null;
		try
		{
			env = new Environment(new File("test.je"),conf);
			readTable(env);
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

	private static void readTable(Environment env) throws DatabaseException
	{
		final TransactionConfig confTxn = READ_COMMITTED;

		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);

		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setReadOnly(true);

        final Database db = env.openDatabase(txn,"testArray",confDB);

        readRows(db,txn);

        txn.commit();

        db.close();
	}

	private static void readRows(Database db, Transaction txn) throws DatabaseException
	{
        readRow(db,txn,88);
        readRow(db,txn,99);
	}

	private static void readRow(Database db, Transaction txn, final int k) throws DatabaseException
	{
		final DatabaseEntry key = new DatabaseEntry();
        IntegerBinding.intToEntry(k,key);

        final DatabaseEntry data = new DatabaseEntry();
		db.get(txn,key,data,LockMode.READ_COMMITTED);

		final IntListBinding bnd = new IntListBinding();

		final List<Integer> rInt = (List<Integer>)bnd.entryToObject(data);

		System.out.println("read record with key "+k+":");
		System.out.println("  size: "+rInt.size());
		for (final int i: rInt)
		{
			System.out.println("  "+i);
		}
		System.out.println();
	}



	private static void createDatabase() throws DatabaseException
	{
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setAllowCreate(true);
		conf.setTransactional(true);

		Environment env = null;
		try
		{
			env = new Environment(new File("test.je"),conf);
			createTable(env);
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

	private static void createTable(final Environment env) throws DatabaseException
	{
		final TransactionConfig confTxn = READ_COMMITTED;

		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);

		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setAllowCreate(true);

        final Database db = env.openDatabase(txn,"testArray",confDB);

        insertRows(db,txn);

        txn.commit();

        db.close();
	}

	private static void insertRows(final Database db, final Transaction txn) throws DatabaseException
	{
		final IntListBinding bnd = new IntListBinding();

		{
	        final DatabaseEntry key = new DatabaseEntry();
	        IntegerBinding.intToEntry(99,key);
	
	        final DatabaseEntry data = new DatabaseEntry();
	        final List<Integer> rInt = new ArrayList<Integer>();
	        rInt.add(31);
	        rInt.add(32);
	        rInt.add(33);
	        bnd.objectToEntry(rInt,data);
	
	        final OperationStatus status = db.put(txn,key,data);
	        if (!status.equals(OperationStatus.SUCCESS))
	        {
	        	throw new IllegalStateException();
	        }
		}
		{
	        final DatabaseEntry key = new DatabaseEntry();
	        IntegerBinding.intToEntry(88,key);

	        final DatabaseEntry data = new DatabaseEntry();
	        final List<Integer> rInt = new ArrayList<Integer>();
	        rInt.add(21);
	        rInt.add(22);
	        rInt.add(23);
	        rInt.add(24);
	        bnd.objectToEntry(rInt,data);

	        final OperationStatus status = db.put(txn,key,data);
	        if (!status.equals(OperationStatus.SUCCESS))
	        {
	        	throw new IllegalStateException();
	        }
		}
	}
}
