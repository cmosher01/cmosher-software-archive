/*
 * TODO
 *
 * Created on Jun 10, 2004
 */
package com.surveysampling.time;

import java.util.Calendar;
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

    public void testGetTimeOnDay()
    {
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
    }

}
