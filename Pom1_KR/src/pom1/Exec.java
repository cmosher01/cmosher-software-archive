package pom1;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import pom1.gui.GUI;

class Exec
{
    Exec()
    {
    }

    public static void main(String args[]) throws InterruptedException, InvocationTargetException
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
