package com.surveysampling.mosher.sql;

import java.util.Map;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This class consists exclusively of static methods useful for handling
 * SQL statements and results.
 * 
 * @author Chris Mosher
 */
public class SQL
{

    /**
     * Gets the column names and values from the current row in the
     * given ResultSet, and adds them to the given Map. The column name
     * is the key, and the column's data is the value. All values
     * are of type String. This method calls Map.put to update the
     * map, so if an entry already exists in the map for one of the
     * column names, then its value will be overwritten.
     * 
     * @param rs the ResultSet whose current row is read to form the map
     * @return the HashMap of column names (keys) to their values
     * @exception java.sql.SQLException
     */
    public static void mapResultSet(ResultSet rs, Map map)
        throws SQLException
    {
        ResultSetMetaData meta = rs.getMetaData();

        for (int i = 0; i < meta.getColumnCount(); ++i)
            map.put(meta.getColumnName(i),rs.getString(i));
    }
}
