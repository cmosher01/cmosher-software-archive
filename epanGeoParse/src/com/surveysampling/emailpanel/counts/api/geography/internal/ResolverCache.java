/*
 * Created on May 16, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.GeographicArea;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.ExactResolver;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.FuzzyResolver;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.sql.DatabaseUtil;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Caches all necessary geographic names (and abbreviations),
 * organized by type of geography (DMA name, state name, etc.)
 * 
 * @author Chris Mosher
 */
public class ResolverCache
{
    private final DatalessKeyAccess accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final DatalessKeyAccess accessKeyGeoType = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");

    private final Map mapTypeToNameResolver = new HashMap(); // <GeographyEnumType,Resolver>
    private final Map mapTypeToAbbrevResolver = new HashMap(); // <GeographyEnumType,Resolver>

    private final Map mapGeoKeyToName = new HashMap(5000); // <DatalessKey,String>



    /**
     * @throws DatalessKeyAccessCreationException
     */
    public ResolverCache() throws DatalessKeyAccessCreationException
    {
    }

    /**
     * @param db
     * @throws SQLException
     */
    public void fill(final Connection db) throws SQLException
    {
        synchronized (db)
        {
            fillCacheName(db);
            fillCacheAbbrev(db);
        }
    }

    private void fillCacheName(final Connection db) throws SQLException
    {
        Statement st = null;
        try
        {
            final String sql =
                "SELECT supportedGeoTypeID, geoID, name FROM GeographicName ORDER BY supportedGeoTypeID";

            st = db.createStatement();
            final ResultSet rs = st.executeQuery(sql);

            fillResolvers(rs,true,true,this.mapTypeToNameResolver);
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
        }
    }

    private void fillCacheAbbrev(final Connection db) throws SQLException
    {
        Statement st = null;
        try
        {
            final String sql =
                "SELECT supportedGeoTypeID, geoID, name FROM GeographicAbbrev ORDER BY supportedGeoTypeID";

            st = db.createStatement();
            final ResultSet rs = st.executeQuery(sql);

            fillResolvers(rs,false,false,this.mapTypeToAbbrevResolver);
        }
        finally
        {
            DatabaseUtil.closeStatementNoThrow(st);
        }
    }

    private void fillResolvers(final ResultSet rs, final boolean fuzzy, final boolean addToKeyMap, final Map mapResolver) throws SQLException
    {
        GeographyEnumType prevGeoType = null;
        Resolver resolver = null;
        while (rs.next())
        {
            final DatalessKey keyType = this.accessKeyGeoType.createKeyFromResultSet(rs,rs.findColumn("supportedGeoTypeID"));
            final GeographyEnumType geoType = GeographyEnumType.fromString(DatalessKeyAccessFactory.getDatalessKeyAccess(keyType).keyAsString(keyType));
            if (prevGeoType == null || !geoType.equals(prevGeoType))
            {
                if (fuzzy)
                {
                    resolver = new FuzzyResolver();
                }
                else
                {
                    resolver = new ExactResolver();
                }
                mapResolver.put(geoType,resolver);
                prevGeoType = geoType;
            }

            final DatalessKey keyGeo = this.accessKeyGeo.createKeyFromResultSet(rs,rs.findColumn("geoID"));
            final String name = rs.getString("name").trim();
            resolver.put(new GeographicArea(keyGeo,name));

            if (addToKeyMap)
            {
                this.mapGeoKeyToName.put(keyGeo,name);
            }
        }
    }

    /**
     * @param geoType
     * @return the <code>Resolver</code> for the given type of geography
     */
    public Resolver getNameResolver(final GeographyEnumType geoType)
    {
        if (!this.mapTypeToNameResolver.containsKey(geoType))
        {
            throw new IllegalStateException();
        }
        return (Resolver)this.mapTypeToNameResolver.get(geoType);
    }

    /**
     * @param geoType
     * @return the <code>Resolver</code> for the given type of geography
     */
    public Resolver getAbbreviationResolver(final GeographyEnumType geoType)
    {
        if (!this.mapTypeToAbbrevResolver.containsKey(geoType))
        {
            throw new IllegalStateException();
        }
        return (Resolver)this.mapTypeToAbbrevResolver.get(geoType);
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

    /**
     * @return a new CountyParser to parse county/state names
     */
    public CountyParser getCountyParser()
    {
        return new CountyParser(this,getAbbreviationResolver(GeographyEnumType.STATE),getNameResolver(GeographyEnumType.STATE),getNameResolver(GeographyEnumType.COUNTY),this.accessKeyGeo);
    }
}
