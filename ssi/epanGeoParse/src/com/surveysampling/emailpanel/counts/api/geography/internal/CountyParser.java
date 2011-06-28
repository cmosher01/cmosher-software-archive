/*
 * Created on May 19, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography.internal;

import java.util.ArrayList;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.geography.GeographicArea;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.util.key.DatalessKeyAccess;

/**
 * Special parser for a county name. The state is
 * either given by the user, or parsed from the
 * (end of) the given string.
 * 
 * @author Chris Mosher
 */
public class CountyParser
{
    private final ResolverCache rGeo;
    private final Resolver resolverStateAbbrev;
    private final Resolver resolverStateName;
    private final Resolver resolverCountyName;
    private final DatalessKeyAccess access;

    private GeographicArea state;



    /**
     * @param rGeo
     * @param resolverStateAbbrev
     * @param resolverStateName
     * @param resolverCountyName
     * @param access
     */
    public CountyParser(final ResolverCache rGeo, final Resolver resolverStateAbbrev, final Resolver resolverStateName, final Resolver resolverCountyName, final DatalessKeyAccess access)
    {
        this.rGeo = rGeo;
        this.resolverStateAbbrev = resolverStateAbbrev;
        this.resolverStateName = resolverStateName;
        this.resolverCountyName = resolverCountyName;
        this.access = access;
    }

    /**
     * @param nameCountyState
     * @param stateDefault
     * @param appendTo matching county (or counties). Each object
     * appended will itself be an <code>ArrayList</code> of
     * <code>GeographicArea</code> objects representing the matching
     * county or counties.
     * @param maxMatches
     * @return state! geo area (not county)
     */
    public GeographicArea resolveCounty(final String nameCountyState, final GeographicArea stateDefault, final List appendTo, final int maxMatches)
    {
        final String nameFiltered = filterNameComplete(nameCountyState);

        final String[] rWord = nameFiltered.split(" ");

        final String[] rLastWord = getLast3Words(rWord);

        final int nLastWordsState = parseState(rLastWord,rWord.length);

        final StringBuffer sbCounty = new StringBuffer(64);

        for (int i = 0; i < rWord.length-nLastWordsState; ++i)
        {
            if (!rWord[i].equalsIgnoreCase("county"))
            {
                sbCounty.append(rWord[i]);
                sbCounty.append(" ");
            }
        }

        if (this.state.getKey().isNull())
        {
            this.state = stateDefault;
        }

        if (sbCounty.length() > 0)
        {
            if (!this.state.getKey().isNull())
            {
                sbCounty.append(", ");
                sbCounty.append(this.rGeo.lookupName(this.state.getKey()));
            }
    
            this.resolverCountyName.resolve(sbCounty.toString(),appendTo,maxMatches);
        }

        return this.state;
    }

    /**
     * @param rLastWord
     * @param cWordsTotal
     * @return number of final words that make up the state name
     */
    private int parseState(final String[] rLastWord, final int cWordsTotal)
    {
        this.state = new GeographicArea(access.createNullKey(),"NULL");

        /*
         * if last word is exactly 2 characters
         *   if it is state abbreviation, return 1
         * end if
         * if more than 3 words
         *   if last 3 words is state name, return 3
         * end if
         * if more than 2 words
         *   if last 2 words is state name, return 2
         * end if
         * if more than 1 word
         *   if last 1 word is state name, return 1
         * end if
         * return 0
         */
        if (rLastWord[0].length() == 2)
        {
            this.state = resolveStateAbbrev(rLastWord[0]);
            if (!this.state.getKey().isNull())
            {
                return 1;
            }
        }
        if (cWordsTotal > 3)
        {
            this.state = resolveStateName(rLastWord[2]+" "+rLastWord[1]+" "+rLastWord[0]);
            if (!this.state.getKey().isNull())
            {
                return 3;
            }
        }
        if (cWordsTotal > 2)
        {
            this.state = resolveStateName(rLastWord[1]+" "+rLastWord[0]);
            if (!this.state.getKey().isNull())
            {
                return 2;
            }
        }
        if (cWordsTotal > 1)
        {
            this.state = resolveStateName(rLastWord[0]);
            if (!this.state.getKey().isNull())
            {
                return 1;
            }
        }
        return 0;
    }

    private GeographicArea resolveStateAbbrev(final String name)
    {
        final List rMatches = new ArrayList();
        this.resolverStateAbbrev.resolve(name,rMatches,1);
        if (rMatches.isEmpty())
        {
            return new GeographicArea(access.createNullKey(),"NULL");
        }
        return (GeographicArea)rMatches.get(0);
    }

    /**
     * @param name
     * @return state geo key matching given name, or null
     */
    private GeographicArea resolveStateName(final String name)
    {
        final List rMatches = new ArrayList();
        this.resolverStateName.resolve(name,rMatches,1);
        if (rMatches.isEmpty())
        {
            return new GeographicArea(access.createNullKey(),"NULL");
        }
        return (GeographicArea)rMatches.get(0);
    }




    /**
     * @param rWord
     * @return array of three Strings always
     */
    private static String[] getLast3Words(final String[] rWord)
    {
        final String[] rLastWord = new String[3];
        rLastWord[0] = getLastNWord(0,rWord);
        rLastWord[1] = getLastNWord(1,rWord);
        rLastWord[2] = getLastNWord(2,rWord);
        return rLastWord;
    }

    /**
     * @param nFromLast
     * @param rWord
     * @return n from last word in rWord, else empty string
     */
    private static String getLastNWord(final int nFromLast, final String[] rWord)
    {
        final int iWord = rWord.length-nFromLast-1;
        if (iWord < 0 || rWord.length <= iWord)
        {
            return "";
        }
        return rWord[iWord];
    }

    private static String filterNameComplete(final String name)
    {
        String s = name;

        s = s.toUpperCase();
        s = s.replaceAll("[^A-Z]"," ");
        s = s.replaceAll(" +"," ");
        s = s.trim();

        return s;
    }
}
