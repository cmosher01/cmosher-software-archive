import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

public class RunHalt implements Runnable
{

    /**
     * @param args
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException
    {
        SwingUtilities.invokeAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                msg("constructing");
                final Runnable program = new RunHalt();
                program.run();
                msg("exiting");
            }
            
        });
    }

    final MachineListener listener = new MachineListener()
    {
		@Override
		public void cycle()
		{
			System.out.println("cycle");
			try
			{
				Thread.sleep(1000);
			}
			catch (final InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}

		@Override
		public void sleep()
		{
			System.out.println("sleep");
		}

		@Override
		public void start()
		{
			System.out.println("start");
		}

		@Override
		public void stop()
		{
			System.out.println("stop");
		}

		@Override
		public void wake()
		{
			System.out.println("wake");
		}
    	
    };

    final Machine machine = new Machine(this.listener);
//    final Gui gui = new Gui(this.machine);

    @Override
    public void run()
    {
        sleep();
        sleep();
        sleep();
        msg("running");
        this.machine.run(true);
        sleep();
        sleep();
        sleep();
        msg("halting");
        this.machine.run(false);
        sleep();
        sleep();
/*
        this.machine.run(true);
        sleep();
        sleep();
        sleep();
 */
        msg("shutting down");
        this.machine.shutDown();
        sleep();
        sleep();
    }

    /**
     * @param string
     */
    private static void msg(final String msg)
    {
        System.out.println("---------------------------------"+msg);
    }

    private static void sleep()
    {
        try
        {
            Thread.sleep(1500);
        }
        catch (final InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
