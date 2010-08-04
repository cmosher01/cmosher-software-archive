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

    final Machine machine = new Machine();
//    final Gui gui = new Gui(this.machine);

    @Override
    public void run()
    {
        msg("running");
        this.machine.run();
        sleep();
        sleep();
        sleep();
        msg("halting");
        this.machine.halt();
        sleep();
        sleep();
        msg("running");
        this.machine.run();
        sleep();
        sleep();
        sleep();
        msg("shutting down");
        this.machine.shutDown();
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
