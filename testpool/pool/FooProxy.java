package pool;

class FooProxy implements Foo
{
    private Foo delegate;

    FooProxy(Foo f)
    {
        delegate = f;
    }

    public void foo()
    {
        delegate.foo();
    }

    public String toString()
    {
        return delegate.toString();
    }
}
