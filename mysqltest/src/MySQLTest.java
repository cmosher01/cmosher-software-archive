import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	private int family;

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
			family = dbInsert("insert into Family(name) values(\"Flandreau\")");
			log.fine("Done creating family with id "+family);

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

	protected void processLine(String s) throws SQLException
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

		int year = readInt(i.next());
		switch (year)
        {
            case 1790:
                parse1790(i);
            break;
			case 1800:
				parse1800(i);
			break;

            default:
            break;
        }
    }

	protected void parse1790(Iterator i) throws SQLException
    {
		String state = (String)i.next();
		String county = (String)i.next();
		String township = (String)i.next();
		int district = readInt(i.next());
		int image = readInt(i.next());
		String nameLast = (String)i.next();
		String nameFirst = (String)i.next();
		String nameMiddle = (String)i.next();
		String nameSuffix = (String)i.next();
		int m16to150 = readInt(i.next());
		int m0to15 = readInt(i.next());
		int f0to150 = readInt(i.next());

		int idImage = insertImage(1790, state, county, township, district, image);
        int hh = insertHousehold(nameLast, nameFirst, nameMiddle, nameSuffix, idImage);

		PreparedStatement st = null;
		try
		{
			st = db.prepareStatement(
			"insert into CountEntry (household,gender,minAge,maxAge,cnt) values (?,?,?,?,?)");

			st.setInt(1,hh);
			st.setString(2,"m");
			st.setInt(3,16);
			st.setInt(4,150);
			st.setInt(5,m16to150);
			st.execute();

			st.setInt(3,0);
			st.setInt(4,15);
			st.setInt(5,m0to15);
			st.execute();

			st.setString(2,"f");
			st.setInt(4,150);
			st.setInt(5,f0to150);
			st.execute();
		}
		finally
		{
			closeStatement(st);
			st = null;
		}
    }

    protected int insertHousehold(String nameLast, String nameFirst, String nameMiddle, String nameSuffix, int idImage) throws SQLException
    {
        PreparedStatement st = null;
        int hh = 0;
        try
        {
        	st = db.prepareStatement(
        	"insert into Household (image,family,nameLast,nameFirst,nameMiddle,nameSuffix) values (?,?,?,?,?,?)");
        	st.setInt(1,idImage);
        	st.setInt(2,family);
        	st.setString(3,nameLast);
        	st.setString(4,nameFirst);
        	st.setString(5,nameMiddle);
        	st.setString(6,nameSuffix);
        	st.execute();
        	ResultSet rs = st.getGeneratedKeys();
        	while (rs.next())
        	{
        		hh = rs.getInt(1);
        	}
        }
        finally
        {
        	closeStatement(st);
        	st = null;
        }
        return hh;
    }

	protected void parse1800(Iterator i) throws SQLException
	{
		String state = (String)i.next();
		String county = (String)i.next();
		String township = (String)i.next();
		int district = readInt(i.next());
		int image = readInt(i.next());
		String nameLast = (String)i.next();
		String nameFirst = (String)i.next();
		String nameMiddle = (String)i.next();
		String nameSuffix = (String)i.next();
		int m0to9 = readInt(i.next());
		int m10to15 = readInt(i.next());
		int m16to25 = readInt(i.next());
		int m26to44 = readInt(i.next());
		int m45to150 = readInt(i.next());
		int f0to9 = readInt(i.next());
		int f10to15 = readInt(i.next());
		int f16to25 = readInt(i.next());
		int f26to44 = readInt(i.next());
		int f45to150 = readInt(i.next());

        int idImage = insertImage(1800, state, county, township, district, image);
		int hh = insertHousehold(nameLast, nameFirst, nameMiddle, nameSuffix, idImage);


		PreparedStatement st = null;
		try
		{
			st = db.prepareStatement(
			"insert into CountEntry (household,gender,minAge,maxAge,cnt) values (?,?,?,?,?)");

			st.setInt(1,hh);
			st.setString(2,"m");
			st.setInt(3,16);
			st.setInt(4,150);
			st.setInt(5,m16to150);
			st.execute();

			st.setInt(3,0);
			st.setInt(4,15);
			st.setInt(5,m0to15);
			st.execute();

			st.setString(2,"f");
			st.setInt(4,150);
			st.setInt(5,f0to150);
			st.execute();
		}
		finally
		{
			closeStatement(st);
			st = null;
		}
	}

    protected int insertImage(int year, String state, String county, String township, int district, int image) throws SQLException
    {
        int idImage = 0;
        PreparedStatement st = null;
        try
        {
        	st = db.prepareStatement(
        	"select id from ImageIdent where "+
        	"year = ? and state = ? and county = ? and township = ? and district = ? and image = ?");
        	st.setInt(1,year);
        	st.setString(2,state);
        	st.setString(3,county);
        	st.setString(4,township);
        	st.setInt(5,district);
        	st.setInt(6,image);
        	ResultSet rs = st.executeQuery();
        	if (rs.next())
        	{
        		idImage = rs.getInt("id");
        	}
        }
        finally
        {
        	closeStatement(st);
        	st = null;
        }
        if (idImage == 0)
        {
        	try
        	{
        		st = db.prepareStatement(
        		"insert into ImageIdent (year,state,county,township,district,image) values (?,?,?,?,?,?)");
        		st.setInt(1,year);
        		st.setString(2,state);
        		st.setString(3,county);
        		st.setString(4,township);
        		st.setInt(5,district);
        		st.setInt(6,image);
        		st.execute();
        		ResultSet rs = st.getGeneratedKeys();
        		while (rs.next())
        		{
        			idImage = rs.getInt(1);
        		}
        	}
        	finally
        	{
        		closeStatement(st);
        		st = null;
        	}
        }
        return idImage;
    }

    protected int readInt(Object stringField)
    {
		String s = (String)stringField;
    	int i;
		if (s.length() > 0)
		{
			i = Integer.parseInt(s);
		}
		else
		{
			i = 0;
		}
    	return i;
    }

    protected void calc() throws SQLException
    {
    	Statement st = null;
		PreparedStatement ins = null;
    	try
    	{
			st = db.createStatement();
			ins = db.prepareStatement("insert into CalcPerson(entry,birthEarliest,birthLatest) values (?,?,?)");

    		ResultSet rs = st.executeQuery("select "+
    		"cnt, ce.id entry,"+
			"date_add(date_sub(censusday,interval maxage+1 year), interval 1 day) earliest, "+
    		"date_sub(censusday,interval minage year) latest "+
			"from imageident im "+
			"join household hh on (hh.image=im.id) "+
			"join countentry ce on (ce.household=hh.id) "+
			"join census cen on (cen.year=im.year)");

    		while (rs.next())
    		{
    			int c = rs.getInt("cnt");
				int entry = rs.getInt("entry");
				String earliest = rs.getString("earliest");
				String latest = rs.getString("latest");
				ins.setInt(1,entry);
				ins.setString(2,earliest);
				ins.setString(3,latest);
    			for (int i = 0; i < c; ++i)
    			{
					ins.executeUpdate();
    			}
    		}
    	}
    	finally
    	{
			closeStatement(ins);
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
    }

    protected void createSchema() throws SQLException
	{
		dbUpdate("drop table if exists Family");
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
		dbUpdate("drop table if exists Census");
		dbUpdate("create table "+
		"Census "+
		"( "+
		"    year integer unsigned not null primary key, "+
		"    month integer unsigned, "+
		"    day integer unsigned, "+
		"    censusDay date "+
		")");
		Statement st = null;
		try
		{
			st = db.createStatement();
			st.execute("insert into Census(year,month,day,censusDay) values "+
			"(1790,8,2,17900802), "+
			"(1800,8,4,18000804), "+
			"(1810,8,6,18100806), "+
			"(1820,8,7,18200807), "+
			"(1830,6,1,18300601), "+
			"(1840,6,1,18400601), "+
			"(1850,6,1,18500601), "+
			"(1860,6,1,18600601), "+
			"(1870,6,1,18700601), "+
			"(1880,6,1,18800601), "+
			"(1890,6,1,18900601), "+
			"(1900,6,1,19000601), "+
			"(1910,4,15,19100415), "+
			"(1920,1,1,19200101), "+
			"(1930,4,1,19300401) ");
		}
		finally
		{
			closeStatement(st);
		}


		dbUpdate("drop table if exists ImageIdent");
		dbUpdate("create table "+
		"ImageIdent "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    year integer unsigned not null references Census(year), "+
		"    state char(2), "+
		"    county varchar(64), "+
		"    township varchar(64), "+
		"    district integer unsigned default 0, "+
		"    image integer unsigned "+
		")");
		dbUpdate("drop table if exists Household");
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
		dbUpdate("drop table if exists CountEntry");
		dbUpdate("create table "+
		"CountEntry "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    household integer unsigned not null references Household(id), "+
		"    gender enum (\"m\",\"f\"), "+
		"    minAge integer unsigned, "+
		"    maxAge integer unsigned, "+
		"    cnt  integer unsigned "+
		")");
		dbUpdate("drop table if exists CalcPerson");
		dbUpdate("create table "+
		"CalcPerson "+
		"( "+
		"    id integer unsigned not null auto_increment primary key, "+
		"    entry integer unsigned not null references CountEntry(id), "+
		"    birthEarliest date, "+
		"    birthLatest date "+
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
