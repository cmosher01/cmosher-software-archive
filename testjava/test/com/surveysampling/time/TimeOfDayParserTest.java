/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.text.ParseException;

import junit.framework.TestCase;

/**
 * TODO
 */
public class TimeOfDayParserTest extends TestCase
{

    /**
     * Constructor for TimeOfDayParserTest.
     * @param arg0
     */
    public TimeOfDayParserTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TimeOfDayParserTest.class);
    }

    public void testAccessors() throws ParseException
    {
        TimeOfDayParser p = new TimeOfDayParser("23:59:58.999");
        assertEquals(23,p.getHour());
        assertEquals(59,p.getMinute());
        assertEquals(58,p.getSecond());
        assertEquals(999,p.getMillisecond());
    }

    public void testFormats()
    {
        makeAndCheck("23:59:58.999",23,59,58,999);
        TimeOfDayParser p = new TimeOfDayParser("23:59:58.999");
        assertEquals(23,p.getHour());
        assertEquals(59,p.getMinute());
        assertEquals(58,p.getSecond());
        assertEquals(999,p.getMillisecond());
    }

    /**
     * @param string
     * @param i
     * @param j
     * @param k
     * @param l
     */
    private void makeAndCheck(String s, int hour, int minute, int second, int millisecond) throws ParseException
    {
        TimeOfDayParser p = new TimeOfDayParser(s);
        assertEquals(hour,p.getHour());
        assertEquals(minute,p.getMinute());
        assertEquals(second,p.getSecond());
        assertEquals(millisecond,p.getMillisecond());
    }
}
