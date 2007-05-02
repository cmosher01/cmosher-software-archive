/*
 * Created on April 13, 2005
 */
package com.surveysampling.util.key;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Reads and writes <code>DatalessKey</code> objects.
 * Currently implemented as read a key from a <code>ResultSet</code>
 * and write a key to a <code>PreparedStatement</code>; or
 * translate a key to or from a <code>String</code>.
 * Use <code>DatalessKeyAccessFactory</code> to create an
 * instance of this class.
 * @author Chris Mosher
 */
public interface DatalessKeyAccess
{
    /**
     * Provides a mechanism to persist a <code>DatalessKey</code>
     * in a <code>java.sql.Connection</code>, along with
     * <code>putKeyToPreparedStatement</code>.
     * @param resultSet <code>ResultSet</code> to read the key from
     * @param column column number in <code>resultSet</code> to read the key from
     * @return a <code>DatalessKey</code> representing the key
     * specified by the given column
     * @throws SQLException
     */
    DatalessKey createKeyFromResultSet(ResultSet resultSet, int column) throws SQLException;

    /**
     * Provides a mechanism to persist a <code>DatalessKey</code>
     * in a <code>java.sql.Connection</code>, along with
     * <code>createKeyFromResultSet</code>.
     * @param key the <code>DatalessKey</code> to write
     * @param st <code>PreparedStatement</code> to write <code>key</code> to
     * @param parameterIndex index in <code>st</code> of the parameter ("?") to set
     * @throws SQLException
     * @throws ClassCastException if <code>key</code> is not of the correct
     * underlying type
     */
    void putKeyToPreparedStatement(DatalessKey key, PreparedStatement st, int parameterIndex) throws SQLException, ClassCastException;

    /**
     * Appends the key as a SQL function call to the
     * given SQL statement. The SQL function name is
     * of the form keyValueType where Type is the
     * underlying data type name of the key. This would
     * typically be used only when it was not possible
     * to use <code>putKeyToPreparedStatement</code>.
     * @param key
     * @param s SQL statement to append to
     */
    void putKeyAsSQLFunction(DatalessKey key, StringBuffer s);

    /**
     * Appends the key as a SQL numeric constant,
     * for example, "12345" (optional operation). This would
     * typically be used only when it was not possible
     * to use <code>putKeyToPreparedStatement</code>.
     * @param key
     * @param s SQL statement to append to
     */
    void putKeyAsSQLNumber(DatalessKey key, StringBuffer s);



    /**
     * Provides a mechanism to persist a <code>DatalessKey</code>
     * in a <code>String</code> (for example, XML or a properties file),
     * along with <code>keyAsString</code>.
     * @param s <code>String</code> representation of a key (previously
     * obtained by calling <code>keyAsString</code>).
     * @return a <code>DatalessKey</code> representing the key
     * specified by the given <code>String</code>
     */
    DatalessKey createKeyFromString(String s);

    /**
     * Provides a mechanism to persist a <code>DatalessKey</code>
     * in a <code>String</code> (for example, XML or a properties file),
     * along with <code>createKeyFromString</code>.
     * @param key <code>DatalessKey</code> to translate
     * @return <code>String</code> representation of <code>key</code>
     * @throws ClassCastException if <code>key</code> is not of the correct
     * underlying type
     */
    String keyAsString(DatalessKey key) throws ClassCastException;



    /**
     * Creates a new <code>DatalessKey</code> (optional operation).
     * The actual value of the key created depends upon the implementation.
     * @return the new <code>DatalessKey</code>
     * @throws UnsupportedOperationException if the implementation
     * doesn't support this operation
     */
    DatalessKey createNewKey() throws UnsupportedOperationException;

    /**
     * Creates a new <code>DatalessKey</code> whose <code>isNull</code>
     * method returns <code>true</code>.
     * @return a NULL <code>DatalessKey</code>
     */
    DatalessKey createNullKey();
}
