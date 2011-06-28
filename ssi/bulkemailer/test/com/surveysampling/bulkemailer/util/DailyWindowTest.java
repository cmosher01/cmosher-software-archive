/*
 * Created on Jun 8, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.ParseException;
import java.util.Calendar;

import junit.framework.TestCase;

/**
 */
public class DailyWindowTest extends TestCase
{
    private static final Calendar cal = Calendar.getInstance();
    private final TimeOfDay nine = new TimeOfDay(cal,9,0,0,0);
    private final TimeOfDay five = new TimeOfDay(cal,17,0,0,0);
    private final TimeOfDay five2 = new TimeOfDay(cal,17,0,0,0);
    private final TimeOfDay six = new TimeOfDay(cal,18,0,0,0);

    public DailyWindowTest(String name)
    {
        super(name);
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(DailyWindowTest.class);
    }

    public void testAlways()
    {
        DailyWindow w = DailyWindow.createDailyWindowAlwaysOpen();
        assertTrue(w.isAlwaysOpen());
        assertFalse(w.isNeverOpen());
        assertFalse(w.hasWindow());

        try
        {
            w.getStart();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }

        try
        {
            w.getEnd();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }

        try
        {
            w.isStartFirst();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }
    }

    public void testNever()
    {
        DailyWindow w = DailyWindow.createDailyWindowNeverOpen();
        assertFalse(w.isAlwaysOpen());
        assertTrue(w.isNeverOpen());
        assertFalse(w.hasWindow());

        try
        {
            w.getStart();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }

        try
        {
            w.getEnd();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }

        try
        {
            w.isStartFirst();
            fail("Should throw RuntimeException");
        }
        catch (RuntimeException shouldBeThrown)
        {
            // OK
        }
    }

    public void testEquals()
    {
        DailyWindow wAlways = DailyWindow.createDailyWindowAlwaysOpen();
        DailyWindow wAlways2 = DailyWindow.createDailyWindowAlwaysOpen();
        DailyWindow wNever = DailyWindow.createDailyWindowNeverOpen();
        DailyWindow wNever2 = DailyWindow.createDailyWindowNeverOpen();
        assertEquals(wAlways,wAlways2);
        assertEquals(wNever,wNever2);
        assertFalse(wAlways.equals(wNever));

        DailyWindow w95 = DailyWindow.createDailyWindow(nine,five);
        DailyWindow w95a = DailyWindow.createDailyWindow(nine,five2);
        assertEquals(w95,w95a);
        assertEquals(w95a,w95);
        assertEquals(w95.hashCode(),w95a.hashCode());

        DailyWindow w96 = DailyWindow.createDailyWindow(nine,six);
        assertFalse(w95.equals(w96));
        assertFalse(wAlways.equals(w95));
        assertFalse(wNever.equals(w95));
    }

    public void testHash()
    {
        DailyWindow w95 = DailyWindow.createDailyWindow(nine,five);
        DailyWindow w95_2 = DailyWindow.createDailyWindow(nine,five);
        assertEquals(w95.hashCode(),w95_2.hashCode());

        DailyWindow wAlways = DailyWindow.createDailyWindowAlwaysOpen();
        assertEquals(1,wAlways.hashCode());
        
        DailyWindow wNever = DailyWindow.createDailyWindowNeverOpen();
        assertEquals(2,wNever.hashCode());
    }

    public void testSimpleAccessors()
    {
        DailyWindow w95 = DailyWindow.createDailyWindow(nine,five);
        assertEquals(new TimeOfDay(cal,9,0,0,0),w95.getStart());
        assertEquals(new TimeOfDay(cal,17,0,0,0),w95.getEnd());
        assertFalse(w95.isAlwaysOpen());
        assertFalse(w95.isNeverOpen());
        assertTrue(w95.hasWindow());
    }

    public void testIsStartFirst() throws IllegalArgumentException, ParseException
    {
        DailyWindow w9To5 = DailyWindow.createDailyWindow(nine,five);
        assertTrue(w9To5.isStartFirst());

        DailyWindow wElevenAtNightToOneInTheMorning = DailyWindow.createDailyWindow(new TimeOfDay(cal,new TimeOfDayParser("23:00")),new TimeOfDay(cal,new TimeOfDayParser("1:00")));
        assertFalse(wElevenAtNightToOneInTheMorning.isStartFirst());
    }

    public void testAutomaticAlways()
    {
        DailyWindow wAlways = DailyWindow.createDailyWindowAlwaysOpen();
        DailyWindow wAlwaysAuto = DailyWindow.createDailyWindow(new TimeOfDay(cal,1,2,3,4),new TimeOfDay(cal,1,2,3,4));
        assertEquals(wAlways,wAlwaysAuto);
    }

    public void testIsOpenAt() throws IllegalArgumentException, ParseException
    {
        DailyWindow w95 = DailyWindow.createDailyWindow(nine,five);
        TimeOfDay t0 = new TimeOfDay(cal,8,0,0,0);
        TimeOfDay t1 = new TimeOfDay(cal,11,0,0,0);
        TimeOfDay t2 = new TimeOfDay(cal,20,0,0,0);
        assertFalse(w95.isOpenAt(t0));
        assertTrue(w95.isOpenAt(t1));
        assertFalse(w95.isOpenAt(t2));

        DailyWindow wElevenAtNightToOneInTheMorning = DailyWindow.createDailyWindow(new TimeOfDay(cal,new TimeOfDayParser("23:00")),new TimeOfDay(cal,new TimeOfDayParser("1:00")));
        t0 = new TimeOfDay(cal,22,0,0,0);
        t1 = new TimeOfDay(cal,23,30,0,0);
        t2 = new TimeOfDay(cal,0,30,0,0);
        TimeOfDay t3 = new TimeOfDay(cal,2,0,0,0);
        assertFalse(wElevenAtNightToOneInTheMorning.isOpenAt(t0));
        assertTrue(wElevenAtNightToOneInTheMorning.isOpenAt(t1));
        assertTrue(wElevenAtNightToOneInTheMorning.isOpenAt(t2));
        assertFalse(wElevenAtNightToOneInTheMorning.isOpenAt(t3));

        DailyWindow wAlways = DailyWindow.createDailyWindowAlwaysOpen();
        assertTrue(wAlways.isOpenAt(t0));
        
        DailyWindow wNever = DailyWindow.createDailyWindowNeverOpen();
        assertFalse(wNever.isOpenAt(t0));
    }
}
