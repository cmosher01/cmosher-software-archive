import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLTest
{
    public static void main(String[] rArg) throws Throwable
    {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        Connection db = DriverManager.getConnection("jdbc:mysql:///test","root","");
    }
}
