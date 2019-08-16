import java.awt.image.BufferedImage;
import java.io.Closeable;
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
		System.out.println("  FP_START: "+AppleNTSC.FP_START);
		System.out.println("SYNC_START: "+AppleNTSC.SYNC_START);
		System.out.println("  BP_START: "+AppleNTSC.BP_START);
		System.out.println("  CB_START: "+AppleNTSC.CB_START);
		System.out.println(" PIC_START: "+AppleNTSC.PIC_START);

		BufferedImage image = new BufferedImage(AppleNTSC.H,AppleNTSC.V * 2,BufferedImage.TYPE_INT_RGB);
		GUI gui = new GUI(thistv,image);

		AnalogTV tv = new AnalogTV();

		tv.write_sync_signal();

//		tv.write_apple_color_test();
		tv.write_play_signal4();

//		dump_signal(in.signal);
//		draw_signal(in.signal,image);

		tv.test_draw(image);
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
