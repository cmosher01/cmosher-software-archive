import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLTest
{
	private static final MySQLTest app = new MySQLTest();

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
		Class.forName("com.mysql.jdbc.Driver").newInstance();

		Connection db = null;
		try
		{
			db = DriverManager.getConnection("jdbc:mysql:///test","root","");
			createSchema(db);
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

    protected void createSchema(Connection db) throws SQLException
    {
        Statement st = null;
        try
        {
        	st = db.createStatement();
        	st.execute(
        	"create table if not exists family "+
        	"( "+
        	"    id integer unsigned not null auto_increment primary key, "+
        	"    name varchar(64)"+
        	")");
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
