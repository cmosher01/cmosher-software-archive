/*
 * Created on June 17, 2005
 */
package com.surveysampling.emailpanel.counts.api.internal.sql;

import java.util.StringTokenizer;

import com.surveysampling.emailpanel.shared.DatabaseURLs;

/**
 * Parses a database username and determines
 * the level (ALPHA, BETA, or RELEASE) based on
 * that username.
 * Usernames can be of the form:
 * 
 * 1. LEVEL_USERNAME
 * 
 * 2. USERNAME,LEVEL or LEVEL,USERNAME
 * 
 * 3. anything else (where RELEASE level is assumed)
 * 
 * where LEVEL is ALPHA, BETA, or RELEASE.
 * 
 * Not thread-safe.
 * 
 * @author Chris Mosher
 */
public class UsernameParser
{
    private final String username;

    private String actualUsername;
    private int level;

    /**
     * Initialized the parser to parse the given username.
     * The username will be up-cased and trimmed.
     * @param username
     */
    public UsernameParser(final String username)
    {
        this.username = username.toUpperCase().trim();
    }

    /**
     * Parses the username. Must be called before
     * calling getUsername or getLevel.
     */
    public void parse()
    {
        if (isParsed())
        {
            return;
        }

        if (this.username.indexOf(',') >= 0)
        {
            parseNameWithComma();
        }
        else
        {
            parseNameWithUnderscore();
        }
    }

    private void parseNameWithComma()
    {
        // username is of the form:
        // "level-name,user-name" or "user-name,level-name"
        final StringTokenizer st = new StringTokenizer(this.username,", ");
        final String field1 = st.nextToken();
        final String field2 = st.nextToken();

        if (field1.equals("ALPHA") || field1.equals("BETA") || field1.equals("RELEASE"))
        {
            this.level = DatabaseURLs.getLevel(field1);
            this.actualUsername = field2;
        }
        else if (field2.equals("ALPHA") || field2.equals("BETA") || field2.equals("RELEASE"))
        {
            this.level = DatabaseURLs.getLevel(field2);
            this.actualUsername = field1;
        }
        else
        {
            // This probably shouldn't ever happen.
            // But in case it does, use RELEASE and
            // remove all commas
            this.level = DatabaseURLs.RELEASE;
            this.actualUsername = this.username.replaceAll(",","");
        }
    }

    private void parseNameWithUnderscore()
    {
        if (this.username.startsWith("ALPHA_"))
        {
            this.level = DatabaseURLs.ALPHA;
        }
        else if (this.username.startsWith("BETA_"))
        {
            this.level = DatabaseURLs.BETA;
        }
        else
        {
            this.level = DatabaseURLs.RELEASE;
        }
        this.actualUsername = this.username;
    }

    /**
     * Gets the actual username to log in with
     * @return the username
     */
    public String getUsername()
    {
        if (!isParsed())
        {
            throw new IllegalStateException();
        }
        return this.actualUsername;
    }

    /**
     * The level:
     * <code>DatabaseURLs.ALPHA</code>,
     * <code>DatabaseURLs.BETA</code>, or
     * <code>DatabaseURLs.RELEASE</code>.
     * @return level
     */
    public int getLevel()
    {
        if (!isParsed())
        {
            throw new IllegalStateException();
        }
        return this.level;
    }

    /**
     * Checks if the username has been parsed.
     * @return <code>true</code> if <code>parse</code> has been called
     */
    public boolean isParsed()
    {
        return this.actualUsername != null;
    }
}
