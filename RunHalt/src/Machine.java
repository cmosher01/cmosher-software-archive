import java.util.concurrent.atomic.AtomicBoolean;

public class Machine
{
    private final AtomicBoolean running = new AtomicBoolean(false);

    private final Object lock = new Object();
    private final Thread thread = new Thread(new Runnable()
    {
        @SuppressWarnings("synthetic-access")
        @Override
        public void run()
        {
            System.out.println("[starting]");
            execute();
            System.out.println("[exiting]");
        }
        
    });

    public Machine()
    {
        this.thread.start();
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
    }


    public void run()
    {
        this.running.set(true);
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
    }

    public void halt()
    {
        this.running.set(false);
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
    }

    public void shutDown()
    {
        halt();
        this.thread.interrupt();
        join();
    }
    public boolean isRunning()
    {
        return this.running.get();
    }

    public boolean isShuttingDown()
    {
        return this.thread.isInterrupted();
    }



    private void execute()
    {
        int i = 0;
        while (!isShuttingDown())
        {
            synchronized (this.lock)
            {
                while (!isRunning())
                {
                    System.out.println("[sleeping]");
                    try
                    {
                        this.lock.wait();
                    }
                    catch (final InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("[waking]");
                }
            }
            System.out.println("running: "+i++);
            sleep();
        }
    }

    private void join()
    {
        try
        {
            this.thread.join();
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(1000);
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
