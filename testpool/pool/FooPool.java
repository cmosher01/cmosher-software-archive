package pool;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class FooPool
{
    private LinkedList unused = new LinkedList();
    private Map inUse = new HashMap();
    private ReferenceQueue rq = new ReferenceQueue();

    public FooPool(int maxObjects)
    {
        for (int i = 0; i < maxObjects; i++)
            unused.addLast(new FooDefault());

        Thread runGC = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    System.gc();
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
            }
        });

        runGC.setDaemon(true);
        runGC.setPriority(Thread.MIN_PRIORITY);
        runGC.start();

        Thread reaper = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    Reference ref = null;

                    try
                    {
                        ref = rq.remove();
                    }
                    catch (InterruptedException e)
                    {
                        continue;
                    }

                    Object recyclable = null;
                    synchronized (inUse)
                    {
                        recyclable = inUse.remove(ref);
                    }
                    synchronized (unused)
                    {
                        unused.addLast(recyclable);
                    }
                }
            }
        });

        reaper.setDaemon(true);
        reaper.start();
    }

    public Foo get()
    {
        Foo theFoo = null;
        synchronized (unused)
        {
            if (unused.isEmpty())
                throw new BufferUnderflowException();

            theFoo = (Foo)unused.removeFirst();
        }

        Foo theProxy = new FooProxy(theFoo);
        Reference ref = new WeakReference(theProxy,rq);
        synchronized (inUse)
        {
            inUse.put(ref,theFoo);
        }

        return theProxy;
    }
}
