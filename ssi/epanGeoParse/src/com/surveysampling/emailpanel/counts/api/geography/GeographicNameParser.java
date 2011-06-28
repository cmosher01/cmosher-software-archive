/*
 * Created on May 23, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.internal.CountyParser;
import com.surveysampling.emailpanel.counts.api.geography.internal.ResolverCache;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.util.ProgressWatcher;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Parses lists of geographic names, and returns a list of
 * resulting fuzzy matches.
 * The names are read from a given database using the following
 * two SQL statements (or equivalent):
 * <code>SELECT supportedGeoTypeID, geoID, name FROM GeographicName</code>
 * and
 * <code>SELECT supportedGeoTypeID, geoID, name FROM GeographicAbbrev</code>
 * @author Chris Mosher
 */
public class GeographicNameParser
{
    private final ResolverCache cacheResolvers;
    private final DatalessKeyAccess accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");

    /**
     * Initialize this parser. Uses the given <code>Connection</code>
     * to a database, which must be properly set up, to read valid
     * geographic names (and primary keys) from. The given <code>Connection</code>
     * is only used during this method; after this object is constructed
     * the database connection can be closed by the caller if desired.
     * @param db
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     */
    public GeographicNameParser(final Connection db) throws DatalessKeyAccessCreationException, SQLException
    {
        this.cacheResolvers = new ResolverCache();
        this.cacheResolvers.fill(db);
    }

    /**
     * Given a list of <code>String</code>s, try to match each one
     * as a <code>geoType</code> name. It will parse DMA names,
     * MSA names, state names, state postal abbreviations, or
     * county names (w/ state name or abbreviation). Note that
     * the state name or abbreviation may be omitted on subsequent
     * county names and the same state as the prior county will
     * be assumed. Each given name may match multiple actual
     * geographic areas.
     * @param rNameToSearchFor <code>Collection</code> of <code>String</code>
     * names to search for (in order of its <code>iterator</code>).
     * @param geoType type of geography that the names in <code>rNameToSearchFor</code>
     * represent: <code>GeographyEnumType.STATE</code>, <code>GeographyEnumType.COUNTY</code>, 
     * <code>GeographyEnumType.DMA</code>, or <code>GeographyEnumType.MSA</code>.
     * @param appendMatchesTo <code>List</code> to append matches to. One item is appended
     * to this <code>List</code> for each <code>String</code> given in <code>rNameToSearchFor</code>
     * Each object appended will be an <code>ArrayList</code> of <code>GeograpicArea</code> objects.
     * @param maxMatches maximum number of matches to return for each name.
     * @param progressWatcher a <code>ProgressWatcher</code> that this object notifies after
     * each given name is resolved; or <code>null</code> if progress information is not required
     */
    public void parseGeo(final Collection rNameToSearchFor, final GeographyEnumType geoType, final List appendMatchesTo, final int maxMatches, final ProgressWatcher progressWatcher)
    {
        ProgressWatcher watcher = progressWatcher;
        if (watcher == null)
        {
            watcher = new ProgressWatcher()
            {
                public void setTotalSteps(int p) {}
                public void setProgress(int p, String s) {}
            };
        }

        if (geoType.equals(GeographyEnumType.STATE))
        {
            resolveState(rNameToSearchFor,appendMatchesTo,maxMatches,watcher);
        }
        else if (geoType.equals(GeographyEnumType.COUNTY))
        {
            resolveCounty(rNameToSearchFor,appendMatchesTo,maxMatches,watcher);
        }
        else if (geoType.equals(GeographyEnumType.DMA))
        {
            resolveSimple(GeographyEnumType.DMA,rNameToSearchFor,appendMatchesTo,maxMatches,watcher);
        }
        else if (geoType.equals(GeographyEnumType.MSA))
        {
            resolveSimple(GeographyEnumType.MSA,rNameToSearchFor,appendMatchesTo,maxMatches,watcher);
        }
        else
        {
            throw new IllegalArgumentException("invalid geography type");
        }
    }

    private void resolveCounty(final Collection rNameToSearchFor, final List appendMatchesTo, final int maxMatches, final ProgressWatcher watcher)
    {
        final CountyParser parser = this.cacheResolvers.getCountyParser();

        GeographicArea stateDefault = new GeographicArea(accessKeyGeo.createNullKey(),"NULL");

        watcher.setTotalSteps(rNameToSearchFor.size());
        int i = 0;
        for (final Iterator iSearch = rNameToSearchFor.iterator(); iSearch.hasNext();)
        {
            final String name = (String)iSearch.next();
            watcher.setProgress(i++,name);

            final List rMatch = new ArrayList(maxMatches);
            stateDefault = parser.resolveCounty(name,stateDefault,rMatch,maxMatches);
            appendMatchesTo.add(rMatch);
        }
    }

    private void resolveState(final Collection rNameToSearchFor, final List appendMatchesTo, final int maxMatches, final ProgressWatcher watcher)
    {
        final Resolver resolverName = this.cacheResolvers.getNameResolver(GeographyEnumType.STATE);
        final Resolver resolverAbbrev = this.cacheResolvers.getAbbreviationResolver(GeographyEnumType.STATE);

        watcher.setTotalSteps(rNameToSearchFor.size());
        int i = 0;
        for (final Iterator iSearch = rNameToSearchFor.iterator(); iSearch.hasNext();)
        {
            final String name = (String)iSearch.next();
            watcher.setProgress(i++,name);

            final List rMatch = new ArrayList(maxMatches);
            if (name.length() == 2)
            {
                resolverAbbrev.resolve(name,rMatch,1);
            }
            else
            {
                resolverName.resolve(name,rMatch,maxMatches);
            }
            appendMatchesTo.add(rMatch);
        }
    }

    private void resolveSimple(final GeographyEnumType geoType, final Collection rNameToSearchFor, final List appendMatchesTo, final int maxMatches, final ProgressWatcher watcher)
    {
        final Resolver resolverName = this.cacheResolvers.getNameResolver(geoType);

        watcher.setTotalSteps(rNameToSearchFor.size());
        int i = 0;
        for (final Iterator iSearch = rNameToSearchFor.iterator(); iSearch.hasNext();)
        {
            final String name = (String)iSearch.next();
            watcher.setProgress(i++,name);

            final List rMatch = new ArrayList(maxMatches);
            resolverName.resolve(name,rMatch,maxMatches);
            appendMatchesTo.add(rMatch);
        }
    }

    /** Look up the name of a given geographic area.
     * @param keyGeo the key of the area to look up
     * @return the name, or empty string if the given key
     * <code>isNull</code>
     * @throws IllegalStateException if <code>keyGeo</code> is not found
     */
    public String lookup(final DatalessKey keyGeo) throws IllegalStateException
    {
        return this.cacheResolvers.lookupName(keyGeo);
    }
}
