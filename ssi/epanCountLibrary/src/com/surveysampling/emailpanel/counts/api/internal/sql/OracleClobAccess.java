/*
 * Created on Jun 1, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.sql.CLOB;

/**
 * Static method to create an Oracle CLOB.
 * 
 * @author Chris Mosher
 */
public final class OracleClobAccess
{
    private OracleClobAccess()
    {
        assert false;
    }

    /**
     * Puts an Oracle CLOB column's value into a <code>PreparedStatement</code>.
     * @param readFrom <code>StringBuffer</code> to get the XML from
     * @param st <code>PreparedStatement</code> to write the XML to
     * @param parameterIndex index in <code>st</code> of the XMLType column to write the XML to
     * @throws SQLException
     */
    public static void putClobToPreparedStatement(final StringBuffer readFrom, final PreparedStatement st, final int parameterIndex) throws SQLException
    {
        st.setObject(parameterIndex,OracleClobAccess.createClob(readFrom,st.getConnection()));
    }

    /**
     * For example:
     * <blockquote><pre>
     * PreparedStatement st = db.prepareStatement("INSERT INTO Xtable VALUES(XMLTYPE(?))");
     * st.setObject(1,createClob("<test />",db));
     * st.executeUpdate();
     * </pre></blockquote>
     * @param readFrom
     * @param db
     * @return
     * @throws SQLException
     */
    public static CLOB createClob(final StringBuffer readFrom, final Connection db) throws SQLException
    {
        CLOB clob = null;
        boolean ok = false;
        try
        {
            clob = CLOB.createTemporary(db,true,CLOB.DURATION_SESSION);
            clob.open(CLOB.MODE_READWRITE);

            try
            {
                final Writer wClob = clob.setCharacterStream(0L);
                wClob.write(readFrom.toString());
                wClob.flush();
                wClob.close();
            }
            catch (final IOException e)
            {
                /*
                 * Wrap any IOException in a SQLException. I think
                 * SQLException makes more sense to our caller. Further,
                 * Oracle is just making these IOExceptions as wrappers
                 * for what were SQLExceptions in the first place.
                 */
                throw (SQLException)new SQLException().initCause(e);
            }

            clob.close();

            ok = true;
        }
        finally
        {
            if (!ok)
            {
                if (clob != null)
                {
                    try
                    {
                        clob.freeTemporary();
                    }
                    catch (final Throwable e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return clob;
    }

    /**
     * Gets an Oracle CLOB column's value from a <code>ResultSet</code>,
     * and appends it to the given <code>StringBuffer</code>.
     * @param rs <code>ResultSet</code> to read the CLOB from
     * @param column index in <code>rs</code> of the CLOB column to read from
     * @param appendTo <code>StringBuffer</code> to append the CLOB's data to
     * @throws SQLException
     */
    public static void getClobFromResultSet(final ResultSet rs, final int column, final StringBuffer appendTo) throws SQLException
    {
        final Clob clob = rs.getClob(column);

        final Reader xml = clob.getCharacterStream();
        final Writer buffer = new StringWriter();

        try
        {
            copyAllLines(xml,buffer);
        }
        catch (final IOException e)
        {
            // Wrap any IOException in a SQLException, which makes more sense
            throw (SQLException)new SQLException().initCause(e);
        }

        appendTo.append(buffer);
    }

    private static void copyAllLines(final Reader readerFrom, final Writer writerTo) throws IOException
    {
        final BufferedReader in = new BufferedReader(readerFrom);
        final BufferedWriter out = new BufferedWriter(writerTo);

        for (String s = in.readLine(); s != null; s = in.readLine())
        {
            out.write(s);
            out.newLine();
        }

        out.close();
        in.close();
    }
}
