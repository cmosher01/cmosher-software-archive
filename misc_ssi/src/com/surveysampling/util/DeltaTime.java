/*
 * Created on May 21, 2004
 */
package com.surveysampling.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Contains static methods concerning a millisecond
 * time (long) treated as a length of time, rather
 * than a single point in time.
 * 
 * @author Chris Mosher
 */
public final class DeltaTime
{
    private DeltaTime() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * DeltaTime uses this to format the time portion
     * of the value. This is set to GMT time zone,
     * the same zone that Java's epoch is based on.
     */
    private static final SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss.SSS");
    static
    {
        f.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    /**
     * One day, in milliseconds.
     */
    private static final long ONE_DAY = 86400000L;

    /**
     * Convenience method that calls <code>format</code>
     * with a new <code>StringBuffer</code> and returns
     * the result as a <code>String</code>.
     * @param ms length of time, in milliseconds
     * @return String formatted result
     * @throws IllegalArgumentException if ms is negative
     */
    public static String format(long ms)
    {
        StringBuffer sb = new StringBuffer(16);
        format(ms,sb);
        return sb.toString();
    }

    /**
     * Formats a given length of time as a string in
     * the following format:
     * <p>
     * [<code>days </code>]<code>HH:mm:ss.SSS</code>
     * </p>
     * where <code>days</code> is the number of days (if greater than zero),
     * and the other fields are as defined in {@link java.text.SimpleDateFormat SimpleDateFormat}.
     * @param ms length of time, in milliseconds (cannot be negative)
     * @param sb StringBuffer to append the results to
     * @throws IllegalArgumentException if <code>ms</code> is negative
     */
    public static void format(long ms, StringBuffer sb)
    {
        if (ms < 0)
        {
            throw new IllegalArgumentException("delta time cannot be negative");
        }
        // split into full days and part of one day
        long days = ms/ONE_DAY;
        long part = ms%ONE_DAY;

        if (days > 0)
        {
            sb.append(days);
            sb.append(" ");
        }
        sb.append(f.format(new Date(part)));
    }
}
