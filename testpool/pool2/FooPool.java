package pool2;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NoSuchElementException;

public class FooPool
{
    private LinkedList unused = new LinkedList();
    private Map inUse = new HashMap();
    private ReferenceQueue recycleBin = new ReferenceQueue();

    public FooPool(int size)
    {
        for (int i = 0; i < size; i++)
            unused.addLast(new FooDefault());
    }

    public synchronized Foo get() throws NoSuchElementException
    {
        recycle();

        Foo theFoo = (Foo)unused.removeFirst();
        Foo theProxy = new FooProxy(theFoo);
        Reference ref = new WeakReference(theProxy,recycleBin);
        inUse.put(ref,theFoo);

        return theProxy;
    }

    protected synchronized void recycle()
    {
        System.gc();

        Reference ref = recycleBin.poll();
        while (ref != null)
        {
            Object recyclable = inUse.remove(ref);
            unused.addLast(recyclable);
            
            ref = recycleBin.poll();
        }
    }
}
