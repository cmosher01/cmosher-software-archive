/*
 * Created on Jan 27, 2008
 */
package video;

import gui.UI;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoStaticGenerator
{
	private final AnalogTV tv;
	private final BufferedImage image;

	private Thread thread;
	private final AtomicBoolean shutdown = new AtomicBoolean(true);
	private final Random rand = new Random();
	private final UI ui;

	/**
	 * @param tv
	 */
	public VideoStaticGenerator(final AnalogTV tv, final UI ui, final BufferedImage image)
	{
		this.tv = tv;
		this.image = image;
		this.ui = ui;
	}

	public void run()
	{
		if (this.thread != null)
		{
			throw new IllegalStateException();
		}
		synchronized (this.shutdown)
		{
			this.shutdown.set(false);
		}
		this.thread = new Thread(new Runnable()
		{
			@SuppressWarnings("synthetic-access")
			public void run()
			{
				try
				{
			    	threadProcedure();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
		this.thread.setName("User-VideoStaticGenerator");
		this.thread.start();
	}

	private void threadProcedure()
	{
		while (!isShuttingDown())
		{
			update();
			this.ui.updateScreen();
			throttleIfNecessary();
		}
	}

	private void update()
	{
		this.tv.isig = 0;
		for (int i = 0; i < AppleNTSC.H*AppleNTSC.V; ++i)
		{
			this.tv.write_signal(this.rand.nextInt(140)-40);
		}
//		this.tv.draw_signal(this.image);
		this.tv.test_draw(this.image);
	}

	private int times;
	private long msPrev;
	private void throttleIfNecessary()
	{
		++this.times;
		if (this.times >= 60)
		{
			final long msActual = System.currentTimeMillis()-this.msPrev;
			final long msDelta = 1000-msActual;
			sleep(msDelta);

			this.msPrev = System.currentTimeMillis();
			this.times = 0;
		}
	}

	private static void sleep(final long msDelta)
	{
		if (msDelta > 0)
		{
			try
			{
				Thread.sleep(msDelta);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}

	private boolean isShuttingDown()
	{
		synchronized (this.shutdown)
		{
			return this.shutdown.get();
		}
	}
	public boolean isRunning()
	{
		return this.thread != null;
	}

	public void shutdown()
	{
		if (this.thread == null)
		{
			throw new IllegalStateException();
		}
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
		}

		try
		{
			this.thread.join();
			this.thread = null;
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
