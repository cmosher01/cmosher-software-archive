/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;

/**
 * Access class for <code>DatalessKey</code> storing
 * key as a <code>long</code>.
 * 
 * @author Chris Mosher
 */
public final class DatalessKeyAccessLong implements DatalessKeyAccess
{
    DatalessKeyAccessLong()
    {
    }

    /**
     * @param rs
     * @param column
     * @return
     * @throws SQLException
     */
    public DatalessKey createKeyFromResultSet(final ResultSet rs, final int column) throws SQLException
    {
        final long key = rs.getLong(column);
        if (rs.wasNull())
        {
            return createNullKey();
        }
        return new DatalessKeyLong(key);
    }

    /**
     * @param key
     * @param st
     * @param parameterIndex
     * @throws SQLException
     * @throws ClassCastException
     */
    public void putKeyToPreparedStatement(final DatalessKey key, final PreparedStatement st, final int parameterIndex) throws SQLException, ClassCastException
    {
        if (key.isNull())
        {
            st.setNull(parameterIndex,Types.NUMERIC);
        }
        else
        {
            st.setLong(parameterIndex,((DatalessKeyLong)key).asLong());
        }
    }

    /**
     * @see com.surveysampling.util.key.DatalessKeyAccess#putKeyAsSQLFunction(com.surveysampling.util.key.DatalessKey, java.lang.StringBuffer)
     */
    public void putKeyAsSQLFunction(final DatalessKey key, final StringBuffer s)
    {
        if (key.isNull())
        {
            throw new UnsupportedOperationException("key cannot be null");
        }
        s.append("keyValueLong(\'");
        s.append(((DatalessKeyLong)key).asString());
        s.append("\')");
    }

    /**
     * @see com.surveysampling.util.key.DatalessKeyAccess#putKeyAsSQLNumber(com.surveysampling.util.key.DatalessKey, java.lang.StringBuffer)
     */
    public void putKeyAsSQLNumber(DatalessKey key, StringBuffer s)
    {
        if (key.isNull())
        {
            s.append("IS NULL");
            return;
        }

        s.append(((DatalessKeyLong)key).asLong());
    }

    /**
     * @param key
     * @return the new <code>DatalessKey</code>
     */
    public DatalessKey createKeyFromString(final String key)
    {
        if (key.equalsIgnoreCase("NULL"))
        {
            return createNullKey();
        }
        return new DatalessKeyLong(key);
    }

    /**
     * @param key the <code>DatalessKey</code> to translate
     * @return the <code>String</code> representation of <code>key</code>
     * @throws ClassCastException
     */
    public String keyAsString(final DatalessKey key) throws ClassCastException
    {
        if (key.isNull())
        {
            return "NULL";
        }
        return ((DatalessKeyLong)key).asString();
    }

    /**
     * Not implemented.
     * @return never returns
     * @throws UnsupportedOperationException always throws
     */
    public DatalessKey createNewKey() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * @see com.surveysampling.util.key.DatalessKeyAccess#createNullKey()
     */
    public DatalessKey createNullKey()
    {
        return new DatalessKeyLong();
    }
    
    /**
     * Implementation of <code>DatalessKey</code> storing
     * the key as a <code>long</code>.
     * 
     * @author Chris Mosher
     */
    private final static class DatalessKeyLong extends DatalessKeyImpl implements Serializable
    {
        private final long key;
        private final boolean nul;

        DatalessKeyLong(final long key)
        {
            this.nul = false;
            this.key = key;
        }

        DatalessKeyLong(final String key)
        {
            this.nul = false;
            this.key = Long.decode(key).longValue();
        }

        private DatalessKeyLong()
        {
            this.nul = true;
            this.key = Long.MIN_VALUE;
        }

        long asLong()
        {
            assert !this.nul;
            return this.key;
        }

        String asString()
        {
            assert !this.nul;
            return String.valueOf(this.key);
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#equals(java.lang.Object)
         */
        public boolean equals(final Object obj)
        {
            if (!(obj instanceof DatalessKeyLong))
            {
                return false;
            }

            final DatalessKeyLong that = (DatalessKeyLong)obj;
            if (this.nul && that.nul)
            {
                return true;
            }
            else if (this.nul || that.nul)
            {
                return false;
            }
            return this.key == that.key;
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#hashCode()
         */
        public int hashCode()
        {
            return (int)(this.key ^ (this.key >>> 32));
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#isNull()
         */
        public boolean isNull()
        {
            return this.nul;
        }

        /**
         * For debugging purposes only.
         * @return string
         */
        public String toString()
        {
            return "[DatalessKeyLong: " + getAccess().keyAsString(this) + "]";
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#getAccess()
         */
        public DatalessKeyAccess getAccess()
        {
            return new DatalessKeyAccessLong();
        }
    }
}
