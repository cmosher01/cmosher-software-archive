/*
 * Created on May 20, 2005
 */
package com.surveysampling.emailpanel.counts.api.geography;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.surveysampling.emailpanel.counts.api.criteria.GeographyEnumType;
import com.surveysampling.util.key.exception.DatalessKeyAccessCreationException;

/**
 * Main program to test geographic name parsing.
 * Reads a file (given a file-spec as the command
 * line argument) of county names/states and fuzzy
 * matches them. Results are written to standard out.
 * 
 * @author Chris Mosher
 */
public class GeoParseTest
{
    /**
     * @param args
     * @throws SQLException
     * @throws DatalessKeyAccessCreationException
     * @throws IOException
     */
    public static void main(String[] args) throws SQLException, DatalessKeyAccessCreationException, IOException
    {
        if (args.length < 1)
        {
            throw new IllegalArgumentException();
        }
        File file = new File(args[0]);
        file = file.getCanonicalFile();
        file = file.getAbsoluteFile();
        test(new InputStreamReader(new FileInputStream(file)));
    }

    private static void test(final Reader countyNames) throws SQLException, DatalessKeyAccessCreationException, IOException
    {
        System.setProperty("jdbc.drivers","oracle.jdbc.driver.OracleDriver");
        final Connection db = DriverManager.getConnection("jdbc:oracle:thin:@dev1.surveysampling.com:1521:DEV","chrism","majic4u");

        final GeographicNameParser parser = new GeographicNameParser(db);

        db.close();



        final List rGeoToSearchFor = new ArrayList();
        final BufferedReader in = new BufferedReader(countyNames);
        for (String sLine = in.readLine(); sLine != null; sLine = in.readLine())
        {
            sLine = sLine.trim();
            if (sLine.length() == 0)
            {
                continue;
            }

            rGeoToSearchFor.add(sLine);
        }
        in.close();

        final List rMatch = new ArrayList();
        parser.parseGeo(rGeoToSearchFor,GeographyEnumType.COUNTY,rMatch,5,null);

        dumpMatches(rGeoToSearchFor,rMatch);
    }

    private static void dumpMatches(final List rGeoToSearchFor, final List rMatchLists)
    {
        final Iterator iMatch = rMatchLists.iterator();
        for (final Iterator iSearch = rGeoToSearchFor.iterator(); iSearch.hasNext();)
        {
            String search = (String)iSearch.next();
            List rMatch = (List)iMatch.next();
            System.out.println(search);
            if (rMatch.isEmpty())
            {
                System.out.println("    no matches found");
            }
            for (final Iterator im = rMatch.iterator(); im.hasNext();)
            {
                final GeographicArea match = (GeographicArea)im.next();
                System.out.println("    "+match.toString());
            }
        }
    }
}
