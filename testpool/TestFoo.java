import foo.Foo;
import foo.FooFactory;

public class TestFoo
{
    private TestFoo()
    {
    }

    public static void main(String[] args)
    {
        FooFactory ff = new FooFactory(3);

        Foo[] rf = new Foo[3];

        for (int i = 0; i < 3; i++)
        {
            rf[i] = ff.make();
            System.out.println(rf[i]);
        }

        // This allocation fails
        System.out.println(ff.make());

        // Next allocation fails unless release is done
        ff.release(rf[0]);
        ff.release(rf[2]);
        System.out.println(ff.make());
        System.out.println(ff.make());

        System.out.println(ff.make());
    }
}
