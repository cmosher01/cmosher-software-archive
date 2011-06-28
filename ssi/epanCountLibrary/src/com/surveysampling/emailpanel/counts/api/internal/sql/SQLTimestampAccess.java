/*
 * Created on April 27, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

/**
 * Provides static methods to access SQL timestamp columns
 * and convert the values to and from <code>java.util.Date</code>
 * objects.
 * 
 * @author Chris Mosher
 */
public final class SQLTimestampAccess
{
    private SQLTimestampAccess()
    {
        assert false;
    }

    /**
     * @param rs
     * @param column
     * @return new <code>Date</code>, or <code>null</code>
     * @throws SQLException
     */
    public static Date getTimestampFromResultSet(final ResultSet rs, final int column) throws SQLException
    {
        final Timestamp timestamp = rs.getTimestamp(column);
        if (rs.wasNull())
        {
            return null;
        }

        return new Date(timestamp.getTime());
    }

    /**
     * @param date or <code>null</code>
     * @param st
     * @param parameterIndex
     * @throws SQLException
     */
    public static void putTimestampToPreparedStatement(final Date date, final PreparedStatement st, final int parameterIndex) throws SQLException
    {
        if (date == null)
        {
            st.setNull(parameterIndex,Types.TIMESTAMP);
            return;
        }

        st.setTimestamp(parameterIndex, new Timestamp(date.getTime()));
    }
}
