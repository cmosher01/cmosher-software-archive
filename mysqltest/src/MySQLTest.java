import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class MySQLTest
{
    public static void main(String[] rArg) throws Throwable
    {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection db = DriverManager.getConnection("jdbc:mysql:///test","root","");
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
        db.close();
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
