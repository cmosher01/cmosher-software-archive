/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * TODO
 */
public class TimeOfDayParser
{
    /**
     * Allowable formats:
     * 
     * H:mm
     * HH:mm
     * HH:mm:ss
     * HH:mm:ss.SSS
     * 
     * @param s
     * @return
     */
    public static TimeOfDay parse(String s) throws ParseException
    {
        if (s.length() == 4)
        {
            s = "0"+s;
        }

        SimpleDateFormat fmt = null;
        if (s.length() == 5)
        {
            fmt = new SimpleDateFormat("HH:mm:ss.SSS");
        }
        else if (s.length() == 8)
        {
            fmt = new SimpleDateFormat("HH:mm:ss.SSS");
        }
        else if (s.length() == 12)
        {
            fmt = new SimpleDateFormat("HH:mm:ss.SSS");
        }
        else
        {
            throw new ParseException("Invalid time-of-day string",0);
        }
    }
}
