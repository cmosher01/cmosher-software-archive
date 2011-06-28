/*
 * Created on May 27, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.GeographicAreaCode;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.CodeResolver;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Caches all necessary geographic codes (and their names),
 * organized by type of geography (DMA name, state name, etc.)
 * 
 * @author Chris Mosher
 */
public class ResolverCodeCache
{
    private final DatalessKeyAccess accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final DatalessKeyAccess accessKeyGeoType = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");

    private final Map mapTypeToCodeResolver = new HashMap(); // <GeographyEnumType,Resolver>

    private final Map mapGeoKeyToName = new HashMap(5000); // <DatalessKey,String>



    /**
     * @throws DatalessKeyAccessCreationException
     */
    public ResolverCodeCache() throws DatalessKeyAccessCreationException
    {
    }

    /**
     * Fills this cache with geographic codes read from the
     * given database connection.
     * @param db database connection
     * @throws SQLException
     */
    public void fill(final Connection db) throws SQLException
    {
        synchronized (db)
        {
            Statement st = null;
            try
            {
                final String sql =
                    "SELECT supportedGeoTypeID, geoID, code, name FROM GeographicCode ORDER BY supportedGeoTypeID";
    
                st = db.createStatement();
                final ResultSet rs = st.executeQuery(sql);
    
                fillResolvers(rs);
            }
            finally
            {
                DatabaseUtil.closeStatementNoThrow(st);
            }
        }
    }

    private void fillResolvers(final ResultSet rs) throws SQLException
    {
        GeographyEnumType prevGeoType = null;
        Resolver resolver = null;
        while (rs.next())
        {
            final DatalessKey keyType = this.accessKeyGeoType.createKeyFromResultSet(rs,rs.findColumn("supportedGeoTypeID"));
            final GeographyEnumType geoType = GeographyEnumType.fromString(DatalessKeyAccessFactory.getDatalessKeyAccess(keyType).keyAsString(keyType));
            if (prevGeoType == null || !geoType.equals(prevGeoType))
            {
                resolver = new CodeResolver();
                this.mapTypeToCodeResolver.put(geoType,resolver);
                prevGeoType = geoType;
            }

            final DatalessKey keyGeo = this.accessKeyGeo.createKeyFromResultSet(rs,rs.findColumn("geoID"));
            final String code = rs.getString("code").trim();
            final String name = rs.getString("name").trim();
            resolver.put(new GeographicAreaCode(keyGeo,code,name));
            this.mapGeoKeyToName.put(keyGeo,code+" "+name);
        }
    }

    /**
     * Gets the resolver for the given type of geography.
     * @param geoType
     * @return the <code>Resolver</code> for the given type of geography
     */
    public Resolver getCodeResolver(final GeographyEnumType geoType)
    {
        if (!this.mapTypeToCodeResolver.containsKey(geoType))
        {
            throw new IllegalStateException();
        }
        return (Resolver)this.mapTypeToCodeResolver.get(geoType);
    }

    /**
     * Looks up a geographic name given its key.
     * @param keyGeo
     * @return the name. If key <code>isNull</code> returns empty string
     */
    public String lookupName(final DatalessKey keyGeo)
    {
        if (keyGeo.isNull())
        {
            return "";
        }
        if (!this.mapGeoKeyToName.containsKey(keyGeo))
        {
            throw new IllegalStateException();
        }
        return (String)this.mapGeoKeyToName.get(keyGeo);
    }
}
