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
                // TODO Auto-generated catch block
                e.printStackTrace();
                return null;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        final X x = new X();
        x.a = 6;
        x.b = 7;

        Thread tPrint = new Thread(new Runnable()
        {
            public void run()
            {
                while (true)
                {
                    System.out.println(x);
                    try
                    {
                        Thread.sleep(500);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        tPrint.setDaemon(true);

        Thread tSet = new Thread(new Runnable()
        {
            public void run()
            {
                X y = x.clone();
            }
        });
        tSet.setDaemon(true);

        tSet.start();
        tPrint.start();

        Thread.sleep(30000);
    }
}
