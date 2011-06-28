/*
 * Created on Dec 2, 2005
 */
package nu.mine.mosher.gedcom;

import nu.mine.mosher.grodb2.datatype.DatePeriodBinding;
import nu.mine.mosher.grodb2.datatype.Source;
import nu.mine.mosher.grodb2.datatype.SourceBinding;
import nu.mine.mosher.grodb2.datatype.SourceID;
import nu.mine.mosher.grodb2.datatype.SourceIDBinding;
import nu.mine.mosher.grodb2.datatype.TimeBinding;
import nu.mine.mosher.grodb2.datatype.UUIDBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.TransactionConfig;

public class TestDump
{
	/**
	 * @param args
	 * @throws DatabaseException 
	 */
	public static void main(String[] args) throws DatabaseException
	{
		System.err.println("Opening databases...");
		final Environment environment = TestAccess.openEnvironment(false);
		final Database dbSource = TestAccess.openDatabaseSource(environment,false);

		final TransactionConfig configTxn = new TransactionConfig();
		configTxn.setReadCommitted(true);
		final Transaction txn = environment.beginTransaction(TestAccess.NO_PARENT,configTxn);

		final CursorConfig configCursor = new CursorConfig();
		configCursor.setReadCommitted(true);

		final Cursor cursor = dbSource.openCursor(txn,configCursor);

		final DatabaseEntry keyFound = new DatabaseEntry();
		final DatabaseEntry dataFound = new DatabaseEntry();

		final SourceIDBinding bindingSourceID = new SourceIDBinding(new UUIDBinding());
		final SourceBinding bindingSource = new SourceBinding(new DatePeriodBinding(), new TimeBinding());

		int cSource = 0;
		while (cursor.getNext(keyFound,dataFound,LockMode.DEFAULT) == OperationStatus.SUCCESS)
	    {
			++cSource;
			final SourceID idSource = (SourceID)bindingSourceID.entryToObject(keyFound);
			final Source source = (Source)bindingSource.entryToObject(dataFound);

			System.out.print("SOURCE ");
			System.out.print(idSource);
			System.out.print(": ");
			System.out.print(source);
			System.out.println();
	    }
		System.out.println("Read "+cSource+" sources.");

		cursor.close();
		txn.commit();
		dbSource.close();
		TestAccess.closeEnvironment(environment);
	}
}
