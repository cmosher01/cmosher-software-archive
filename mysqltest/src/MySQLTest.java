import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import nu.mine.mosher.logging.LoggingInitializer;

public class MySQLTest
{
	private static final MySQLTest app;

	private Logger log = Logger.global;
	private Connection db;

	static
	{
		LoggingInitializer.init();
		app = new MySQLTest();
	}

    public static void main(String[] rArg) throws Throwable
    {
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
			log.info("Inserting data...");
			insertData();
			log.info("Done inserting data.");
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

	protected void insertData() throws SQLException
    {
    	dbUpdate("delete from family");
    	int fland = dbInsert("insert into family(name) values(\"Flandreau\")");
    	log.fine("Inserted Flandreau with id "+fland);
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

	protected int dbInsert(String sql) throws SQLException
	{
		int key = -1;
		Statement st = null;
		try
		{
			st = db.createStatement();
			st.execute(sql,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = st.getGeneratedKeys();
			while (rs.next())
			{
				key = rs.getInt(1);
			}
		}
		finally
		{
			closeStatement(st);
		}
		return key;
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
