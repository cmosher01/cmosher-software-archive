import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import nu.mine.mosher.logging.LoggingInitializer;

public class MySQLTest
{
	private static final MySQLTest app = new MySQLTest();

	private Logger log = Logger.global;
	private Connection db;

    public static void main(String[] rArg) throws Throwable
    {
		LoggingInitializer.init();
    	app.run(rArg);
    }

	private MySQLTest()
	{
		if (app != null)
		{
			throw new UnsupportedOperationException();
		}
	}

    protected void run(String[] rArg) throws Throwable
    {
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			db = DriverManager.getConnection("jdbc:mysql:///test","root","");
			log.info("Creating schema...");
			createSchema();
			log.info("Done creating schema.");
		}
		finally
		{
			if (db != null)
			{
				try
				{
					db.close();
				}
				catch (Throwable e)
				{
					e.printStackTrace();
				}
			}
		}
    }

	protected void createSchema() throws SQLException
	{
		dbUpdate("create table if not exists "+
		"Family "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    name varchar(64)"+
		")");
		dbUpdate("create table if not exists "+
		"Census1790 "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    nameLast varchar(64), "+
		"    nameFirst varchar(64), "+
		"    nameMiddle varchar(8), "+
		"    nameSuffix varchar(8), "+
		"    maleAge16plus integer, "+
		"    maleAge0to15 integer, "+
		"    female integer, "+
		"    other integer, "+
		"    slave integer "+
		")");
	}

    protected void dbUpdate(String sql) throws SQLException
    {
        Statement st = null;
        try
        {
        	st = db.createStatement();
        	st.executeUpdate(sql);
        }
        finally
        {
            closeStatement(st);
        }
    }

    private static void closeStatement(Statement st)
    {
        if (st != null)
        {
        	try
        	{
        		st.close();
        	}
        	catch (Throwable e)
        	{
        		e.printStackTrace();
        	}
        }
    }
}
