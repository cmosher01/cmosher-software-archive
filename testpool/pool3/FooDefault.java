package pool3;

class FooDefault implements Foo
{
    private static int count = 0;
    private int id = count++;

    FooDefault()
    {
    }

    public void foo()
    {
        System.out.println("Foo::foo says: " + this);
    }

    public String toString()
    {
        return "foo: " + id;
    }
}
