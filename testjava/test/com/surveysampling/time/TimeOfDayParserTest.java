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
        makeAndCheck("23:59:58.999",23,59,58,999);
    }

    public void testFormats() throws ParseException
    {
        makeAndCheck("9:98",9,98,0,0);
        makeAndCheck("99:98",99,98,0,0);
        makeAndCheck("99:98:97",99,98,97,0);
        makeAndCheck("99:98:97.996",99,98,97,996);
    }

    public void testZero() throws ParseException
    {
        makeAndCheck("00:00:00.000",0,0,0,0);
    }

    public void testBadStrings() throws ParseException
    {
        shouldThrow("");
        shouldThrow("a");
        shouldThrow("a:00");
        shouldThrow("01:ab");
        shouldThrow("01:02:cd");
        shouldThrow("01:02:03.00e");
    }

    private void shouldThrow(String s)
    {
        try
        {
            new TimeOfDayParser(s);
            fail("Should throw ParseException");
        }
        catch (ParseException shouldBeThrown)
        {
        }
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
