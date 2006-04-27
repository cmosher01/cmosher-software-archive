

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import ui.DraggerUserInterface;




public class Dragger implements Runnable, Closeable
{
    /**
     * @param args
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main(String[] args) throws InterruptedException, InvocationTargetException
    {
        final Dragger dragger = new Dragger();
        SwingUtilities.invokeAndWait(dragger);
    }

    private final DraggerUserInterface ui = new DraggerUserInterface();

    private Dragger()
    {
        // only instantiated by main
    }



    public void run()
    {
        DraggerUserInterface.setSwingDefaults();

        // create the main frame window for the application
        this.ui.init(new WindowAdapter()
        {
            @Override
            public void windowClosing(final WindowEvent e)
            {
                close();
            }
        });
    }

    public void close()
    {
        this.ui.close();
    }
}
