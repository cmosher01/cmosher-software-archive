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
            execute();
        }
    });

    private final MachineListener listener;

    public Machine(final MachineListener listener)
    {
    	this.listener = listener;
    }


    public void run(final boolean run)
    {
        this.running.set(run);
        synchronized (this.lock)
        {
        	if (run)
        	{
                startThread();
        	}
            this.lock.notifyAll();
        }
    }

    public void shutDown()
    {
        this.thread.interrupt();
        joinThread();
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
        this.listener.start();
        while (!isShuttingDown())
        {
            checkSleep();
            if (!isShuttingDown())
            {
                this.listener.cycle();
            }
        }
        this.listener.stop();
    }

	private void checkSleep()
	{
		synchronized (this.lock)
		{
		    while (!isRunning() && !isShuttingDown())
		    {
		        this.listener.sleep();
		        try
		        {
		            this.lock.wait();
		        }
		        catch (final InterruptedException e)
		        {
		            Thread.currentThread().interrupt();
		        }
		        this.listener.wake();
		    }
		}
	}

	private void startThread()
	{
		if (!this.thread.getState().equals(Thread.State.NEW))
        {
			return;
        }
        this.thread.start();
	}

    private void joinThread()
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
}
