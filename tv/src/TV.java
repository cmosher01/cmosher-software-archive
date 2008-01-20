import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingUtilities;

/*
 * Created on Jan 18, 2008
 */
public class TV implements Closeable
{
	private static TV thistv;
	public static void main(final String... args) throws InterruptedException, InvocationTargetException
	{
		Thread.currentThread().setName("User-main");
		SwingUtilities.invokeAndWait(new Runnable()
		{
			public void run()
			{
				thistv = new TV();
				program();
			}
		});
		synchronized (thistv.shutdown)
		{
			while (!thistv.shutdown.get())
			{
				try
				{
					thistv.shutdown.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private static void program()
	{
		System.out.println("SYNC_START: "+ANALOGTV.SYNC_START);
		System.out.println("  BP_START: "+ANALOGTV.BP_START);
		System.out.println("  CB_START: "+ANALOGTV.CB_START);
		System.out.println(" PIC_START: "+ANALOGTV.PIC_START);
		System.out.println("  FP_START: "+ANALOGTV.FP_START);

		BufferedImage image = new BufferedImage(ANALOGTV.H,ANALOGTV.V * 2,BufferedImage.TYPE_INT_RGB);
		GUI gui = new GUI(thistv,image);

		analogtv_ tv = new analogtv_();

		tv.analogtv_setup_sync();
		tv.analogtv_read_color_info();

//		dump_signal(in.signal);
//		draw_signal(in.signal,image);

		tv.analogtv_test_draw(image);
	}

	private final AtomicBoolean shutdown = new AtomicBoolean();

	public void close()
	{
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
			this.shutdown.notifyAll();
		}
	}
}
