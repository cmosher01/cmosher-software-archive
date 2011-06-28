/*
 * Created on June 6, 2005
 */
package com.surveysampling.emailpanel.counts.api.request.internal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.criteria.AgeType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EducationType;
import com.surveysampling.emailpanel.counts.api.criteria.EpanCountCriteria;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.EthnicityType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GenderType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.GeographyType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.IncomeType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedEnumType;
import com.surveysampling.emailpanel.counts.api.criteria.MarriedType;
import com.surveysampling.emailpanel.counts.api.criteria.UserGeoType;
import com.surveysampling.emailpanel.counts.api.criteria.WithKidsType;
import com.surveysampling.emailpanel.counts.api.internal.sql.SQLConditionBuilder;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Builds a SQL string to do the count query for
 * a given criteria.
 * 
 * @author Chris Mosher
 */
public class EpanCountSQLBuilder
{
    private static final int MEMBER_AGE_MIN = 2;
    private static final int MEMBER_AGE_MAX = 17;

    private final EpanCountCriteria criteria;
    private final DatalessKeyAccess keyAccessGeoID = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
    private final DatalessKeyAccess keyAccess  = DatalessKeyAccessFactory.createDatalessKeyAccess("UUID");
    private final NonContinentalGeoID nContGeoID;



    /**
     * Initializes a new builder, that reads from the given criteria.
     * @param criteria
     * @throws DatalessKeyAccessCreationException
     */
    public EpanCountSQLBuilder(final EpanCountCriteria criteria, final NonContinentalGeoID ncgid) throws DatalessKeyAccessCreationException
    {
        this.criteria = criteria;
        this.nContGeoID = ncgid;
    }

    /**
     * Build the SQL query for the given count (out of the full set of
     * counts given in the constructor), and append it to
     * the given <code>StringBuffer</code>.
     * @param gender if breaking out by gender, or <code>null</code> if not
     * @param keyGeo primary key of geographic area for this query,
     * if breaking out by geography (<code>null</code>, otherwise).
     * @param sql <code>StringBuffer</code> to append the <code>SELECT</code> statement to
     */
    public void appendQuerySQL(final GenderEnumType gender, final DatalessKey keyGeo, final StringBuffer sql)
    {
        sql.append("SELECT \n");
        sql.append("    COUNT(*) c \n");
        sql.append("FROM \n");
        sql.append("    PanelistSummary P \n");
        sql.append("WHERE \n");

        final SQLConditionBuilder where = new SQLConditionBuilder(sql,"AND");

        where.appendTerm(whereAge());
        where.appendTerm(whereIncome());
        where.appendTerm(whereMarried());
        where.appendTerm(whereEducation());
        where.appendTerm(whereEthnicity());
        where.appendTerm(whereKids());

        if (gender == null)
        {
            where.appendTerm(whereGender());
        }
        else
        {
            final StringBuffer s = new StringBuffer();
            s.append("genderID = ");
            s.append(gender.getValue());
            where.appendTerm(s);
        }

        final UserGeoType userGeo = this.criteria.getUserGeo();
        final GeographyEnumType geoType = userGeo.getGeoType();
        if (keyGeo == null || geoType.equals(GeographyEnumType.CONTINENTAL) || geoType.equals(GeographyEnumType.USA))
        {
            where.appendTerm(whereGeography());
        }
        else
        {
            final StringBuffer s = new StringBuffer();
            appendGeographyColumn(geoType,s);
            s.append(" = ");
            DatalessKeyAccessFactory.getDatalessKeyAccess(keyGeo).putKeyAsSQLNumber(keyGeo,s);
            where.appendTerm(s);
        }
    }

    protected StringBuffer whereGeography()
    {
        final StringBuffer sql = new StringBuffer(1024);

        final UserGeoType userGeo = this.criteria.getUserGeo();
        if (userGeo == null)
        {
            return sql;
        }

        final GeographyEnumType geoType = userGeo.getGeoType();
        if (geoType.equals(GeographyEnumType.USA))
        {
            sql.append("NULL IS NULL"); // we only have USA, so use tautology
            return sql;
        }

        if (geoType.equals(GeographyEnumType.CONTINENTAL))
        {
            // this means exclude Alaska and Hawaii
            sql.append("state NOT IN (");

            //get the list of non continental geoID's
            //iterate through the list
            //append each geoID, comma-separated, to the query

            StringBuffer buf = new StringBuffer();
            boolean first = true;
            for (Integer id:nContGeoID)
            {
                if (!first)
                {
                    buf.append(",");
                }
                else
                    first = false;
                buf.append(""+id);
            }
            sql.append(buf.toString());
            
            sql.append(")");

            return sql;
        }

        final GeographyType geography = this.criteria.getGeography();
        if (geography == null)
        {
            return sql;
        }

        // remove blank geography IDs
        // (if a user-entered geography doesn't have a match, we want to skip it)
        final List rIDorEmpty = geography.getId();
        final List rID = new ArrayList(rIDorEmpty.size());
        for (final Iterator iIDorEmpty = rIDorEmpty.iterator(); iIDorEmpty.hasNext();)
        {
            final String idOrEmpty = (String)iIDorEmpty.next();
            if (idOrEmpty.length() > 0)
            {
                final String id = idOrEmpty;
                rID.add(id);
            }
        }

        int iGeo = 0;
        int iGeoLim = Math.min(iGeo+1000,rID.size());
        do
        {
            if (iGeo > 0)
            {
                sql.append(" OR ");
            }
            whereGeo1000(sql,geoType,rID.subList(iGeo,iGeoLim));
            iGeo += 1000;
            iGeoLim = Math.min(iGeo+1000,rID.size());
        }
        while (iGeo < iGeoLim);

        return sql;
    }

    private void whereGeo1000(final StringBuffer sql, final GeographyEnumType geoType, final List rID)
    {
        appendGeographyColumn(geoType,sql);
        sql.append(" IN (");

        if (rID.size() == 0)
        {
            // strange case (happens when they forget to enter any geography codes/names
            sql.append("NULL"); // causes zero rows to be returned
        }

        boolean haveTerm = false;
        int cTerm = 0;
        for (final Iterator iID = rID.iterator(); iID.hasNext();)
        {
            final String id = (String)iID.next();
            if (haveTerm)
            {
                sql.append(",");
            }
            else
            {
                haveTerm = true;
            }
            this.keyAccessGeoID.putKeyAsSQLNumber(this.keyAccessGeoID.createKeyFromString(id),sql);
            if (++cTerm % 20 == 0 && iID.hasNext())
            {
                sql.append("\n");
            }
        }

        sql.append(")");
    }

    protected StringBuffer whereGender()
    {
        final StringBuffer sql = new StringBuffer(32);

        final GenderType gender = this.criteria.getGender();
        if (gender == null)
        {
            return sql;
        }

        final List rID = gender.getId();
        if (rID.size() == 0)
        {
            return sql;
        }

        sql.append("genderID IN (");
        boolean haveTerm = false;
        for (final Iterator iID = rID.iterator(); iID.hasNext();)
        {
            final GenderEnumType id = (GenderEnumType)iID.next();
            if (haveTerm)
            {
                sql.append(",");
            }
            else
            {
                haveTerm = true;
            }
            sql.append(id.getValue());
        }
        sql.append(")");

        return sql;
    }

    protected StringBuffer whereKids()
    {
        final StringBuffer sql = new StringBuffer(32);

        final WithKidsType withKids = this.criteria.getWithKids();
        if (withKids == null)
        {
            return sql;
        }

        final GenderType gender = withKids.getGender();
        boolean boys = true;
        boolean girls = true;
        if (gender != null)
        {
            final List id = gender.getId();
            boys = false;
            girls = false;
            for (final Iterator rID = id.iterator(); rID.hasNext();)
            {
                final GenderEnumType gen = (GenderEnumType)rID.next();
                if (gen.equals(GenderEnumType.MALE))
                {
                    boys = true;
                }
                else if (gen.equals(GenderEnumType.FEMALE))
                {
                    girls = true;
                }
                else
                {
                    throw new IllegalStateException();
                }
            }
        }

        final AgeType age = withKids.getAge();
        int ageMin = MEMBER_AGE_MIN;
        int ageMax = MEMBER_AGE_MAX;
        if (age != null)
        {
            {
                final BigInteger min = age.getMin();
                if (min != null)
                {
                    ageMin = min.intValue();
                }
            }
            {
                final BigInteger max = age.getMax();
                if (max != null)
                {
                    ageMax = max.intValue();
                }
            }
        }

        boolean without = withKids.isNoKids();

        boolean haveTerm = false;
        for (int iAge = ageMin; iAge <= ageMax; ++iAge)
        {
            if (boys)
            {
                memberAgeTerm("M",iAge,without,haveTerm,sql);
                haveTerm = true;
            }
            if (girls)
            {
                memberAgeTerm("F",iAge,without,haveTerm,sql);
                haveTerm = true;
            }
        }

        return sql;
    }

    private void memberAgeTerm(final String gender, final int iAge, final boolean without, final boolean haveTerm, final StringBuffer sql)
    {
        if (haveTerm)
        {
            sql.append(without ? " AND " : " OR ");
        }

        sql.append("hasMember");
        sql.append(gender);
        sql.append(ageString(iAge));

        sql.append(" = \'");
        sql.append(without ? 'F' : 'T');
        sql.append("\'");
    }

    protected static String ageString(final int age)
    {
        if (0 <= age && age < 10)
        {
            return "0"+age;
        }
        else if (10 <= age && age < 100)
        {
            return ""+age;
        }
        throw new UnsupportedOperationException();
    }

    protected StringBuffer whereAge()
    {
        final StringBuffer sql = new StringBuffer(32);

        final AgeType age = this.criteria.getAge();
        if (age == null)
        {
            return sql;
        }

        final BigInteger min = age.getMin();
        final BigInteger max = age.getMax();
        if (min != null && max != null)
        {
            sql.append("ageInYears BETWEEN ");
            sql.append(min);
            sql.append(" AND ");
            sql.append(max);
        }
        else if (min != null)
        {
            sql.append("ageInYears >= ");
            sql.append(min);
        }
        else if (max != null)
        {
            sql.append("ageInYears <= ");
            sql.append(max);
        }

        return sql;
    }

    protected StringBuffer whereIncome()
    {
        final StringBuffer sql = new StringBuffer(32);

        final IncomeType income = this.criteria.getIncome();
        if (income == null)
        {
            return sql;
        }

        boolean hasIDs = income.getId().size() > 0;
        boolean hasPNA = income.isPna();

        if (hasIDs)
        {
            sql.append("incomeRangeID IN (");
            boolean haveTerm = false;
            for (final Iterator iID = income.getId().iterator(); iID.hasNext();)
            {
                final IncomeEnumType id = (IncomeEnumType)iID.next();
                if (haveTerm)
                {
                    sql.append(",");
                }
                else
                {
                    haveTerm = true;
                }
                sql.append(id.getValue());
            }
            sql.append(")");
        }

        if (hasIDs && hasPNA)
        {
            sql.append(" OR ");
        }

        if (hasPNA)
        {
            sql.append("incomeRangeID IS NULL");
        }

        return sql;
    }

    protected StringBuffer whereMarried()
    {
        final StringBuffer sql = new StringBuffer(32);

        final MarriedType married = this.criteria.getMarried();
        if (married == null)
        {
            return sql;
        }

        final List rID = married.getId();
        if (rID.size() == 0)
        {
            return sql;
        }

        sql.append("marriedID IN (");
        boolean haveTerm = false;
        for (final Iterator iID = rID.iterator(); iID.hasNext();)
        {
            final MarriedEnumType id = (MarriedEnumType)iID.next();
            if (haveTerm)
            {
                sql.append(",");
            }
            else
            {
                haveTerm = true;
            }
            sql.append(id.getValue());
        }
        sql.append(")");

        return sql;
    }

    protected StringBuffer whereEducation()
    {
        final StringBuffer sql = new StringBuffer(32);

        final EducationType education = this.criteria.getEducation();
        if (education == null)
        {
            return sql;
        }

        final List rID = education.getId();
        if (rID.size() == 0)
        {
            return sql;
        }

        sql.append("educLevelID IN (");
        boolean haveTerm = false;
        for (final Iterator iID = rID.iterator(); iID.hasNext();)
        {
            final EducationEnumType id = (EducationEnumType)iID.next();
            if (haveTerm)
            {
                sql.append(",");
            }
            else
            {
                haveTerm = true;
            }
            sql.append(id.getValue());
        }
        sql.append(")");

        return sql;
    }

    protected StringBuffer whereEthnicity()
    {
        final StringBuffer sql = new StringBuffer(32);

        final EthnicityType ethnicity = this.criteria.getEthnicity();
        if (ethnicity == null)
        {
            return sql;
        }

        final List rID = ethnicity.getId();
        if (rID.size() == 0)
        {
            return sql;
        }

        boolean haveTerm = false;
        for (final Iterator iEth = ethnicity.getId().iterator(); iEth.hasNext();)
        {
            final EthnicityEnumType eth = (EthnicityEnumType)iEth.next();
            if (haveTerm)
            {
                sql.append(" OR ");
            }
            else
            {
                haveTerm = true;
            }
            appendEthnicColumn(eth,sql);
            sql.append(" = \'T\'");
        }

        return sql;
    }

    protected static void appendEthnicColumn(final EthnicityEnumType eth, final StringBuffer appendTo)
    {
        if (eth.equals(EthnicityEnumType.WHITE))
        {
            appendTo.append("bWhite");
        }
        else if (eth.equals(EthnicityEnumType.BLACK))
        {
            appendTo.append("bBlack");
        }
        else if (eth.equals(EthnicityEnumType.HISPANIC))
        {
            appendTo.append("bHispanic");
        }
        else if (eth.equals(EthnicityEnumType.ASIAN))
        {
            appendTo.append("bAsian");
        }
        else if (eth.equals(EthnicityEnumType.INDIAN))
        {
            appendTo.append("bIndian");
        }
        else if (eth.equals(EthnicityEnumType.PACIFIC))
        {
            appendTo.append("bPacific");
        }
        else if (eth.equals(EthnicityEnumType.OTHER))
        {
            appendTo.append("bOtherEthnicity");
        }
        else
        {
            throw new IllegalStateException();
        }
    }

    protected static void appendGeographyColumn(final GeographyEnumType geo, final StringBuffer appendTo)
    {
        if (geo.equals(GeographyEnumType.STATE))
        {
            appendTo.append("state");
        }
        else if (geo.equals(GeographyEnumType.COUNTY) || geo.equals(GeographyEnumType.FIPS))
        {
            appendTo.append("county");
        }
        else if (geo.equals(GeographyEnumType.DMA))
        {
            appendTo.append("dma");
        }
        else if (geo.equals(GeographyEnumType.MSA))
        {
            appendTo.append("msa");
        }
        else if (geo.equals(GeographyEnumType.ZIP))
        {
            appendTo.append("zip");
        }
        else
        {
            throw new IllegalStateException();
        }
    }
}
