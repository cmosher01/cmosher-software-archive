/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key.impl;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import nu.mine.mosher.uuid.UUIDFactory;

import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;

/**
 * Access class for <code>DatalessKey</code> storing
 * key as a <code>UUID</code>.
 * 
 * @author Chris Mosher
 */
public final class DatalessKeyAccessUUID implements DatalessKeyAccess
{
    DatalessKeyAccessUUID()
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
        final byte[] key = rs.getBytes(column);
        if (rs.wasNull())
        {
            return createNullKey();
        }
        return new DatalessKeyUUID(key);
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
            st.setNull(parameterIndex,Types.VARBINARY);
        }
        else
        {
            st.setBytes(parameterIndex,((DatalessKeyUUID)key).asByteArray());
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
        s.append("keyValueUUID(\'");
        s.append(((DatalessKeyUUID)key).asString());
        s.append("\')");
    }

    /**
     * @param key
     * @param s
     * @throws UnsupportedOperationException
     */
    public void putKeyAsSQLNumber(DatalessKey key, StringBuffer s) throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
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
        return new DatalessKeyUUID(key);
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
        return ((DatalessKeyUUID)key).asString();
    }

    /**
     * Creates a new <code>DatalessKey</code> represented by
     * a newly generated UUID. 
     * @return the new <code>DatalessKey</code>
     * @throws UnsupportedOperationException always throws
     */
    public DatalessKey createNewKey()
    {
        return new DatalessKeyUUID();
    }

    /**
     * @see com.surveysampling.util.key.DatalessKeyAccess#createNullKey()
     */
    public DatalessKey createNullKey()
    {
        return new DatalessKeyUUID(true);
    }

    /**
     * Implementation of <code>DatalessKey</code> storing
     * the key as a <code>UUID</code>.
     * 
     * @author Chris Mosher
     */
    private final static class DatalessKeyUUID extends DatalessKeyImpl implements Serializable
    {
        private final boolean nul;
        private final UUID key;

        private DatalessKeyUUID()
        {
            this.nul = false;
            this.key = new UUIDFactory().createUUID();
        }

        private DatalessKeyUUID(final boolean isNull)
        {
            assert isNull;
            this.nul = true;
            this.key = null;
        }
        DatalessKeyUUID(final byte[] rb)
        {
            this.nul = false;
            this.key = UUID.nameUUIDFromBytes(rb);// TODO fix these
        }

        DatalessKeyUUID(final String key)
        {
            this.nul = false;
            this.key = UUID.fromString(key);
        }

        byte[] asByteArray()
        {
            assert !this.nul;
            return null;// TODO fix these
        }

        String asString()
        {
            assert !this.nul;
            return this.key.toString();
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#equals(java.lang.Object)
         */
        public boolean equals(final Object obj)
        {
            if (!(obj instanceof DatalessKeyUUID))
            {
                return false;
            }

            final DatalessKeyUUID that = (DatalessKeyUUID)obj;
            if (this.nul && that.nul)
            {
                return true;
            }
            else if (this.nul || that.nul)
            {
                return false;
            }
            return this.key.equals(that.key);
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#hashCode()
         */
        public int hashCode()
        {
            if (this.nul)
            {
                return 0;
            }
            return this.key.hashCode();
        }

        /**
         * For debugging purposes only.
         * @return string
         */
        public String toString()
        {
            return "[DatalessKeyUUID: "+getAccess().keyAsString(this) + "]";
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#isNull()
         */
        public boolean isNull()
        {
            return this.nul;
        }

        /**
         * @see com.surveysampling.util.key.DatalessKey#getAccess()
         */
        public DatalessKeyAccess getAccess()
        {
            return new DatalessKeyAccessUUID();
        }
    }
}
