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
        	ResultSet rs = st.executeQuery("select version() v;");
        	while (rs.next())
        	{
        		System.out.println(rs.getString("v"));
        	}
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
