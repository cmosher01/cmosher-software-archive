/*
 * Created on Mar 21, 2005
 */
package com.surveysampling.emailpanel.counts.update.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.surveysampling.sql.DatabaseUtil;

/**
 * Contains generic Oracle SQL static helper functions. 
 * 
 * @author Chris Mosher
 */
public final class OracleSQLUtil
{
    /**
     * Code for Oracle error ORA-00942: table or view does not exist
     */
    public static final int TABLE_NOT_FOUND_ERROR = 942;

    /**
     * Maximum length for index name, as defined by Oracle.
     */
    public static final int MAX_INDEX_NAME_LENGTH = 30;

    private OracleSQLUtil()
    {
        assert false : "Cannot instantiate.";
    }

    /**
     * @param statement
     */
    private static void closeStatementNoThrow(Statement statement)
    {
        DatabaseUtil.closeStatementNoThrow(statement);
    }

    /**
     * Executes the given SQL statement, which is a simple
     * DDL statement, using the given database <code>Connection</code>.
     * @param DDL the DDL statement to execute
     * @param db database <code>Connection</code>
     * @throws SQLException
     */
    public static void executeDDL(final String DDL, final Connection db) throws SQLException
    {
        Statement statement = null;
        try
        {
            statement = db.createStatement();
            statement.executeUpdate(DDL);
        }
        finally
        {
            closeStatementNoThrow(statement);
        }
    }

    /**
     * Drops the given table (using the given database
     * <code>Connection</code>). In case the table doesn't
     * exist, do nothing (don't generate an exception).
     * @param table the name of the table to drop (case insensitive)
     * @param db the database <code>Connection</code> to use
     * @throws SQLException any error other than "table not found"
     */
    public static void dropTable(final String table, final Connection db) throws SQLException
    {
        final StringBuffer sqlDropTable = new StringBuffer(41);
        sqlDropTable.append("DROP TABLE ");
        sqlDropTable.append(table);

        try
        {
            executeDDL(sqlDropTable.toString(),db);
        }
        catch (SQLException e)
        {
            /*
             * In case the table didn't exist when we tried
             * to delete it, we can safely ignore the resulting
             * Oracle error. But still report any other error.
             */
            if (e.getErrorCode() != TABLE_NOT_FOUND_ERROR)
            {
                throw e;
            }
        }
    }

    /**
     * Gets the names of all columns in a given table in the database.
     * The table must be in the current schema.
     * This method uses the given database <code>Connection</code> to
     * query the given table name. The <code>Connection</code> must
     * be set up properly, with all necessary privileges, before calling
     * this method.
     * @param table name of the table to get the columns of (case insensitive)
     * @param db database <code>Connection</code> to use
     * @return <code>HashSet</code> containing <code>String</code> names
     * of all columns in the given table
     * @throws SQLException
     */
    public static HashSet getColumnNames(final String table, final Connection db) throws SQLException
    {
        final Set set = new HashSet();

        PreparedStatement statement = null;
        try
        {
            statement = db.prepareStatement(getColumnNamesSQL(table));

            final ResultSet result = statement.executeQuery();
            while (result.next())
            {
                /*
                 * We should be getting zero rows back, so this
                 * loop should not even get executed once.
                 */
            }

            ResultSetMetaData metaData = result.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); ++i)
            {
                set.add(metaData.getColumnName(i).toUpperCase());
            }
        }
        finally
        {
            closeStatementNoThrow(statement);
        }

        return (HashSet)set;
    }

    /**
     * @param table
     * @return SQL to select no rows from the given table
     */
    private static String getColumnNamesSQL(final String table)
    {
        return "SELECT * FROM "+table+" WHERE NULL IS NOT NULL";
    }



    /**
     * Gathers Oracle statistics, including histograms, for the
     * given table, cascading to its indexes. The table must
     * be in the current schema. The <code>Connection</code> must
     * be set up properly, with all necessary privileges, before calling
     * this method.
     * @param table name of the table to gather stats on (case insensitive)
     * @param db database <code>Connection</code> to use
     * @throws SQLException for any database errors that occur
     * @throws IOException
     */
    public static void createHistogramsStatsCascade(final String table, final Connection db) throws SQLException, IOException
    {
        PreparedStatement statement = null;
        try
        {
            statement = db.prepareStatement(getHistogramSql());
            statement.setString(1,table.toUpperCase());

            statement.executeUpdate();
        }
        finally
        {
            closeStatementNoThrow(statement);
        }
    }

    /**
     * Builds the SQL for the histogram gathering process.
     * The SQL must be used as a PreparedStatement that takes
     * one parameter: the table name.
     * @return
     * @throws IOException
     */
    private static String getHistogramSql() throws IOException
    {
        return getSqlScriptResourceAsString("createHistogramsStatsCascade.sql");
    }

    /**
     * @param fileNameSql
     * @return
     * @throws IOException
     */
    private static String getSqlScriptResourceAsString(final String fileNameSql) throws IOException
    {
        final InputStream instreamSql = OracleSQLUtil.class.getResourceAsStream(fileNameSql);
        final BufferedReader inSql = new BufferedReader(new InputStreamReader(instreamSql));

        final StringBuffer sql = new StringBuffer(instreamSql.available());
        for (String sqlLine = inSql.readLine(); sqlLine != null; sqlLine = inSql.readLine())
        {
            sql.append(sqlLine);
            sql.append(" \n");
        }

        return sql.toString();
    }
}
