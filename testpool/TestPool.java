import pool.Foo;
import pool.FooPool;

public class TestPool
{
    private TestPool()
    {
    }

    public static void main(String[] args)
    {
        FooPool ff = new FooPool(3);
        Foo[] f = new Foo[4];

        for (int i = 0; i < 3; i++)
        {
            f[i] = ff.get();
            System.out.println(f[i]);
        }

        for (int i = 0; i < 3; i++)
            f[i] = null;

        try
        {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
        }

        System.out.println(ff.get());
        System.out.println(ff.get());
        System.out.println(ff.get());
        System.out.println(ff.get());
    }
}
