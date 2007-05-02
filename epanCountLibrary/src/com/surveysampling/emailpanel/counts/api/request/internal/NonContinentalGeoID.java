/*
 * Created on Dec 19, 2005
 *
 */
package com.surveysampling.emailpanel.counts.api.request.internal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
/**
 * An object used to cache the geoid's of states
 * that are not part of the Continental USA 
 * (e.g Alaska and Hawaii)

 * @author james
 */
public class NonContinentalGeoID implements Iterable<Integer>
{

    private List<Integer> listOfGeoID;
    private final DatalessKeyAccess keyAccess;
    
    /**
     * Constructor
     * 
     * @param db    the connection
     * @param ka    DatalessKeyAccess used for the SQL query
     * @throws SQLException
     */
    public NonContinentalGeoID (final Connection db, DatalessKeyAccess ka) throws SQLException
    {
        this.keyAccess = ka;
        this.listOfGeoID = new ArrayList<Integer>();
        fillCacheExcludedGeos(db);
    }
    
    /**
     * Creates the sql query and stores the results. The results are typically
     * int representations of the geoID.
     * 
     * @param db    the connection
     * @throws SQLException
     */
    private void fillCacheExcludedGeos(final Connection db) throws SQLException
    {
        Statement st = null;
        try
        {
            StringBuffer sb = new StringBuffer();
            sb.append("SELECT geoID FROM GeoExcludes WHERE ");
            sb.append("supportedGeoTypeID = ");
            final DatalessKey keyGeo = this.keyAccess.createKeyFromString(GeographyEnumType.CONTINENTAL.getValue());
            this.keyAccess.putKeyAsSQLFunction(keyGeo,sb);
            
            final String sql = sb.toString();
            st = db.createStatement();
            final ResultSet rs = st.executeQuery(sql);
            while (rs.next())
            {
                listOfGeoID.add(rs.getInt("GEOID"));
            }

            if ((listOfGeoID.size()<=0)||(listOfGeoID.size()>1000))
                throw new IllegalStateException("NO GEOIDS or MORE THAN 1000 GEOIDS RETURNED");
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
        }
    }
    
    /**
     * 
     */
    public Iterator<Integer> iterator()
    {
        return listOfGeoID.iterator();
    }
}
