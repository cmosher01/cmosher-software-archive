import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;
import ui.DraggerUserInterface;




public class Dragger implements Runnable
{
    /**
     * @param rArg
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main(final String... rArg) throws InterruptedException, InvocationTargetException
    {
        final Dragger dragger = new Dragger();
        SwingUtilities.invokeAndWait(dragger);
    }



    private Dragger()
    {
        // only instantiated by main
    }



    public void run()
    {
    	final DraggerUserInterface ui = new DraggerUserInterface();
        ui.init();
    }
}
