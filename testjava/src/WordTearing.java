public class WordTearing extends Thread
{
    static final int LENGTH = 8;
    static final int ITERS = 10000;
    static byte[] counts = new byte[LENGTH];
    static Thread[] threads = new Thread[LENGTH];
    final int id;


    public WordTearing(int i)
    {
        id = i;
    }

    public void run()
    {
        for (; counts[id] < ITERS; counts[id]++);
        if (counts[id] != ITERS)
        {
            System.err.println("Word-Tearing found: " + "counts[" + id + "] = " + counts[id]);
            System.exit(1);
        }
    }

    public static void main(String[] args)
    {
        for (int i = 0; i < LENGTH; ++i)
             (threads[i] = new WordTearing(i)).start();
    }
}
