/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

        if (s.length() == 5)
        {
            ensure(s,2,':');
        }
        else if (s.length() == 8)
        {
        }
        else if (s.length() == 12)
        {
        }
        else
        {
            throw new ParseException("Invalid time-of-day string",0);
        }
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
