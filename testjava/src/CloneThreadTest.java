public class CloneThreadTest
{
    private static class X implements Cloneable
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

        Thread tMutate = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    while (true)
                    {
                        synchronized (x)
                        {
                            x.a++;
                            Thread.sleep(30);
                            x.b++;
                        }
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        tMutate.setDaemon(true);

        Thread tClone = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    while (true)
                    {
                        X y = null;
                        synchronized (x)
                        {
                            y = (X)x.clone();
                        }
                        if (y.a != y.b)
                        {
                            System.err.println(y);
                        }
                        Thread.sleep(9);
                    }
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });
        tClone.setDaemon(true);

        tClone.start();
        tMutate.start();

        Thread.sleep(30000);
    }
}
