package com.surveysampling.mosher.testorajvm;

//import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
//import java.io.IOException;
//import oracle.jdbc.driver.OracleDriver;
//import com.surveysampling.util.ConfigProperties;
//import com.surveysampling.sql.DatabaseConnection;

class TestOraJVM
{
    public TestOraJVM() {}
    public static void main(String[] rArg)
    {
        Connection con = null;
        try
        {
            System.out.println("begin main---------------------------------------------");


            System.out.println("begin open file---------------------------------------------");
            File f = new File("/usr/www/webuser/dbconfig.ini");
            FileReader fr = new FileReader(f);
            int x = fr.read();
            fr.close();
            System.out.println(x);
            System.out.println("end open file---------------------------------------------");

//            System.out.println("begin registerDriver---------------------------------------------");
//            DriverManager.registerDriver (new OracleDriver());
//            System.out.println("end registerDriver---------------------------------------------");

//            String url = 
//                  "jdbc:oracle:thin:@(description=(address=(host=sagesse)(protocol=tcp)(port=1521))(connect_data=(SERVICE_NAME=t817.surveysampling.com)(SRVR = DEDICATED)))";
//                  "jdbc:default:connection:";

//            System.out.println("begin getconnection---------------------------------------------");
//            con = DriverManager.getConnection(url);//,"test_pd","test_pd");
//            System.out.println("end getconnection---------------------------------------------");

            //con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from da_prod.webapppage");
            while (rs.next())
            {
                String s = rs.getString("webapppageid");
                System.out.println(s);
            }

        }
        catch (SQLException e)
        {
            System.out.println("begin handling exception---------------------------------");
            System.out.println("SQLException: ");
            System.out.println(e.getMessage());
            System.out.println(e.getSQLState());
            SQLException en = e;
            while (en.getNextException() != null)
            {
                System.out.println("next SQLException: ");
                System.out.println(en.getMessage());
            }
            System.out.println("end handling exception-----------------------------------");
        }
        catch (Throwable e)
        {
            System.out.println("Throwable: ");
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
                if (con != null)
                    con.close();
            }
            catch (Throwable e)
            {
            }

            System.out.println("end main---------------------------------------------");
        }
    }

//    private static Connection getConnection() throws SQLException, IOException
//    {
//        // First try to get a local database: this will work if we are
//        // running inside the Oracle JVM (i.e., this class is being
//        // run when it was installed into Oracle with the loadjava command).
//        // If not, getConnection will throw and exception
//        Connection con = null;
//        try
//        {
//            con = DriverManager.getConnection("jdbc:default:connection:");
//        }
//        catch (Throwable e)
//        {
//            con = null;
//        }
//
//        if (con == null)
//        {
//	        // Get the username and password for a Project Director
//	        ConfigProperties configProperties = new ConfigProperties(com.surveysampling.sql.Constants.CONFIG_FILENAME);
//            String username = configProperties.getProperty("ExtensionPullUserName");
//            String password = configProperties.getProperty("ExtensionPullPassword");
//
//            // Get the connection
//	        DatabaseConnection emailPanelConnection = new DatabaseConnection();
//	        con = emailPanelConnection.GetConnection(username,password);
//	    }
//
//        return con;
//    }
}
