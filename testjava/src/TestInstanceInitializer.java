public class TestInstanceInitializer
{

    {
        System.out.println("instance initializer");
    }

    public TestInstanceInitializer()
    {
        System.out.println("constructor");
    }

    public static void main(String[] rArg)
    {
        System.out.println("main");
        new TestInstanceInitializer();
    }
}
