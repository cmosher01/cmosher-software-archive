package pool3;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

class FooReference extends WeakReference implements Foo
{
    private Foo recyclable;

    FooReference(Foo foo, ReferenceQueue rq)
    {
        super(rq);
        recyclable = foo;
    }

    Foo getRecyclable()
    {
        return recyclable;
    }

//copy all methods of Foo interface here; delegate to underlying Foo
    public void foo()
    {
        recyclable.foo();
    }

    public String toString()
    {
        return recyclable.toString();
    }
}
