/*
 * Created on Jun 10, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 */
public class TimeOfDayTest extends TestCase
{
    private static final SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");

    private TimeOfDay tod = new TimeOfDay(Calendar.getInstance(),17,34,12,367);

    /**
     * Constructor for TimeOfDayTest.
     * @param arg0
     */
    public TimeOfDayTest(String arg0)
    {
        super(arg0);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(TimeOfDayTest.class);
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testGetHours()
    {
        assertEquals(17,tod.getHours());
    }

    public void testGetMinutes()
    {
        assertEquals(34,tod.getMinutes());
    }

    public void testGetSeconds()
    {
        assertEquals(12,tod.getSeconds());
    }

    public void testGetMilliseconds()
    {
        assertEquals(367,tod.getMilliseconds());
    }

    public void testGetCalendar()
    {
        Calendar s = tod.getCalendar();
        if (!(s instanceof GregorianCalendar))
        {
            fail("non-GregorianCalendar returned");
        }
    }

    public void testGetTimeOnDay() throws ParseException
    {
        Date d = fmt.parse("2004/04/03 00:00:00.000");
        Date dcomp = new Date(tod.getTimeOnDay(d));
        Date dexpt = fmt.parse("2004/04/03 17:34:12.367");
        assertEquals(dexpt,dcomp);
    }

    public void testGetTimeOnDayDuringDSTAhead() throws ParseException
    {
        // 2:30 AM doesn't exist on morning of DST clock advance
        TimeOfDay tod2 = new TimeOfDay(Calendar.getInstance(),2,30,0,0);
        Date d = fmt.parse("2004/04/04 00:00:00.000");
        Date dcomp = new Date(tod2.getTimeOnDay(d));
        // GregorianCalendar changes it to 3:30 AM
        Date dexpt = fmt.parse("2004/04/04 03:30:00.000");
        assertEquals(dexpt,dcomp);
    }

    /*
     * Test for boolean equals(Object)
     */
    public void testEqualsObject()
    {
        TimeOfDay tod2 = new TimeOfDay(Calendar.getInstance(),17,34,12,367);
        assertEquals(tod2,tod);
    }

    public void testCompareTo()
    {
        TimeOfDay tod2 = new TimeOfDay(Calendar.getInstance(),17,34,13,000);
        assertTrue(tod.compareTo(tod2)<0);
        TimeOfDay tod3 = new TimeOfDay(Calendar.getInstance(),17,34,11,000);
        assertTrue(tod.compareTo(tod3)>0);
        TimeOfDay tod4 = new TimeOfDay(Calendar.getInstance(),17,34,12,367);
        assertTrue(tod.compareTo(tod4)==0);
    }

    public void testIllegalArguments()
    {
        Calendar cal = Calendar.getInstance();
        try
        {
            new TimeOfDay(cal,-1,0,0,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,24,0,0,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,-1,0,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,60,0,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,0,-1,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,0,60,0);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,0,0,-1);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new TimeOfDay(cal,0,0,0,1000);
            fail("should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        new TimeOfDay(cal,0,0,0,0);
        new TimeOfDay(cal,23,59,59,999);
    }

    public void testHashCode()
    {
        TimeOfDay tod2 = new TimeOfDay(Calendar.getInstance(),17,34,12,367);
        assertEquals(tod2.hashCode(),tod.hashCode());
    }

    public void testToString()
    {
        assertEquals("17:34:12.367",tod.toString());
        shouldFormatAs(9,30,0,0,"09:30");
        shouldFormatAs(21,30,0,0,"21:30");
        shouldFormatAs(2,0,0,0,"02:00");
        shouldFormatAs(2,0,0,1,"02:00:00.001");
        shouldFormatAs(23,59,59,999,"23:59:59.999");
        shouldFormatAs(23,59,59,0,"23:59:59");
    }

    private void shouldFormatAs(int hour, int minute, int second, int millisecond, String should)
    {
        assertEquals(should,new TimeOfDay(Calendar.getInstance(),hour,minute,second,millisecond).toString());
    }
}
