package pool5.test;

import pool5.ObjectPool;

public class TestPool5
{
    private TestPool5()
    {
    }

    public static void main(String[] args)
    {
        Object[] f = new Object[] { new FooDefault(), new FooDefault(), new FooDefault() };

        ObjectPool ff = new ObjectPool(f);

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
        Foo tooMany = (Foo)ff.get();
    }
}
