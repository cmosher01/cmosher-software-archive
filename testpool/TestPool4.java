import pool4.Foo;
import pool4.FooPool;

public class TestPool4
{
    private TestPool4()
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

        for (int i = 0; i < 3; i++)
        {
            f[i] = ff.get();
            System.out.println(f[i]);
        }
        for (int i = 0; i < 3; i++)
            f[i] = null;

        for (int i = 0; i < 3; i++)
        {
            f[i] = ff.get();
            System.out.println(f[i]);
        }
        Foo tooMany = ff.get();
    }
}
