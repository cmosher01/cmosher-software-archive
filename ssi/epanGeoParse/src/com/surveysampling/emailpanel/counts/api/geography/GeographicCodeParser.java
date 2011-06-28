/*
 * Created on May 27, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.internal.ResolverCodeCache;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.util.ProgressWatcher;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Parses lists of geographic codes, and returns a list of
 * resulting matches.
 * The codes are read from a given database using the following
 * SQL statement (or equivalent):
 * <code>SELECT supportedGeoTypeID, geoID, code, name FROM GeographicCode</code>
 * 
 * @author Chris Mosher
 */
public class GeographicCodeParser
{
    private final ResolverCodeCache cacheResolvers;

    /**
     * Initialize this parser. Uses the given <code>Connection</code>
     * to a database, which must be properly set up, to read valid
     * geographic codes, names, and primary keys from. The given <code>Connection</code>
     * is only used during this method; after this object is constructed
     * the database connection can be closed by the caller if desired.
     * @param db
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     */
    public GeographicCodeParser(final Connection db) throws DatalessKeyAccessCreationException, SQLException
    {
        this.cacheResolvers = new ResolverCodeCache();
        this.cacheResolvers.fill(db);
    }

    /**
     * Given a list of <code>String</code>s, try to match each one
     * as a <code>geoType</code> code.
     * @param rCodeToSearchFor <code>Collection</code> of <code>String</code>
     * codes to search for (in order of its <code>iterator</code>).
     * @param geoType type of geography that the names in <code>rNameToSearchFor</code>
     * represent: <code>GeographyEnumType.FIPS</code> is currently the only supported code,
     * although <code>GeographyEnumType.ZIP</code> may be added in the future.
     * @param appendMatchesTo <code>List</code> to append matches to. One item is appended
     * to this <code>List</code> for each <code>String</code> given in <code>rNameToSearchFor</code>
     * Each object appended will be an <code>ArrayList</code> of <code>GeograpicAreaCode</code> objects.
     * @param maxMatches maximum number of matches to return for each code.
     * @param progressWatcher a <code>ProgressWatcher</code> that this object notifies after
     * each given code is resolved; or <code>null</code> if progress information is not required
     */
    public void parseGeo(final Collection rCodeToSearchFor, final GeographyEnumType geoType, final List appendMatchesTo, final int maxMatches, final ProgressWatcher progressWatcher)
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

        final Resolver resolver = this.cacheResolvers.getCodeResolver(geoType);

        watcher.setTotalSteps(rCodeToSearchFor.size());
        int i = 0;
        for (final Iterator iSearch = rCodeToSearchFor.iterator(); iSearch.hasNext();)
        {
            final String code = (String)iSearch.next();
            watcher.setProgress(i++,code);

            final List rMatch = new ArrayList(maxMatches);
            resolver.resolve(code,rMatch,maxMatches);
            appendMatchesTo.add(rMatch);
        }
    }

    /**
     * ??? do we need this?
     * Look up the name of a given geographic area.
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
