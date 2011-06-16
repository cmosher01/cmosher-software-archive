package pom1;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import pom1.cli.CLI;
import pom1.gui.GUI;

class Exec
{
    public static void main(String... args) throws InterruptedException, InvocationTargetException, IOException
    {
    	GUI(args);
    }

    private static void CLI(String... args) throws IOException
    {
    	new CLI();
    }
    private static void GUI(String... args) throws InterruptedException, InvocationTargetException
    {
    	SwingUtilities.invokeAndWait(new Runnable()
    	{
			public void run()
			{
		        try
				{
					new GUI();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
					throw new IllegalStateException(e);
				}
			}
    	});
    }
}
