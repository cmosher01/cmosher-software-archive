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
	private int fland;

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

			log.info("Calculating...");
			calc();
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

	protected void calc() throws SQLException
    {
    	calcOneCount(1790,"male",16,-1);
		calcOneCount(1790,"male",0,15);
		calcOneCount(1790,"female",0,-1);
    }

	protected void calcOneCount(int year, String sGender, int ageMin, int ageMax) throws SQLException
    {
    	Statement st = null;
    	try
    	{
    		st = db.createStatement();
    		ResultSet rs = st.executeQuery("select "+columnName(sGender,ageMin,ageMax)+" c from Census"+year);
    		while (rs.next())
    		{
    			int c = rs.getInt("c");
    			for (int i = 0; i < c; ++i)
    			{
    				log.info(
    					sGender+","+
    					(ageMax<0 ? ""+(1790-150) : ""+(1790-ageMax))+","+
    					(1790-ageMin));
    			}
    		}
    	}
    	finally
    	{
    		closeStatement(st);
    	}
    }

	protected String columnName(String sGender, int minAge, int maxAge)
	{
		String s = "";
		s += sGender;
		s += "Age";
		s += minAge;
		if (maxAge < 0)
		{
			s += "plus";
		}
		else
		{
			s += "to"+maxAge;
		}
		return s;
	}

    protected void insertData() throws SQLException
    {
    	dbUpdate("delete from Family");
    	fland = dbInsert("insert into Family(name) values(\"Flandreau\")");
    	log.fine("Inserted Flandreau with id "+fland);

		dbInsert("insert into Census1790(nameLast,nameFirst,maleAge16plus,maleAge0to15,femaleAge0plus,family) "+
		"values(\"Flandreau\",\"Benjamin\",4,2,5,"+fland+")");
		dbInsert("insert into Census1790(nameLast,nameFirst,maleAge16plus,maleAge0to15,femaleAge0plus,family) "+
		"values(\"Flandreau\",\"Elias\",3,0,4,"+fland+")");
		dbInsert("insert into Census1790(nameLast,nameFirst,maleAge16plus,maleAge0to15,femaleAge0plus,family) "+
		"values(\"Flandreau\",\"James\",2,2,5,"+fland+")");
		dbInsert("insert into Census1790(nameLast,nameFirst,nameSuffix,maleAge16plus,maleAge0to15,femaleAge0plus,family) "+
		"values(\"Flandreau\",\"James\",\"Junr.\",1,3,2,"+fland+")");
		dbInsert("insert into Census1790(nameLast,nameFirst,maleAge16plus,maleAge0to15,femaleAge0plus,family) "+
		"values(\"Flandreau\",\"John\",1,3,3,"+fland+")");
    }

    protected void createSchema() throws SQLException
	{
		dbUpdate("drop table Family");
		dbUpdate("create table "+
		"Family "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    name varchar(64)"+
		")");

		dbUpdate("drop table Census1790");
		dbUpdate("create table "+
		"Census1790 "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    family integer unsigned not null references Family(id), "+
		"    nameLast varchar(64), "+
		"    nameFirst varchar(64), "+
		"    nameMiddle varchar(8), "+
		"    nameSuffix varchar(8), "+
		"    maleAge16plus integer, "+
		"    maleAge0to15 integer, "+
		"    femaleAge0plus integer, "+
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
