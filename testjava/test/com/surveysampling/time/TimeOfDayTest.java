/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.TestCase;

/**
 * TODO
 */
public class TimeOfDayTest extends TestCase
{
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
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
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
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        Date d = fmt.parse("2004/04/03 00:00:00.000");
        Date dcomp = new Date(tod.getTimeOnDay(d));
        Date dexpt = fmt.parse("2004/04/03 17:34:12.367");
        assertEquals(dexpt,dcomp);

        TimeOfDay tod2 = new TimeOfDay(Calendar.getInstance(),2,30,0,0);
        d = fmt.parse("2004/04/04 00:00:00.000");
        dcomp = new Date(tod2.getTimeOnDay(d));
        dexpt = fmt.parse("2004/04/04 03:30:00.000");
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

}
