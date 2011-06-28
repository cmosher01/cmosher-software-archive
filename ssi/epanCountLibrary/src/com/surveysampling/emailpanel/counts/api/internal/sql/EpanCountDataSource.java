/*
 * Created on April 28, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import com.surveysampling.emailpanel.shared.DatabaseURLs;

import oracle.jdbc.OracleConnection;

/**
 * Creates database <code>Connection</code>s for use by
 * the <code>EpanCountLibrary</code> API.
 * 
 * @author Chris Mosher
 */
public class EpanCountDataSource implements DataSource
{
    /**
     * Creates a new <code>DataSource</code> for use by the <code>EpanCountLibrary</code> API.
     * @return the new <code>DataSource</code>
     */
    public static DataSource createDataSource()
    {
        return new EpanCountDataSource();
    }



    /**
     * @param username
     * @param password
     * @return
     * @throws SQLException
     */
    public Connection getConnection(final String username, final String password) throws SQLException
    {
        final UsernameParser parser = new UsernameParser(username);
        parser.parse();

        final Connection connection = connect(parser,password);
        initializeConnection(parser.getLevel(),connection);

        return connection;
    }



    private static Connection connect(final UsernameParser parser, final String password) throws SQLException
    {
        final String url = DatabaseURLs.getURL(parser.getLevel(),DatabaseURLs.DEDICATED);
        final String username = parser.getUsername();

        return DriverManager.getConnection(url,username,password);
    }



    private void initializeConnection(final int level, final Connection connection) throws SQLException
    {
        connection.setAutoCommit(false);
        connection.commit();

        if (connection instanceof OracleConnection)
        {
            final OracleConnection oracle = (OracleConnection)connection;

            oracle.setImplicitCachingEnabled(true);
            oracle.setStatementCacheSize(20);
        }

        final Statement st = connection.createStatement();

        String schema = "EPANCOUNT";
        if (level == DatabaseURLs.BETA)
        {
            schema += "QC";
        }
        st.executeUpdate("ALTER SESSION SET CURRENT_SCHEMA = "+schema);

        st.executeUpdate("SET ROLE CONNECT, SSI_Email_Count IDENTIFIED BY oneby1");

        final ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM CurrentUserID");
        int ids = 0;
        while (rs.next())
        {
            ids = rs.getInt(1);
        }
        if (ids != 1)
        {
            throw new SQLException("User not found in SSIEmployee table.");
        }
    }






    /**
     * @return never returns
     * @throws UnsupportedOperationException always throws
     */
    public Connection getConnection() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @param out ignored
     * @throws UnsupportedOperationException always throws
     */
    public void setLogWriter(PrintWriter out) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return always returns null
     */
    public PrintWriter getLogWriter()
    {
        return null;
    }

    /**
     * @param seconds ignored
     * @throws UnsupportedOperationException always throws
     */
    public void setLoginTimeout(int seconds) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @return always returns zero
     */
    public int getLoginTimeout()
    {
        return 0;
    }
}
