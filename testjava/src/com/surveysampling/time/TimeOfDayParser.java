/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.text.ParseException;

/**
 * TODO
 */
public class TimeOfDayParser
{
    private final int hour;
    private final int minute;
    private final int second;
    private final int millisecond;

    /**
     * Allowable formats:
     * 
     * H:mm
     * HH:mm
     * HH:mm:ss
     * HH:mm:ss.SSS
     * 
     * Numbers are assumed to be decimal.
     * 
     * @param cal
     * @param s
     * @return
     * @throws ParseException
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
        }
        else if (s.length() == 8)
        {
            ensure(s,2,':');
            ensure(s,5,':');
        }
        else if (s.length() == 12)
        {
            ensure(s,2,':');
            ensure(s,5,':');
            ensure(s,8,'.');
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
     * @param s
     * @param i
     * @param j
     * @return
     */
    private int getd(String s, int i, int j)
    {
        assert (j < 10000); // sanity
        assert (i+j < s.length());

        int r = 0;
        while (j-- > 0)
        {
            char c = s.charAt(i++);
            r *= 10;
            r += Character.digit(c,10);
        }
        return r;
    }

    /**
     * @param s
     * @param i
     * @param c
     */
    private void ensure(String s, int i, char c) throws ParseException
    {
        if (s.charAt(i) != c)
        {
            throw new ParseException("Invalid time-of-day string",i);
        }
    }
}
