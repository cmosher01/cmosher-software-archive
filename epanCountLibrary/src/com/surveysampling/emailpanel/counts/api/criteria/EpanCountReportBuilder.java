/*
 * Created on Jun 8, 2005
 */
package com.surveysampling.emailpanel.counts.api.criteria;

import java.math.BigInteger;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.surveysampling.emailpanel.counts.api.geography.GeographicCodeParser;
import com.surveysampling.emailpanel.counts.api.geography.GeographicNameParser;
import com.surveysampling.emailpanel.counts.api.internal.DisjointRange;
import com.surveysampling.emailpanel.counts.api.request.EpanCount;
import com.surveysampling.emailpanel.counts.api.request.EpanCountRequest;
import com.surveysampling.emailpanel.xdem.XdemCriteria;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Builds a text report of an <code>EpanCountRequest</code>.
 * The report consists of the client, topic, and all other
 * criteria, from the request, along with the count query
 * results, if the request has been run. The generated
 * report is formatted with spaces and newlines, and
 * is viewed best using a fixed-width font, such as Courier.
 * 
 * @author Chris Mosher
 */
public class EpanCountReportBuilder
{
    private final GeographicNameParser parserName;
    private final GeographicCodeParser parserCode;
    private final DatalessKeyAccess accessKeyGeo = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");





    /**
     * Initializes this <code>EpanCountReportBuilder</code>,
     * with the given <code>GeographicNameParser</code> that it
     * uses to look up the names of geographic areas when building
     * a report.
     * @param parserName
     * @param parserCode
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountReportBuilder(final GeographicNameParser parserName, final GeographicCodeParser parserCode) throws DatalessKeyAccessCreationException
    {
        this.parserName = parserName;
        this.parserCode = parserCode;
    }





    /**
     * Builds a report of the given <code>EpanCountRequest</code>
     * and appends it to the given <code>StringBuffer</code>. The
     * report is intended to be displayed to the user. The exact
     * format of the generated report is not documented, and may
     * change over time, so the report should not be parsed programmatically.
     * @param request
     * @param report
     */
    public void appendReport(final EpanCountRequest request, final StringBuffer report)
    {
        report.append("Client: ");
        report.append(request.getClientName());
        report.append('\n');

        report.append("Topic: ");
        report.append(request.getTopic());
        report.append('\n');

        final EpanCountCriteria criteria = request.getCriteria();

        report.append('\n');
        writeAge(criteria.getAge(),report);
        writeKids(criteria.getWithKids(),report);
        writeIncome(criteria.getIncome(),report);
        writeEthnicity(criteria.getEthnicity(),report);
        writeEducation(criteria.getEducation(),report);
        writeMarried(criteria.getMarried(),report);
        writeGender(criteria.getGender(),report);
        writeXdem(request.getXdemCriteria(),report);
        writeGeographyType(criteria.getUserGeo(),report);
        if (!isGeographyBrokenOut(criteria.getBreakOut()) || request.getQueryCount() == 0)
        {
            writeGeography(criteria.getGeography(),report);
        }
        report.append('\n');
        writeCounts(request,report);
    }










    protected void writeAge(final AgeType age, final StringBuffer report)
    {
        if (age == null)
        {
            return;
        }

        final BigInteger ageMin = age.getMin();
        final BigInteger ageMax = age.getMax();
        if (ageMin == null && ageMax == null)
        {
            return;
        }

        report.append("Age: ");

        if (ageMax == null)
        {
            report.append(ageMin.toString());
            report.append(" years old and older");
        }
        else if (ageMin == null)
        {
            report.append(ageMax.toString());
            report.append(" years old and younger");
        }
        else if (ageMin.equals(ageMax))
        {
            report.append(ageMin.toString());
            report.append(" years old");
        }
        else
        {
            report.append(ageMin.toString());
            report.append(" through ");
            report.append(ageMax.toString());
            report.append(" years old, inclusive");
        }
        report.append('\n');
    }

    protected void writeKids(final WithKidsType withKids, final StringBuffer report)
    {
        if (withKids == null)
        {
            return;
        }

        report.append("With");
        if (withKids.isNoKids())
        {
            report.append("out");
        }

        boolean specifiedMale = false;
        boolean specifiedFemale = false;

        final GenderType genderType = withKids.getGender();
        if (genderType != null)
        {
            final List rGender = genderType.getId();
            for (final Iterator iGender = rGender.iterator(); iGender.hasNext();)
            {
                final GenderEnumType gender = (GenderEnumType)iGender.next();
                if (gender.equals(GenderEnumType.MALE))
                {
                    specifiedMale = true;
                }
                else if (gender.equals(GenderEnumType.FEMALE))
                {
                    specifiedFemale = true;
                }
            }
        }

        if (specifiedMale && !specifiedFemale)
        {
            report.append(" Boys ");
        }
        else if (specifiedFemale && !specifiedMale)
        {
            report.append(" Girls ");
        }
        else
        {
            report.append(" Children ");
        }

        final AgeType age = withKids.getAge();
        if (age != null)
        {
            writeAge(age,report);
        }
        report.append('\n');
    }

    protected void writeIncome(final IncomeType income, final StringBuffer report)
    {
        if (income == null)
        {
            return;
        }

        report.append("Income: ");

        final SortedSet setRangeIncome = getIncomeSet(income);
        final SortedSet setJoined = DisjointRange.conjoinSet(setRangeIncome);
        appendIncomeRanges(setJoined,report);

        if (income.isPna())
        {
            report.append(" [including preferred not to answer]");
        }
        report.append('\n');
    }

    protected static SortedSet getIncomeSet(final IncomeType income)
    {
        final SortedSet setRangeIncome = new TreeSet();

        for (final Iterator iIncome = income.getId().iterator(); iIncome.hasNext();)
        {
            final IncomeEnumType inc = (IncomeEnumType)iIncome.next();

            setRangeIncome.add(EpanCountEnumNames.getRangeOfIncome(inc));
        }

        return setRangeIncome;
    }

    protected static void appendIncomeRanges(final Collection rRangeIncome, final StringBuffer report)
    {
        boolean have = false;
        for (final Iterator iRangeIncome = rRangeIncome.iterator(); iRangeIncome.hasNext();)
        {
            final DisjointRange rangeIncome = (DisjointRange)iRangeIncome.next();
            if (have)
            {
                report.append(", or ");
            }
            else
            {
                have = true;
            }
            appendIncomeRange(rangeIncome,report);
        }
    }

    protected static void appendIncomeRange(final DisjointRange rangeIncome, final StringBuffer report)
    {
        final int min = rangeIncome.getMin();
        final int lim = rangeIncome.getLimit();

        if (min == 0 && lim == Integer.MAX_VALUE)
        {
            report.append("any");
            return;
        }

        if (min < 0)
        {
            throw new IllegalStateException();
        }
        else if (min == 0)
        {
            report.append("under ");
        }
        else if (min < Integer.MAX_VALUE)
        {
            report.append("$");
            report.append(Integer.toString(min));
            report.append("K");
        }
        else
        {
            throw new IllegalStateException();
        }

        if (lim <= 0)
        {
            throw new IllegalStateException();
        }
        else if (lim < Integer.MAX_VALUE)
        {
            report.append(min == 0 ? "$" : "-");
            report.append(Integer.toString(lim));
            report.append("K");
        }
        else
        {
            report.append("+");
        }
    }

    protected static void writeEthnicity(final EthnicityType ethnicity, final StringBuffer report)
    {
        if (ethnicity == null)
        {
            return;
        }

        report.append("Ethnicity: ");

        boolean haveTerm = false;
        final List rEthnicity = ethnicity.getId();
        for (final Iterator iEthnicity = rEthnicity.iterator(); iEthnicity.hasNext();)
        {
            final EthnicityEnumType eth = (EthnicityEnumType)iEthnicity.next();
            if (haveTerm)
            {
                report.append(", or ");
            }
            else
            {
                haveTerm = true;
            }
            report.append(EpanCountEnumNames.ethnicityString(eth));
        }
        report.append('\n');
    }

    protected static void writeEducation(final EducationType education, final StringBuffer report)
    {
        if (education == null)
        {
            return;
        }

        report.append("Education: ");

        boolean haveTerm = false;
        final List rEducation = education.getId();
        for (final Iterator iEducation = rEducation.iterator(); iEducation.hasNext();)
        {
            final EducationEnumType edu = (EducationEnumType)iEducation.next();
            if (haveTerm)
            {
                report.append(", or ");
            }
            else
            {
                haveTerm = true;
            }
            report.append(EpanCountEnumNames.educationString(edu));
        }
        report.append('\n');
    }

    protected static void writeMarried(final MarriedType married, final StringBuffer report)
    {
        if (married == null)
        {
            return;
        }

        report.append("Married: ");

        boolean haveTerm = false;
        final List rMarried = married.getId();
        for (final Iterator iMarried = rMarried.iterator(); iMarried.hasNext();)
        {
            final MarriedEnumType mar = (MarriedEnumType)iMarried.next();
            if (haveTerm)
            {
                report.append(", or ");
            }
            else
            {
                haveTerm = true;
            }
            report.append(EpanCountEnumNames.marriedString(mar));
        }
        report.append('\n');
    }

    protected static void writeGender(final GenderType gender, final StringBuffer report)
    {
        if (gender == null)
        {
            return;
        }

        report.append("Gender: ");

        boolean specifiedMale = false;
        boolean specifiedFemale = false;
        final List rGender = gender.getId();
        for (final Iterator iGender = rGender.iterator(); iGender.hasNext();)
        {
            final GenderEnumType gen = (GenderEnumType)iGender.next();
            if (gen.equals(GenderEnumType.MALE))
            {
                specifiedMale = true;
            }
            else if (gen.equals(GenderEnumType.FEMALE))
            {
                specifiedFemale = true;
            }
        }

        if (specifiedMale && !specifiedFemale)
        {
            report.append("male");
        }
        else if (specifiedFemale && !specifiedMale)
        {
            report.append("female");
        }
        report.append('\n');
    }

    protected void writeGeographyType(final UserGeoType userGeo, final StringBuffer report)
    {
        if (userGeo == null)
        {
            return;
        }

        final GeographyEnumType geoType = userGeo.getGeoType();
        if (geoType == null)
        {
            return;
        }

        report.append("Geography: ");
        report.append(EpanCountEnumNames.geoTypeString(geoType));
        report.append('\n');
    }

    protected void writeGeography(final GeographyType geography, final StringBuffer report)
    {
        if (geography == null)
        {
            return;
        }

        final List rNumbr = new ArrayList(100);
        final List rCName = new ArrayList(100);

        int longestNumbr = 0;
        int iNumbr = 0;

        final List rID = geography.getId();
        for (final Iterator iID = rID.iterator(); iID.hasNext();)
        {
            final String sID = (String)iID.next();
            if (sID.length() > 0)
            {
                final String numbr = Integer.toString(++iNumbr);
                if (numbr.length() > longestNumbr)
                {
                    longestNumbr = numbr.length();
                }

                final DatalessKey keyGeo = this.accessKeyGeo.createKeyFromString(sID);
                rNumbr.add(numbr);
                rCName.add(lookupGeo(keyGeo));
            }
        }
        final int nCount = rNumbr.size();
        for (int iCount = 0; iCount < nCount; ++iCount)
        {
            final String numbr = (String)rNumbr.get(iCount);
            final String cname = (String)rCName.get(iCount);
            if (nCount > 1)
            {
                fill(numbr,longestNumbr,' ',report);
                report.append(numbr);
                report.append(". ");
            }
            report.append(cname);
            report.append('\n');
        }
    }

    private String lookupGeo(final DatalessKey keyGeo) throws IllegalStateException
    {
        try
        {
            return this.parserName.lookup(keyGeo);
        }
        catch (final IllegalStateException e)
        {
            // geography key not found in list of names
        }

        try
        {
            return this.parserCode.lookup(keyGeo);
        }
        catch (final IllegalStateException e)
        {
            // geography key not found in list of codes
        }

        return "[unknown geography]";
    }





    protected boolean isGeographyBrokenOut(final BreakOutType breakout)
    {
        if (breakout == null)
        {
            return false;
        }

        return breakout.isGeography();
    }

    protected void writeXdem(XdemCriteria xdemCriteria, final StringBuffer report)
    {
        if (xdemCriteria.isEmpty())
        {
            return;
        }

        report.append("Other Demographics: \n");
        xdemCriteria.appendTextReport(report);
        report.append("\n");
    }



    private static final int DELIM_EVERY_N_LINES = 5;
    private static final char DELIM = '-';
    private static final Format formatDate = new SimpleDateFormat("MMMMM d, yyyy, H:mm aaa, z");

    protected void writeCounts(final EpanCountRequest request, final StringBuffer report)
    {
        if (request.getQueryCount() == 0)
        {
            return;
        }

        final List rNumbr = new ArrayList(100);
        final List rCName = new ArrayList(100);
        final List rCount = new ArrayList(100);
        final List rdAsOf = new ArrayList(100);

        int longestNumbr = 0;
        int longestCName = 0;
        int longestCount = 0;
        int iNumbr = 0;
        Date dateAsOfFirst = null;
        boolean multipleDates = false;
        for (final Iterator iCount = request.iterator(); iCount.hasNext();)
        {
            final EpanCount epanCount = (EpanCount)iCount.next();

            final String numbr = Integer.toString(++iNumbr);

            final String cname = epanCount.getName().trim();

            String count;
            if (epanCount.completedSuccessfully())
            {
                count = Long.toString(epanCount.getCount());
            }
            else
            {
                count = "(count was not run)";
            }

            final Date dAsOf = epanCount.getTimeAsOf();
            if (dAsOf != null)
            {
                if (dateAsOfFirst == null)
                {
                    dateAsOfFirst = dAsOf;
                }
                else if (!dAsOf.equals(dateAsOfFirst))
                {
                    multipleDates = true;
                }
            }



            if (numbr.length() > longestNumbr)
            {
                longestNumbr = numbr.length();
            }
            if (cname.length() > longestCName)
            {
                longestCName = cname.length();
            }
            if (count.length() > longestCount)
            {
                longestCount = count.length();
            }

            rNumbr.add(numbr);
            rCName.add(cname);
            rCount.add(count);
            rdAsOf.add(dAsOf);
        }

        final int nCount = rNumbr.size();

        if (!multipleDates && dateAsOfFirst != null)
        {
            report.append("Count");
            if (nCount > 1)
            {
                report.append("s");
            }
            report.append(" as of: ");
            report.append(formatDate.format(dateAsOfFirst));
            report.append("\n");
        }
        if (nCount > 1)
        {
            report.append("(To view the counts correctly, use a fixed-width font, such as Courier.)");
        }
        report.append("\n");

        for (int iCount = 0; iCount < nCount; ++iCount)
        {
            final String numbr = (String)rNumbr.get(iCount);
            final String cname = (String)rCName.get(iCount);
            final String count = (String)rCount.get(iCount);
            final Date dAsOf = (Date)rdAsOf.get(iCount);

            writeOneCount(iCount,nCount,numbr,longestNumbr,cname,longestCName,count,longestCount,dAsOf,multipleDates,report);

            report.append('\n');
        }
        if (nCount > 1)
        {
            writeCountDelim(0,longestNumbr,longestCName,longestCount,report);
        }
    }

    private void writeOneCount(int iCount, final int nCount, final String numbr, int longestNumbr, final String cname, int longestCName, final String count, int longestCount, final Date dAsOf, final boolean multipleDates, final StringBuffer report)
    {
        if (nCount > 1)
        {
            writeCountDelim(iCount,longestNumbr,longestCName,longestCount,report);
            fill(numbr,longestNumbr,' ',report);
            report.append(numbr);
            report.append(". ");
        }

        report.append(cname);
        if (longestCount == 0)
        {
            return;
        }

        fill(cname,longestCName,' ',report);

        if (longestCName > 0)
        {
            fill("",4,' ',report);
        }
        fill(count,longestCount,' ',report);
        report.append(count);

        if (multipleDates && dAsOf != null)
        {
            report.append(" (as of ");
            report.append(formatDate.format(dAsOf));
            report.append(")");
        }
    }





    private static void writeCountDelim(int iCount, int longestNumbr, int longestCName, int longestCount, final StringBuffer report)
    {
        if (DELIM_EVERY_N_LINES <= 0 || iCount%DELIM_EVERY_N_LINES != 0)
        {
            return;
        }

        fill("",longestNumbr+2+longestCName+4+longestCount,DELIM,report);
        report.append("\n");
    }





    private static void fill(final String sValue, final int cFieldWidth, final char filler, final StringBuffer appendTo)
    {
        final int nFiller = cFieldWidth-sValue.length();
        if (nFiller <= 0)
        {
            return;
        }
        final char[] rFiller = new char[nFiller];
        Arrays.fill(rFiller,filler);
        appendTo.append(rFiller);
    }
}
