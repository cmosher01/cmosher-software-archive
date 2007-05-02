/*
 * Created on Jun 10, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.surveysampling.util.Flag;

import junit.framework.TestCase;

/**
 * Tests the ThrottlePrecise class. That class deals with
 * time-sensitive logic, so some of these tests may
 * fail intermittently, even though the class is working correctly.
 */
public class ThrottlePreciseTest extends TestCase
{
    /**
     * @param name
     */
    public ThrottlePreciseTest(String name)
    {
        super(name);
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ThrottlePreciseTest.class);
    }

    /**
     * Define a Throttleable subclass that will
     * run the test
     */
    public static class ThrottleTestSimple implements Throttleable
    {
        int index;
        int iStop;
        int iGo;
        Flag runnable = new Flag(true);

        public synchronized int getIndex()
        {
            return index;
        }
        public synchronized void inc()
        {
            ++index;
        }
        public synchronized int getGo()
        {
            return iGo;
        }
        public synchronized void incGo()
        {
            ++iGo;
        }
        public synchronized int getStop()
        {
            return iStop;
        }
        public synchronized void incStop()
        {
            ++iStop;
        }

        // runs the test
        public void test()
        {
            // create a throttle for 20 per second (10 ms variance)
            ThrottlePrecise th = new ThrottlePrecise(this,20,1000,10,1000);
            // this will hold a timestamp of each iteration
            long[] rms = new long[85];

            // iterate. the throttle will call our stop
            // and go methods when appropriate
            for (int i = 0; i < 85; ++i)
            {
                try
                {
                    // wait for throttle to give us the go
                    runnable.waitUntilTrue();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                // take a timestamp
                rms[i] = System.currentTimeMillis();
                // increment our index (this is our "work")
                inc();
                // tell the throttle we did a unit of work
                // (this will cause the throttle to call our
                // stop method when we hit the limit)
                th.increment();
            }
            // we should have stopeed and started 4 times
            assertEquals(4,getGo());
            assertEquals(4,getStop());
            // check our timestamps to be sure we
            // did 20 per second (allow some deviation)
            long expect = rms[0];
            for (int i = 1; i < 85; ++i)
            {
                if (i%20 == 0)
                {
                    expect += 1000;
                }
                assertTrue(withinOneHalf(expect,rms[i]));
            }
            assertSame(this,th.getThrottleable());
        }

        // the throttle is telling us to go.
        // we count it, and set our flag so
        // the main loop starts running again
        public void go()
        {
            incGo();
            try
            {
                runnable.waitToSetTrue();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        // the throttle is telling us to stop.
        // we count it, check our index to be sure
        // we are stopping at the right time, and
        // set our flag so the testing loop waits
        public void stop()
        {
            incStop();
            /*
             * If the test fails here, it may be OK.
             * This test assumes that we can complete
             * 20 iterations of the main loop within
             * one second. While this is normally
             * true, sometimes it may not be true,
             * due to external factors such as
             * garbage collection or a slow machine.
             */
            assertEquals(getStop()*20,getIndex());
            try
            {
                runnable.waitToSetFalse();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void testSimple()
    {
        // create and run the test
        ThrottleTestSimple tester = new ThrottleTestSimple();
        tester.test();
    }

    public static class ThrottleTestChangeMax implements Throttleable
    {
        int index;
        int iStop;
        int iGo;
        Flag runnable = new Flag(true);

        public synchronized int getIndex()
        {
            return index;
        }
        public synchronized void inc()
        {
            ++index;
        }
        public synchronized int getGo()
        {
            return iGo;
        }
        public synchronized void incGo()
        {
            ++iGo;
        }
        public synchronized int getStop()
        {
            return iStop;
        }
        public synchronized void incStop()
        {
            ++iStop;
        }

        public void test()
        {
            ThrottlePrecise th = new ThrottlePrecise(this,20,1000,10,1000);
            long[] rms = new long[105];
            for (int i = 0; i < 105; ++i)
            {
                try
                {
                    runnable.waitUntilTrue();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                if (i == 65)
                {
                    th.setMax(22);
                }
                rms[i] = System.currentTimeMillis();
                inc();
                th.increment();
            }
            assertEquals(5,getGo());
            assertEquals(5,getStop());
            long expect = rms[0];
            for (int i = 1; i < 105; ++i)
            {
                if (i <= 65)
                {
                    if (i%20 == 0)
                    {
                        expect += 1000;
                    }
                }
                else
                {
                    if ((i-60)%22 == 0)
                    {
                        expect += 1000;
                    }
                }
                assertTrue(withinOneHalf(expect,rms[i]));
            }
        }

        public void go()
        {
            incGo();
            try
            {
                runnable.waitToSetTrue();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        public void stop()
        {
            incStop();
            /*
             * If the test fails here, it may be OK.
             * This test assumes that we can complete
             * 20 iterations of the main loop within
             * one second. While this is normally
             * true, sometimes it may not be true,
             * due to external factors such as
             * garbage collection or a slow machine.
             */
            if (getIndex() <= 65)
            {
                assertEquals(getStop()*20,getIndex());
            }
            else
            {
                assertEquals(getStop()*20+(getStop()-3)*2,getIndex());
            }
            try
            {
                runnable.waitToSetFalse();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void testChangeMax()
    {
        ThrottleTestChangeMax tester = new ThrottleTestChangeMax();
        tester.test();
    }

    public static class ThrottleTestChangePer implements Throttleable
    {
        int index;
        int iStop;
        int iGo;
        Flag runnable = new Flag(true);

        public synchronized int getIndex()
        {
            return index;
        }
        public synchronized void inc()
        {
            ++index;
        }
        public synchronized int getGo()
        {
            return iGo;
        }
        public synchronized void incGo()
        {
            ++iGo;
        }
        public synchronized int getStop()
        {
            return iStop;
        }
        public synchronized void incStop()
        {
            ++iStop;
        }

        public void test()
        {
            ThrottlePrecise th = new ThrottlePrecise(this,20,1000,10,1000);
            long[] rms = new long[105];
            for (int i = 0; i < 105; ++i)
            {
                try
                {
                    runnable.waitUntilTrue();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                if (i == 65)
                {
                    th.setPer(2000);
                }
                rms[i] = System.currentTimeMillis();
                inc();
                th.increment();
            }
            assertEquals(5,getGo());
            assertEquals(5,getStop());
            long expect = rms[0];
            for (int i = 1; i < 105; ++i)
            {
                if (i <= 65)
                {
                    if (i%20 == 0)
                    {
                        expect += 1000;
                    }
                }
                else
                {
                    if (i%20 == 0)
                    {
                        expect += 2000;
                    }
                }
                assertTrue(withinOneHalf(expect,rms[i]));
            }
        }

        public void go()
        {
            incGo();
            try
            {
                runnable.waitToSetTrue();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }

        public void stop()
        {
            incStop();
            /*
             * If the test fails here, it may be OK.
             * This test assumes that we can complete
             * 20 iterations of the main loop within
             * one second. While this is normally
             * true, sometimes it may not be true,
             * due to external factors such as
             * garbage collection or a slow machine.
             */
            assertEquals(getStop()*20,getIndex());
            try
            {
                runnable.waitToSetFalse();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void testChangePer()
    {
        ThrottleTestChangePer tester = new ThrottleTestChangePer();
        tester.test();
    }

    protected static boolean withinOneHalf(long t0, long t1)
    {
        if (t0 < t1)
        {
            return t1-t0 <= 500;
        }
        else if (t1 < t0)
        {
            return t0-t1 <= 500;
        }
        else
        {
            return true;
        }
    }

    protected void dumpTimes(long[] rms)
    {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
        for (int i = 0; i < rms.length; ++i)
        {
            System.out.println(fmt.format(new Date(rms[i])));
        }
        System.out.println("------------------------");
    }

    public void testInvalidArguments()
    {
        class NullThrottleable implements Throttleable
        {
            public void go() { /* */ }
            public void stop() { /* */ }
        }
        try
        {
            new ThrottlePrecise(new NullThrottleable(),-2,1,1,1000);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new ThrottlePrecise(new NullThrottleable(),0,1,1,1000);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new ThrottlePrecise(new NullThrottleable(),1,0,1,1000);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
        try
        {
            new ThrottlePrecise(new NullThrottleable(),0,1,-1,1000);
            fail("Should throw IllegalArgumentException");
        }
        catch (IllegalArgumentException shouldBeThrown)
        {
            // OK
        }
    }

    public void testUnlimitedThrottle()
    {
        class ErrorThrottleable implements Throttleable
        {
            public void go() { fail("go should never be called"); }
            public void stop() { fail("stop should never be called"); }
        }
        ThrottlePrecise th = new ThrottlePrecise(new ErrorThrottleable(),-1,1,10,1000);
        for (int i = 0; i < 100; ++i)
        {
            th.increment();
        }
    }

    public static class ThrottleTestResetUnlimited implements Throttleable
    {
        int index;
        int iStop;
        int iGo;
        Flag runnable = new Flag(true);

        public synchronized int getIndex()
        {
            return index;
        }
        public synchronized void inc()
        {
            ++index;
        }
        public synchronized int getGo()
        {
            return iGo;
        }
        public synchronized void incGo()
        {
            ++iGo;
        }
        public synchronized int getStop()
        {
            return iStop;
        }
        public synchronized void incStop()
        {
            ++iStop;
        }
        public void test()
        {
            // start out unthrottled and run some units
            // (this should have no effect)
            ThrottlePrecise th = new ThrottlePrecise(this,-1,1000,10,1000);
            for (int i = 0; i < 100; ++i)
            {
                th.increment();
            }
            assertEquals(-1,th.getRate());

            // now set to 20 (per second)
            th.setMax(20);
            assertEquals(20,th.getRate());
            // and run a normal test

            long[] rms = new long[85];

            for (int i = 0; i < 85; ++i)
            {
                try
                {
                    runnable.waitUntilTrue();
                }
                catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
                rms[i] = System.currentTimeMillis();
                inc();
                th.increment();
            }
            assertEquals(20,th.getRate());
            assertEquals(4,getGo());
            assertEquals(4,getStop());
            long expect = rms[0];
            for (int i = 1; i < 85; ++i)
            {
                if (i%20 == 0)
                {
                    expect += 1000;
                }
                assertTrue(withinOneHalf(expect,rms[i]));
            }
        }
        public void go()
        {
            incGo();
            try
            {
                runnable.waitToSetTrue();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
        public void stop()
        {
            incStop();
            assertEquals(getStop()*20,getIndex());
            try
            {
                runnable.waitToSetFalse();
            }
            catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void testResetUnlimited()
    {
        ThrottleTestResetUnlimited tester = new ThrottleTestResetUnlimited();
        tester.test();
    }
}
