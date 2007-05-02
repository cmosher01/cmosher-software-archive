/*
 * Created on Jun 10, 2005
 */
package com.surveysampling.emailpanel.counts.api.criteria;

import com.surveysampling.emailpanel.counts.api.internal.DisjointRange;

/**
 * Provides static methods hard-coded with specific
 * values of some criteria-related enumerations.
 * 
 * @author Chris Mosher
 */
public final class EpanCountEnumNames
{
    private EpanCountEnumNames()
    {
        assert false;
    }

    // TODO look up criteria-related data in database instead of hard-coding values

    /**
     * Given an income range, return a <code>DisjointRange</code>
     * that represents its income range.
     * @param inc
     * @return <code>DisjointRange</code>
     */
    public static DisjointRange getRangeOfIncome(final IncomeEnumType inc)
    {
        if (inc.equals(IncomeEnumType.MIN_0_K))
        {
            return new DisjointRange(0,20);
        }
        else if (inc.equals(IncomeEnumType.MIN_20_K))
        {
            return new DisjointRange(20,30);
        }
        else if (inc.equals(IncomeEnumType.MIN_30_K))
        {
            return new DisjointRange(30,40);
        }
        else if (inc.equals(IncomeEnumType.MIN_40_K))
        {
            return new DisjointRange(40,50);
        }
        else if (inc.equals(IncomeEnumType.MIN_50_K))
        {
            return new DisjointRange(50,60);
        }
        else if (inc.equals(IncomeEnumType.MIN_60_K))
        {
            return new DisjointRange(60,75);
        }
        else if (inc.equals(IncomeEnumType.MIN_75_K))
        {
            return new DisjointRange(75,100);
        }
        else if (inc.equals(IncomeEnumType.MIN_100_K))
        {
            return new DisjointRange(100,150);
        }
        else if (inc.equals(IncomeEnumType.MIN_150_K))
        {
            return new DisjointRange(150,Integer.MAX_VALUE);
        }
        throw new IllegalStateException();

    }

    /**
     * Given an ethnicity, get its name
     * @param eth
     * @return ethnicity name
     */
    public static String ethnicityString(final EthnicityEnumType eth)
    {
        if (eth.equals(EthnicityEnumType.BLACK))
        {
            return "Black";
        }
        if (eth.equals(EthnicityEnumType.HISPANIC))
        {
            return "Hispanic";
        }
        if (eth.equals(EthnicityEnumType.WHITE))
        {
            return "White";
        }
        if (eth.equals(EthnicityEnumType.ASIAN))
        {
            return "Asian";
        }
        if (eth.equals(EthnicityEnumType.PACIFIC))
        {
            return "Pacific";
        }
        if (eth.equals(EthnicityEnumType.INDIAN))
        {
            return "Indian";
        }
        if (eth.equals(EthnicityEnumType.OTHER))
        {
            return "[other]";
        }
        throw new IllegalStateException();
    }

    /**
     * Given an education type, get its name
     * @param edu
     * @return education type name
     */
    public static String educationString(final EducationEnumType edu)
    {
        if (edu.equals(EducationEnumType.COMPLETED_SOME_HIGH_SCHOOL))
        {
            return "completed some high school";
        }
        if (edu.equals(EducationEnumType.HIGH_SCHOOL_GRADUATE))
        {
            return "high school graduate";
        }
        if (edu.equals(EducationEnumType.COMPLETED_SOME_COLLEGE))
        {
            return "completed some college";
        }
        if (edu.equals(EducationEnumType.COLLEGE_DEGREE))
        {
            return "college degree";
        }
        if (edu.equals(EducationEnumType.COMPLETED_SOME_POSTGRADUATE))
        {
            return "completed some post-graduate school";
        }
        if (edu.equals(EducationEnumType.MASTERS_DEGREE))
        {
            return "master\'s degree";
        }
        if (edu.equals(EducationEnumType.DOCTORATE_LAW_OR_PROFESSIONAL_DEGREE))
        {
            return "doctorate, law, or professional degree";
        }
        throw new IllegalStateException();
    }

    /**
     * Given a marital status, get its name
     * @param mar
     * @return marital status name
     */
    public static String marriedString(final MarriedEnumType mar)
    {
        if (mar.equals(MarriedEnumType.SINGLE_NEVER_MARRIED))
        {
            return "single (never married)";
        }
        if (mar.equals(MarriedEnumType.MARRIED))
        {
            return "married";
        }
        if (mar.equals(MarriedEnumType.SEPARATED_DIVORCED_WIDOWED))
        {
            return "separated, divorced, or widowed";
        }
        if (mar.equals(MarriedEnumType.DOMESTIC_PARTNERSHIP))
        {
            return "in a domestic partnership";
        }
        throw new IllegalStateException();
    }

    /**
     * Given a geography type, get its name
     * @param geoType
     * @return geography type name
     */
    public static String geoTypeString(final GeographyEnumType geoType)
    {
        if (geoType.equals(GeographyEnumType.CONTINENTAL))
        {
            return "USA (excluding Alaska and Hawaii)";
        }
        else if (geoType.equals(GeographyEnumType.USA))
        {
            return "USA (including Alaska and Hawaii)";
        }
        else if (geoType.equals(GeographyEnumType.ZIP))
        {
            return "US Postal Service 5-digit ZIP Codes";
        }
        else if (geoType.equals(GeographyEnumType.FIPS) || geoType.equals(GeographyEnumType.COUNTY))
        {
            return "US counties";
        }
        else if (geoType.equals(GeographyEnumType.MSA))
        {
            return "US Metropolitan Statistical Areas (OMB Bulletin 04-03, 12/2003) (MSAs)";
        }
        else if (geoType.equals(GeographyEnumType.DMA))
        {
            return "US Nielsen Media Research Designated Market Areas (DMAs)";
        }
        else if (geoType.equals(GeographyEnumType.STATE))
        {
            return "US states";
        }
        throw new IllegalStateException();
    }
}
