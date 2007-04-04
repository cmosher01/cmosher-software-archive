import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import nu.mine.mosher.gedcom.exception.InvalidLevel;
import ui.DraggerUserInterface;
import ui.DrawableFamily;
import ui.DrawablePersona;




public class DraggerTest implements Runnable
{
    /**
     * @param rArg
     * @throws InvocationTargetException 
     * @throws InterruptedException 
     */
    public static void main(final String... rArg) throws InterruptedException, InvocationTargetException
    {
        final DraggerTest dragger = new DraggerTest();
        SwingUtilities.invokeAndWait(dragger);
    }



    private DraggerTest()
    {
        // only instantiated by main
    }



    public void run()
    {
    	final DraggerUserInterface ui = new DraggerUserInterface();
        ui.init();

        final DrawableFactory factory = new DrawableFactory();
        try
		{
			factory.create();
		}
		catch (final Throwable e)
		{
			e.printStackTrace();
			return;
		}

        final List<DrawablePersona> rPersona = new ArrayList<DrawablePersona>();
        factory.getPersona(rPersona);
        final List<DrawableFamily> rFamily = new ArrayList<DrawableFamily>();
        factory.getFamily(rFamily);
        ui.test(rPersona,rFamily);
    }
}
