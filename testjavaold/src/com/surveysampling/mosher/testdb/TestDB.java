package com.surveysampling.mosher.testdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.driver.OracleDriver;

class TestDB
{
    public TestDB() {}
    public static void main(String[] rArg)
    {
        System.out.println("begin main---------------------------------------------");
        try
        {
            System.out.println("begin registerDriver---------------------------------------------");
            DriverManager.registerDriver (new OracleDriver());
            System.out.println("end registerDriver---------------------------------------------");
            String url = 
                "jdbc:oracle:thin:@(description=(address=(host=sagesse)(protocol=tcp)(port=1521))(connect_data=(SERVICE_NAME=t817.surveysampling.com)(SRVR = DEDICATED)))";
                //"jdbc:default:connection:";
            System.out.println("begin getconnection---------------------------------------------");
            Connection con = DriverManager.getConnection(url,"chrism","majic4u");
            System.out.println("end getconnection---------------------------------------------");

            StringBuffer sb = new StringBuffer(5000);
//            sb.append("declare ");
//            sb.append("  i rowid; ");
//            sb.append("begin ");
//            sb.append("  insert into test2 values(500) returning rowid into i;");
//            sb.append("  open ? for select * from test2 where rowid = i;");
//            sb.append("end;");
//            OracleCallableStatement stmt = (OracleCallableStatement)con.prepareCall(sb.toString());
//            stmt.registerOutParameter(1,OracleTypes.CURSOR);
//            stmt.execute();
//            ResultSet rs = stmt.getCursor(1);
//            while (rs.next())
//            {
//                String s = rs.getString(1);
//                System.out.println(s);
//            }

			sb.append("select\n");
			sb.append("*\n");
			sb.append("from\n");
			sb.append("samplereal\n");
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery(sb.toString());
			while (rs.next())
			{
				System.out.println(rs.getString("prenthseq"));
			}
			rs.close();
			st.close();
			con.close();
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
            System.out.println("end main---------------------------------------------");
        }
    }
}
