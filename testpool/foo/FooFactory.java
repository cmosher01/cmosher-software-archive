package foo;

import java.util.LinkedList;

public class FooFactory
{
    private LinkedList cache = new LinkedList();



    public FooFactory(int maxObjects)
    {
        for (int i = 0; i < maxObjects; i++)
            cache.add(new FooDefault());
    }



    public Foo make()
    {
        Foo theFoo = null;
        if (!cache.isEmpty())
        {
            theFoo = (Foo)cache.getLast();
            cache.removeLast();
        }
        return theFoo;
    }

    public void release(Foo f)
    {
        if (!cache.contains(f))
            cache.add(f);
    }
}
