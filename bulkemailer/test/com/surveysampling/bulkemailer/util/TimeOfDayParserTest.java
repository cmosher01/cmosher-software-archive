/*
 * Created on Jun 10, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.ParseException;

import junit.framework.TestCase;

/**
 */
public class TimeOfDayParserTest extends TestCase
{
    public TimeOfDayParserTest(String name)
    {
        super(name);
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

    public void testI18n() throws ParseException
    {
        // even Arabic-Indic digits work!
        makeAndCheck("\u0661:\u0662\u0663",1,23,0,0);
    }

    public void testBadStrings()
    {
        shouldThrow("");
        shouldThrow("a");
        shouldThrow("a:00");
        shouldThrow("01:ab");
        shouldThrow("01:02:cd");
        shouldThrow("01:02:03.00e");
        shouldThrow("01:023");
        shouldThrow("01:0");
        shouldThrow("0::0");
        shouldThrow(" ");
        shouldThrow("  :  :  .   ");
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
            // OK
        }
    }

    private void makeAndCheck(String s, int hour, int minute, int second, int millisecond) throws ParseException
    {
        TimeOfDayParser p = new TimeOfDayParser(s);
        assertEquals(hour,p.getHour());
        assertEquals(minute,p.getMinute());
        assertEquals(second,p.getSecond());
        assertEquals(millisecond,p.getMillisecond());
    }
}
