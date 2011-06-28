/*
 * Created on May 11, 2005
 */
package com.surveysampling.emailpanel.counts.api.criteria;

import com.surveysampling.emailpanel.counts.api.geography.GeographicCodeParser;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.util.key.DatalessKey;

/**
 * Builds the name for an <code>EpanCount</code> query.
 * 
 * @author Chris Mosher
 */
public class EpanCountDisplayNameBuilder
{
    private final GeographicNameParser lookupGeoName;
    private final GeographicCodeParser lookupGeoCode;

    /**
     * Initializes this builder with the given
     * <code>GeographicNameParser</code>, which will
     * use to look up geographic names by key,
     * as passed in to <code>appendDisplayName</code>.
     * @param lookupGeoName
     * @param lookupGeoCode
     */
    public EpanCountDisplayNameBuilder(final GeographicNameParser lookupGeoName, final GeographicCodeParser lookupGeoCode)
    {
        this.lookupGeoName = lookupGeoName;
        this.lookupGeoCode = lookupGeoCode;
    }

    /**
     * Builds the name for an <code>EpanCount</code> query.
     * Specifically, if the request if for only one count
     * (that is, it is not broken out by either geography
     * nor gender), then appends nothing; if broken out by
     * gender only, appends M or F; if broken out by geography
     * only, appends the name of the geographic area; if
     * broken out by both, appends M or F, a space, and
     * the geographic area name.
     * The built name is appended to the given <code>StringBuffer</code>.
     * @param gender gender for this query, if broken out
     * by gender (<code>null</code>, otherwise)
     * @param keyGeo primary key of the geographic area for this query,
     * if broken out by geography (<code>null</code>, otherwise).
     * @param s <code>StringBuffer</code> to append the built name to
     */
    public void appendDisplayName(final GenderEnumType gender, final DatalessKey keyGeo, final StringBuffer s)
    {
        boolean have = false;

        if (gender != null)
        {
            if (have)
            {
                s.append(" ");
            }
            appendGenderDisplay(gender,s);
            have = true;
        }

        if (keyGeo != null)
        {
            if (have)
            {
                s.append(" ");
            }
            appendGeoDisplay(keyGeo,s);
            have = true;
        }
    }





    protected void appendGenderDisplay(final GenderEnumType gender, final StringBuffer s)
    {
        if (gender.equals(GenderEnumType.MALE))
        {
            s.append("M");
        }
        else if (gender.equals(GenderEnumType.FEMALE))
        {
            s.append("F");
        }
    }

    protected void appendGeoDisplay(final DatalessKey keyGeo, final StringBuffer s)
    {
        try
        {
            s.append(this.lookupGeoName.lookup(keyGeo));
            return;
        }
        catch (final IllegalStateException e)
        {
            // geography key not found in list of names
        }

        try
        {
            s.append(this.lookupGeoCode.lookup(keyGeo));
            return;
        }
        catch (final IllegalStateException e)
        {
            // geography key not found in list of codes
        }

        s.append("[unknown geography]");
    }
}
