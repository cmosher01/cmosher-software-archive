package pool3;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.LinkedList;

public class FooPool
{
    private LinkedList unused = new LinkedList();
    private ReferenceQueue recycleBin = new ReferenceQueue();

    public FooPool(int size)
    {
        for (int i = 0; i < size; i++)
            unused.addLast(new FooDefault());
    }

    public synchronized Foo get()
    {
        recycle();
        Foo theFoo = new FooReference((Foo)unused.removeFirst(),recycleBin);
        return theFoo;
    }

    protected synchronized void recycle()
    {
        System.gc();

        Reference ref = recycleBin.poll();
        while (ref != null)
        {
            FooReference recyclable = (FooReference)ref;
            System.out.println("recycling: "+recyclable);
            unused.addLast(recyclable.getRecyclable());
            ref = recycleBin.poll();
        }
    }
}
