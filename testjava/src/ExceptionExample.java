import java.io.FileNotFoundException;
import java.io.FileOutputStream;
//import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 */
public class ExceptionExample extends Exception
{

    /**
     * Constructor for ExceptionExample.
     */
    public ExceptionExample(String s, Throwable cause)
    {
        super(s,cause);
    }
    public void test() throws ExceptionExample, FileNotFoundException
    {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream("test.txt")));
        Connection conn = null;
        try
        {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select uid, name from user");
            while (rs.next())
            {
                out.println("User ID : " + rs.getString("uid") + ", name : " + rs.getString("name"));
            }
        }
        catch (SQLException sqlex)
        {
            out.println("Warning : data incomplete due to exception");
            throw new ExceptionExample("SQL error while reading", sqlex);
        }
//        catch (IOException ioex)
//        {
//            throw new ExceptionExample("I/O error while writing", ioex);
//        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.close();
                }
                catch (SQLException sqlex2)
                {
                    System.err.println(this.getClass().getName() + ".mymethod - cannot close SQL connection : " + sqlex2.toString());
                }
                if (out != null)
                {
//                    try
//                    {
                        out.close();
//                    }
//                    catch (IOException ioex2)
//                    {
//                        System.err.println(this.getClass().getName() + ".mymethod - cannot close outfile" + ioex2.toString());
//                    }
                }
            }
        }
    }
}
