package com.surveysampling.mosher.sql;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SelectLoop
{
    public static void select(Connection con, String sql, Object arg, SelectLoop action)
        throws SQLException
    {
        Statement st = null;
        ResultSet rs = null;
        try
        {
            st = con.createStatement();
            rs = st.executeQuery(sql);
    	    while (rs.next())
    	        action.execute(rs,arg);
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Throwable e)
                {
                }
                rs = null;
            }
            if (st != null)
            {
                try
                {
                    st.close();
                }
                catch (Throwable e)
                {
                }
                st = null;
            }
        }
    }

    abstract protected void execute(ResultSet rs, Object arg) throws SQLException;
}
