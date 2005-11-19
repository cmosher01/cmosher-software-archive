/*
 * Created on Nov 16, 2005
 */
package nu.mine.mosher.grodb2;

import java.io.File;
import java.util.Date;
import java.util.UUID;
import nu.mine.mosher.grodb.date.DatePeriod;
import nu.mine.mosher.grodb.date.DateRange;
import nu.mine.mosher.grodb.date.YMD;
import nu.mine.mosher.grodb2.datatype.DatePeriodBinding;
import nu.mine.mosher.grodb2.datatype.RoleTypeBinding;
import nu.mine.mosher.grodb2.datatype.Source;
import nu.mine.mosher.grodb2.datatype.SourceBinding;
import nu.mine.mosher.grodb2.datatype.SourceID;
import nu.mine.mosher.grodb2.datatype.SourceIDBinding;
import nu.mine.mosher.grodb2.datatype.SourceRel;
import nu.mine.mosher.grodb2.datatype.SourceRelBinding;
import nu.mine.mosher.grodb2.datatype.SourceRelChild;
import nu.mine.mosher.grodb2.datatype.SourceRelParent;
import nu.mine.mosher.grodb2.datatype.TimeBinding;
import nu.mine.mosher.grodb2.datatype.UUIDBinding;
import nu.mine.mosher.grodb2.util.TransactionConfigFactory;
import nu.mine.mosher.time.Time;
import nu.mine.mosher.uuid.UUIDFactory;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.ForeignKeyDeleteAction;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

/**
 * TODO
 *
 * @author Chris Mosher
 */
public class DBSourceTesting
{
	private static final Transaction NO_PARENT = null;

	private static final String dbnSource = "Source";
	private static final String dbnSourceRel = "SourceRel";
	private static final String idxnSourceRelChild = "SourceRelChild";
	private static final String idxnSourceRelParent = "SourceRelParent";

	private final static UUIDFactory factoryUUID = new UUIDFactory();

	/**
	 * @param args
	 * @throws DatabaseException 
	 */
	public static void main(String[] args) throws DatabaseException
	{
//		createDatabase();
		testRead();
	}

	/*
	 * about to insert source with id aa04f096-57d2-11da-b980-8fdbc0a80165: USA
	 * about to insert source with id aa067737-57d2-11da-b980-8fdbc0a80165: US Census
	 * about to insert source with id aa067738-57d2-11da-b980-8fdbc0a80165: US Census, 1790
	 */

	private static void testRead() throws DatabaseException
	{
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setTransactional(true);
		conf.setReadOnly(true);

		Environment env = null;
		try
		{
			env = new Environment(new File("testGroDB2"),conf);
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
		final TransactionConfig confTxn = TransactionConfigFactory.createReadCommitted();

		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);

		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setReadOnly(true);

        confDB.setSortedDuplicates(true);

        final Database db = env.openDatabase(txn,idxnSourceRelChild/*dbnSource*/,confDB);

        readRows(db,txn);

        txn.commit();

        db.close();
	}

	private static void readRows(Database db, Transaction txn) throws DatabaseException
	{
        readRow(db,txn);
//        readRow(db,txn,99);
	}

	private static void readRow(Database db, Transaction txn) throws DatabaseException
	{
//		final long a = 0xaa06773757d211daL;
//		final long b = 0xb9808fdbc0a80165L;
//
//		final UUID uuidChild = new UUID(a,b);
//		final SourceID idChild = new SourceID(uuidChild);
//
//		final DatabaseEntry key = new DatabaseEntry();
//		final SourceIDBinding bindingSourceID = new SourceIDBinding(new UUIDBinding());
//		bindingSourceID.objectToEntry(idChild,key);
//
//        final DatabaseEntry data = new DatabaseEntry();
//        get(key,data,db,txn);
//
//		final SourceBinding bindingSource = new SourceBinding(new DatePeriodBinding(),new TimeBinding());
//		final Source srcReadBackIn = (Source)bindingSource.entryToObject(data);
//		System.out.println(srcReadBackIn.getTitle());
		final long a = 0xaa06773857d211daL;
		final long b = 0xb9808fdbc0a80165L;

		final UUID uuidChild = new UUID(a,b);
		final SourceID idChild = new SourceID(uuidChild);

		final DatabaseEntry key = new DatabaseEntry();
		final SourceIDBinding bindingSourceID = new SourceIDBinding(new UUIDBinding());
		bindingSourceID.objectToEntry(idChild,key);

        final DatabaseEntry data = new DatabaseEntry();
        get(key,data,db,txn);

		final SourceRelBinding bindingSourceRel = new SourceRelBinding(new SourceIDBinding(new UUIDBinding()));
		final SourceRel srcReadBackIn = (SourceRel)bindingSourceRel.entryToObject(data);
		System.out.println(srcReadBackIn.getParent());
	}

	private static void createDatabase() throws DatabaseException
	{
		final EnvironmentConfig conf = new EnvironmentConfig();
		conf.setAllowCreate(true);
		conf.setTransactional(true);

		Environment env = null;
		try
		{
			env = new Environment(new File("testGroDB2"),conf);
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
		final TransactionConfig confTxn = TransactionConfigFactory.createReadCommitted();
		final Transaction txn = env.beginTransaction(NO_PARENT,confTxn);



		final DatabaseConfig confDB = new DatabaseConfig();
        confDB.setTransactional(true); 
        confDB.setAllowCreate(true);
        final Database dbSource = env.openDatabase(txn,dbnSource,confDB);



        final Database dbSourceRel = env.openDatabase(txn,dbnSourceRel,confDB);

        final SecondaryConfig confDB2 = new SecondaryConfig();
        confDB2.setTransactional(true);
        confDB2.setAllowCreate(true);
        confDB2.setSortedDuplicates(true);
        confDB2.setAllowPopulate(true);
        confDB2.setImmutableSecondaryKey(true);
        confDB2.setForeignKeyDatabase(dbSource);
        confDB2.setForeignKeyDeleteAction(ForeignKeyDeleteAction.CASCADE);
        confDB2.setKeyCreator(new SourceRelChild(new SourceRelBinding(new SourceIDBinding(new UUIDBinding()))));
        final SecondaryDatabase idxSourceRelChild = env.openSecondaryDatabase(txn,idxnSourceRelChild,dbSourceRel,confDB2);
        confDB2.setKeyCreator(new SourceRelParent(new SourceRelBinding(new SourceIDBinding(new UUIDBinding()))));
        final SecondaryDatabase idxSourceRelParent = env.openSecondaryDatabase(txn,idxnSourceRelParent,dbSourceRel,confDB2);



        insertRows(dbSource,dbSourceRel,txn);

        txn.commit();



        idxSourceRelParent.close();
        idxSourceRelChild.close();
        dbSourceRel.close();
        dbSource.close();
	}

	private static void insertRows(final Database dbSource, final Database dbSourceRel, final Transaction txn) throws DatabaseException
	{
		final SourceIDBinding bindingSourceID = new SourceIDBinding(new UUIDBinding());
		final SourceBinding bindingSource = new SourceBinding(new DatePeriodBinding(), new TimeBinding());
		final SourceRelBinding bindingSourceRel = new SourceRelBinding(bindingSourceID);



		Source src;

		src = new Source("USA","",new Time(new Date(0L)),"USA","",new DatePeriod(new DateRange(YMD.getMinimum())));
		final SourceID idUSA = insertSource(src,dbSource,txn,bindingSourceID,bindingSource);

		src = new Source("US Census","US Census Bureau",new Time(new Date(0L)),"USA","",new DatePeriod(new DateRange(new YMD(1790)),new DateRange(new YMD(1930))));
		final SourceID idUSCensus = insertSource(src,dbSource,txn,bindingSourceID,bindingSource);

		src = new Source("US Census, 1790","US Census Bureau",new Time(new Date(0L)),"","",new DatePeriod(new DateRange(new YMD(1790))));
		final SourceID idUSCensus1790 = insertSource(src,dbSource,txn,bindingSourceID,bindingSource);



		SourceRel sr;

		sr = new SourceRel(idUSA,idUSCensus);
		insertSourceRel(sr,dbSourceRel,txn,bindingSourceRel);

		sr = new SourceRel(idUSCensus,idUSCensus1790);
		insertSourceRel(sr,dbSourceRel,txn,bindingSourceRel);
	}

	private static void insertSourceRel(final SourceRel sr, final Database dbSourceRel, final Transaction txn, final SourceRelBinding bindingSourceRel) throws DatabaseException
	{
		final DatabaseEntry key = new DatabaseEntry();
		bindingSourceRel.objectToEntry(sr,key);

		final DatabaseEntry dat = new DatabaseEntry(new byte[0]);

		put(key,dat,dbSourceRel,txn);
	}

	private static SourceID insertSource(final Source src, final Database dbSource, final Transaction txn, final SourceIDBinding bindingSourceID, final SourceBinding bindingSource) throws DatabaseException
	{
		final SourceID id = new SourceID(DBSourceTesting.factoryUUID.createUUID());
		System.out.println("about to insert source with id "+id.toString()+": "+src.getTitle());

		final DatabaseEntry key = new DatabaseEntry();
		bindingSourceID.objectToEntry(id,key);

		final DatabaseEntry dat = new DatabaseEntry();
		bindingSource.objectToEntry(src,dat);

		put(key,dat,dbSource,txn);

		return id;
	}

	private static void put(final DatabaseEntry key, final DatabaseEntry data, final Database db, final Transaction txn) throws DatabaseException
	{
		final OperationStatus status = db.put(txn,key,data);

		if (!status.equals(OperationStatus.SUCCESS))
		{
			System.err.println(status);
			throw new DatabaseException();
		}
	}

	private static void get(final DatabaseEntry key, final DatabaseEntry data, final Database db, final Transaction txn) throws DatabaseException
	{
		final OperationStatus status = db.get(txn,key,data,LockMode.READ_COMMITTED);

		if (!status.equals(OperationStatus.SUCCESS))
		{
			System.err.println(status);
			throw new DatabaseException();
		}
	}
}
