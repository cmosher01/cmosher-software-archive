public class CloneThreadTest
{
    private static class X
    {
        public int a;
        public int b;
        public String toString()
        {
            return ""+a+","+b;
        }
    }

    public static void main(String[] args)
    {
        final X x = new X();

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                System.out.println(x);
            }
        });
    }
}
