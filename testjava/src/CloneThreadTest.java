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
        public Object clone()
        {
            try
            {
                return super.clone();
            }
            catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        final X x = new X();
        x.a = 6;
        x.b = 6;

        Thread tPrint = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    x.a++;
                    x.b++;
                }
            }
        });
        tPrint.setDaemon(true);

        Thread tSet = new Thread(new Runnable()
        {
            public void run()
            {
                X y = (X)x.clone();
                System.out.println(y);
            }
        });
        tSet.setDaemon(true);

        tSet.start();
        tPrint.start();

        Thread.sleep(30000);
    }
}
