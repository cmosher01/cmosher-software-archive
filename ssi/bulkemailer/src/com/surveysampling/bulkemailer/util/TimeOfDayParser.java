/*
 * Created on Jun 10, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.ParseException;

/**
 * <p>
 * Parses a string that represents a time of day.
 * </p>
 * <p>
 * Objects of this class are immutable and thread-safe.
 * </p>
 * @author Chris Mosher
 */
public class TimeOfDayParser
{
    private final int hour;
    private final int minute;
    private final int second;
    private final int millisecond;

    /**
     * Initializes a TimeOfDayParser by parsing the
     * given string. The following formats are allowed:
     * <ul>
     * <li><code>H:mm</code></li>
     * <li><code>HH:mm</code></li>
     * <li><code>HH:mm:ss</code></li>
     * <li><code>HH:mm:ss.SSS</code></li>
     * </ul>
     * Other formats cause a <code>ParseException</code>
     * to be thrown.
     * No range checking is performed on the numbers.
     * 
     * @param s the String to be parsed.
     * @throws ParseException if the given string is invalid
     */
    public TimeOfDayParser(String s) throws ParseException
    {
        if (s.length() == 4)
        {
            s = "0"+s;
        }

        int hr, mn, sc, ms;
        if (s.length() == 5)
        {
            ensure(s,2,':');
            hr = getd(s,0,2);
            mn = getd(s,3,2);
            sc = 0;
            ms = 0;
        }
        else if (s.length() == 8)
        {
            ensure(s,2,':');
            ensure(s,5,':');
            hr = getd(s,0,2);
            mn = getd(s,3,2);
            sc = getd(s,6,2);
            ms = 0;
        }
        else if (s.length() == 12)
        {
            ensure(s,2,':');
            ensure(s,5,':');
            ensure(s,8,'.');
            hr = getd(s,0,2);
            mn = getd(s,3,2);
            sc = getd(s,6,2);
            ms = getd(s,9,3);
        }
        else
        {
            throw new ParseException("Invalid time-of-day string",0);
        }

        this.hour = hr;
        this.minute = mn;
        this.second = sc;
        this.millisecond = ms;
    }



    /**
     * Gets the hour component of this time of day.
     * @return hour component
     */
    public int getHour()
    {
        return hour;
    }

    /**
     * Gets the minute component of this time of day.
     * @return minute component
     */
    public int getMinute()
    {
        return minute;
    }

    /**
     * Gets the second component of this time of day.
     * @return second component
     */
    public int getSecond()
    {
        return second;
    }

    /**
     * Gets the millisecond component of this time of day.
     * @return millisecond component
     */
    public int getMillisecond()
    {
        return millisecond;
    }



    /**
     * Parses the given String s, from position i
     * for length j, as an integer.
     * @param s String to be parsed
     * @param i position within s to start parsing
     * @param j length within s to be parsed
     * @return the resulting integer
     * @throws ParseException if the substring to be parsed
     * doesn't contain a valid integer.
     */
    private int getd(String s, int i, int j) throws ParseException
    {
        int r = 0;
        while (j-- > 0)
        {
            char c = s.charAt(i++);
            int x = Character.digit(c,10);
            if (x < 0)
            {
                throw new ParseException("Invalid time-of-day string",i-1);
            }
            r *= 10;
            r += x;
        }
        return r;
    }

    /**
     * Checks the character at the given position within
     * the given string to see if it matches a given character;
     * throws a <code>ParseException</code> if it doesn't match.
     * @param s the String to be checked
     * @param i position within s
     * @param c expected character
     * @throws ParseException if the character at position i
     * within String s is not c.
     */
    private void ensure(String s, int i, char c) throws ParseException
    {
        if (s.charAt(i) != c)
        {
            throw new ParseException("Invalid time-of-day string",i);
        }
    }
}
