import java.sql.Connection;
import java.sql.DriverManager;
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
        }
        finally
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
}
