package com.surveysampling.mosher.rwlock;

public class Test
{
    RWLock lock = new RWLock();
    volatile boolean read1 = false;
    volatile boolean read2 = false;
    volatile boolean write3 = false;

    Test()
    {
    }

    public static void main(String[] rArg)
    {
        Test t = new Test();
        t.test();
    }
    
    void test()
    {
        System.out.println("begin main");

        Thread t1 = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        System.out.println("1: waiting to read");
                        lock.lockForReading();
                        read1 = true;
                        System.out.println("1: reading");
                        try { Thread.sleep(1001); } catch (InterruptedException e) { }
                    }
                    finally
                    {
                        read1 = false;
                        System.out.println("1: done reading------------------------1");
                        lock.unlock();
                    }
                }
            }
        });
        t1.setDaemon(true);
        t1.start();

        Thread t2 = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        System.out.println("    2: waiting to read");
                        lock.lockForReading();
                        read2 = true;
                        System.out.println("    2: reading");
                        try { Thread.sleep(1000); } catch (InterruptedException e) { }
                    }
                    finally
                    {
                        read2 = false;
                        System.out.println("    2: done reading------------------------2");
                        lock.unlock();
                    }
                }
            }
        });
        t2.setDaemon(true);
        t2.start();

        Thread t3 = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        System.out.println("        3: waiting to write");
                        lock.lockForWriting();
                        write3 = true;
                        System.out.println("        3: writing");
                        try { Thread.sleep(500); } catch (InterruptedException e) { }
                    }
                    finally
                    {
                        write3 = false;
                        System.out.println("        3: done writing------------------------3");
                        lock.unlock();
                    }
                }
            }
        });
        t3.setDaemon(true);
        t3.start();

        long msend = System.currentTimeMillis()+30000;
        while (System.currentTimeMillis() < msend)
        {
            if ((read1||read2)&&write3)
                System.out.println("ERROR: reading and writing at the same time!");
            try { Thread.sleep(50); } catch (InterruptedException e) { }
        }
        System.out.println("end main");
    }
}
