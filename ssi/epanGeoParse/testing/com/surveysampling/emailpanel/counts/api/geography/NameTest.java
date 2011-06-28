/*
 * Created on May 16, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.emailpanel.counts.api.geography.internal.ResolverCache;
import com.surveysampling.emailpanel.counts.api.geography.internal.resolver.Resolver;
import com.surveysampling.util.key.DatalessKey;
import com.surveysampling.util.key.DatalessKeyAccess;
import com.surveysampling.util.key.DatalessKeyAccessFactory;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;


/**
 * Pulls (city) names from DMAs or MSAs and makes sure
 * each one fuzzy-matches the correct area, or at least an
 * area containing the city name.
 * 
 * @author Chris Mosher
 */
public class NameTest
{
    /**
     * @param args
     * @throws DatalessKeyAccessCreationException
     * @throws SQLException
     */
    public static void main(String[] args) throws DatalessKeyAccessCreationException, SQLException
    {
        System.setProperty("jdbc.drivers","oracle.jdbc.driver.OracleDriver");
        final Connection db = DriverManager.getConnection("jdbc:oracle:thin:@dev1.surveysampling.com:1521:DEV","chrism","majic4u");

        ResolverCache r = new ResolverCache();
        r.fill(db);

        final DatalessKeyAccess factKey = DatalessKeyAccessFactory.createDatalessKeyAccess("Long");
        final Resolver resolver = r.getNameResolver(GeographyEnumType.DMA);

        final String sql = "SELECT geoid,name from geographicname where geotypeid= 3 order by name";
        final Statement st = db.createStatement();
        final ResultSet rs = st.executeQuery(sql);
        while (rs.next())
        {
            final DatalessKey geoKey = factKey.createKeyFromResultSet(rs, 1);
            final String geoName = rs.getString(2);
            if (geoName.startsWith("(NON-"))
            {
                // who cares about these
                continue;
            }

            final List rMatch = new ArrayList();
            resolver.resolve(geoName,rMatch,5);
            if (rMatch.size() == 0)
            {
                System.out.print(factKey.keyAsString(geoKey));
                System.out.print("\t");
                System.out.print(geoName);
                System.out.print("\t");
                System.out.println("FULL NAME HAD NO MATCHES!");
                continue;
            }
            final DatalessKey keyFullMatch = ((GeographicArea)rMatch.get(0)).getKey();
            if (rMatch.size() > 1)
            {
                System.out.print(factKey.keyAsString(geoKey));
                System.out.print("\t");
                System.out.print(geoName);
                System.out.print("\t");
                System.out.print("FULL NAME HAD MORE THAN ONE MATCH!");
                System.out.print("\t");
                System.out.print("top match: "+r.lookupName(keyFullMatch));
                System.out.print("\t");
                System.out.println("should be: "+geoName);
                continue;
            }
            if (!keyFullMatch.equals(geoKey))
            {
                System.out.print(factKey.keyAsString(geoKey));
                System.out.print("\t");
                System.out.print(geoName);
                System.out.print("\t");
                System.out.print("FULL NAME HAD AN INCORRECT MATCH!");
                System.out.print("\t");
                System.out.print("top match: "+r.lookupName(keyFullMatch));
                System.out.print("\t");
                System.out.println("should be: "+geoName);
                continue;
            }

            final List rCity = parseCities(geoName);
            for (final Iterator iCity = rCity.iterator(); iCity.hasNext();)
            {
                final String city = (String)iCity.next();

                rMatch.clear();
                resolver.resolve(city,rMatch,5);

                if (rMatch.size() == 0)
                {
                    System.out.print(factKey.keyAsString(geoKey));
                    System.out.print("\t");
                    System.out.print(city);
                    System.out.print("\t");
                    System.out.println("NO MATCHES");
                    continue;
                }
                final DatalessKey keyMatch = ((GeographicArea)rMatch.get(0)).getKey();
                if (keyMatch.equals(geoKey))
                {
                    continue;
                }
                if (rMatch.size() == 1)
                {
                    if (r.lookupName(keyMatch).indexOf(city) < 0)
                    {
                        System.out.print(factKey.keyAsString(geoKey));
                        System.out.print("\t");
                        System.out.print(city);
                        System.out.print("\t");
                        System.out.print("ONE MATCH, AND IT WAS WRONG");
                        System.out.print("\t");
                        System.out.print("top match: "+r.lookupName(keyMatch));
                        System.out.print("\t");
                        System.out.println("should be: "+geoName);
                    }
                    continue;
                }
                final DatalessKey keyMatch2 = ((GeographicArea)rMatch.get(1)).getKey();
                if (keyMatch2.equals(geoKey))
                {
                    if (r.lookupName(keyMatch).indexOf(city) < 0)
                    {
                        System.out.print(factKey.keyAsString(geoKey));
                        System.out.print("\t");
                        System.out.print(city);
                        System.out.print("\t");
                        System.out.print("2nd match (not bad)");
                        System.out.print("\t");
                        System.out.print("top match: "+r.lookupName(keyMatch));
                        System.out.print("\t");
                        System.out.println("should be: "+geoName);
                    }
                    continue;
                }
                // if top match contains city as sub-string, that's understandable
                if (r.lookupName(keyMatch).indexOf(city) < 0)
                {
                    System.out.print(factKey.keyAsString(geoKey));
                    System.out.print("\t");
                    System.out.print(city);
                    System.out.print("\t");
                    System.out.print("NOT IN TOP 2 MATCHES");
                    System.out.print("\t");
                    System.out.print("top match: "+r.lookupName(keyMatch));
                    System.out.print("\t");
                    System.out.println("should be: "+geoName);
                }
                continue;
            }
        }

//        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
//        for (String s = in.readLine(); s != null; s = in.readLine())
//        {
//            if (s.equalsIgnoreCase("exit"))
//            {
//                break;
//            }
//
//            final List rMatch = new ArrayList();
//            resolver.resolve(s,5,rMatch,4);
//            if (rMatch.isEmpty())
//            {
//                System.err.println(s+"\tNO MATCH FOUND");
//            }
//            printMatches(s,rMatch,"",r);
//        }
//        in.close();
    }

//    /**
//     * @param r
//     * @param rMatch
//     * @throws ClassCastException
//     */
//    private static void printMatches(final String input, final List rMatch, final String nameFull, final ResolverCache r) throws ClassCastException
//    {
//        for (final Iterator iMatch = rMatch.iterator(); iMatch.hasNext();)
//        {
//            DatalessKey key = (DatalessKey)iMatch.next();
//            System.err.print(input);
//            System.err.print("\t");
//            System.err.print(r.lookupName(key));
//            System.err.print("\t");
//            System.err.print(nameFull);
//            System.err.println();
//        }
//    }

    /**
     * @param geoName
     * @return list of cities
     */
    private static List parseCities(String geoName)
    {
        final List r = new ArrayList();
        final StringTokenizer stComma = new StringTokenizer(geoName,",");
        if (!stComma.hasMoreTokens())
        {
            return r;
        }

        final String preComma = stComma.nextToken();
        final StringTokenizer st = new StringTokenizer(preComma,"-");
        while (st.hasMoreTokens())
        {
            r.add(st.nextToken());
        }

        return r;
    }
}
