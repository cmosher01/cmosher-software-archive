import pool3.Foo;
import pool3.FooPool;

public class TestPool3
{
    private TestPool3()
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

        for (int i = 0; i < 4; i++)
        {
            f[i] = ff.get();
            System.out.println(f[i]);
        }
    }
}
