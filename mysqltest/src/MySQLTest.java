import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Logger;

import nu.mine.mosher.core.StringFields;
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

			log.info("Creating family...");
			dbUpdate("delete from Family");
			fland = dbInsert("insert into Family(name) values(\"Flandreau\")");
			log.fine("Done creating family with id "+fland);

			log.info("Reading data...");
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File("F:\\Genealogy\\by family\\Flandreau\\census\\flandreau_census.csv"))));
			for (String s = in.readLine(); s != null; s = in.readLine())
			{
				processLine(s);
			}
			log.info("Done reading data.");

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

	protected void processLine(String s)
    {
		int comment = s.indexOf('#');
		if (comment >= 0)
		{
			s = s.substring(0,comment);
		}

		// Trim leading and trailing spaces
		s = s.trim();

		// If the line is blank, then just ignore it
		if (s.length() == 0)
		{
			return;
		}

		Iterator i = new StringFields(s).iterator();

		int year = Integer.parseInt((String)i.next());
		switch (year)
        {
            case 1790:
                parse1790(i);
            break;

            default:
            break;
        }
    }

	protected void parse1790(Iterator i)
    {
		String state = (String)i.next();
		String county = (String)i.next();
		String township = (String)i.next();
		String other = (String)i.next();
		int image = Integer.parseInt((String)i.next());
		String nameLast = (String)i.next();
		String nameFirst = (String)i.next();
		String nameMiddle = (String)i.next();
		String nameSuffix = (String)i.next();
		int m16to150 = Integer.parseInt((String)i.next());
		int m0to15 = Integer.parseInt((String)i.next());
		int f0to150 = Integer.parseInt((String)i.next());
    }

    protected void calc() throws SQLException
    {
    	calcOneCount(1790,"male",16,150);
		calcOneCount(1790,"male",0,15);
		calcOneCount(1790,"female",0,150);
    }

	protected void calcOneCount(int year, String sGender, int ageMin, int ageMax) throws SQLException
    {
    	Statement st = null;
    	try
    	{
    		st = db.createStatement();
    		ResultSet rs = st.executeQuery("select "+columnName(sGender,ageMin,ageMax)+" c, id from Census"+year);
    		while (rs.next())
    		{
    			int c = rs.getInt("c");
    			String id = rs.getString("id");
//    				rs.getString("nameFirst")+" "+
//    				rs.getString("nameMiddle")+" "+
//    				rs.getString("nameSuffix");
    			for (int i = 0; i < c; ++i)
    			{
    				log.info(
    					"Census"+year+","+id+","+
    					sGender+","+
    					(year-ageMax)+","+
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
		s += minAge;
		s += "to";
		s += maxAge;
		return s;
	}

    protected void insertData() throws SQLException
    {

//		dbInsert("insert into Census1790(nameLast,nameFirst,nameSuffix,maleAge16plus,maleAge0to15,femaleAge0plus,family) values "+
//		"(\"Flandreau\",\"Benjamin\",null,4,2,5,"+fland+"), "+
//		"(\"Flandreau\",\"Elias\",null,3,0,4,"+fland+"), "+
//		"(\"Flandreau\",\"James\",null,2,2,5,"+fland+"), "+
//		"(\"Flandreau\",\"James\",\"Junr.\",1,3,2,"+fland+"), "+
//		"(\"Flandreau\",\"John\",null,1,3,3,"+fland+")");
		int image = dbInsert("insert into ImageIdent(year,state,county,township,image) values "+
		"(1790,\"NY\")");
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

//		dbUpdate("drop table Census1790");
//		dbUpdate("create table "+
//		"Census1790 "+
//		"( "+
//		"    id integer unsigned not null auto_increment primary key, "+
//		"    family integer unsigned not null references Family(id), "+
//		"    nameLast varchar(64), "+
//		"    nameFirst varchar(64), "+
//		"    nameMiddle varchar(8), "+
//		"    nameSuffix varchar(8), "+
//		"    male16plus integer, "+
//		"    male0to15 integer, "+
//		"    female0plus integer, "+
//		"    other integer, "+
//		"    slave integer "+
//		")");
		dbUpdate("drop table ImageIdent");
		dbUpdate("create table "+
		"ImageIdent "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    year integer unsigned, "+
		"    state char(2), "+
		"    county varchar(64), "+
		"    township varchar(64), "+
		"    other varchar(8), "+
		"    image integer unsigned "+
		")");
		dbUpdate("drop table Household");
		dbUpdate("create table "+
		"Household "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    image integer unsigned not null references ImageIdent(id), "+
		"    family integer unsigned not null references Family(id), "+
		"    nameLast varchar(64), "+
		"    nameFirst varchar(64), "+
		"    nameMiddle varchar(8), "+
		"    nameSuffix varchar(8) "+
		")");
		dbUpdate("drop table CountEntry");
		dbUpdate("create table "+
		"CountEntry "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    household integer unsigned not null references Household(id), "+
		"    gender enum (\"M\",\"F\"), "+
		"    minAge integer unsigned, "+
		"    maxAge integer unsigned, "+
		"    count  integer unsigned "+
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
