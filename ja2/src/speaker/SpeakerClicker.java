package speaker;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/*
 * Created on Jan 8, 2008
 */
public class SpeakerClicker
{
	private final Object lock = new Object();


	private volatile int t;

	private volatile byte[] pcm = new byte[44];
	private int p;

	private volatile boolean click;
	private volatile SourceDataLine line;


	private volatile boolean feed;


	private boolean sound;

	public SpeakerClicker()
	{
//		this.pcm[0] = 0;
//		this.pcm[1] = 0x5F;
		try
		{
			init();
		}
		catch (LineUnavailableException e)
		{
			e.printStackTrace();
		}
	}

	private void init() throws LineUnavailableException
	{
		final AudioFormat fmt = new AudioFormat(44100,8,1,false,false);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class,fmt);
		if (!AudioSystem.isLineSupported(info))
		{
			throw new LineUnavailableException("audio line not supported");
		}

		this.line = (SourceDataLine)AudioSystem.getLine(info);

		this.line.open(fmt,44); // 10 ms buffer
		this.line.start();

		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				feed();
			}
		});
		th.setDaemon(true);
		th.start();
	}

	private void feed()
	{
		while (true)
		{
			boolean f = false;
			synchronized (this.lock)
			{
				try
				{
					this.lock.wait();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				f = this.feed;
			}
			if (true)
			{
				if (this.click)
				{
					this.pcm[this.p++] = 0x5F;
					this.sound = true;
					this.click = false;
				}
				else
				{
					this.pcm[this.p++] = 0;
				}
				if (this.p >= this.pcm.length)
				{
					if (this.sound)
					{
						this.line.write(this.pcm,0,this.pcm.length);
//						this.line.start();
					}
//					else
//						this.line.stop();
					this.sound = false;
					System.out.print("sound: ");
					for (int i = 0; i < this.pcm.length; i++)
					{
						byte b = this.pcm[i];
						System.out.print(b);
						System.out.print(" ");
					}
					System.out.println();
					this.p = 0;
				}
				synchronized (this.lock)
				{
					this.feed = false;
				}
			}
		}
	}
	public void tick()
	{
		while (this.line == null)
		{
			System.err.println("waiting for speaker to initialize");
			try
			{
				Thread.sleep(10);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
		++this.t;
		if (this.t >= 23)
		{
			synchronized (this.lock)
			{
				this.feed = true;
				this.lock.notifyAll();
			}
			this.t = 0;
		}
	}

	public void click()
	{
		this.click = true;
		System.out.println("click");
//		while (this.clip == null)
//		{
//			System.err.println("cannot unmute speaker");
//			try
//			{
//				Thread.sleep(5);
//			}
//			catch (InterruptedException e)
//			{
//				Thread.currentThread().interrupt();
//			}
//		}
//		this.clip.setMicrosecondPosition(0);
//		this.clip.start();
//		this.mute.setValue(false);
//		for (double i = 0; i < 100000.0; ++i)
//		{
//			double x = i*i;
//			double y = x*2;
//			x = y/2;
//		}
//		this.mute.setValue(true);
//		if (this.clip == null)
//		{
//			return;
//		}
//
//		synchronized (this.lock)
//		{
//			this.started = false;
//			this.stopped = false;
//		}
//
//
//		this.clip.setMicrosecondPosition(0);
//		this.clip.start();
//return;
//		synchronized (this.lock)
//		{
//			while (!(this.started && this.stopped))
//			{
//				try
//				{
//					this.lock.wait();
//				}
//				catch (InterruptedException e)
//				{
//					Thread.currentThread().interrupt();
//				}
//			}
//		}
	}

//	private void update(final LineEvent event)
//	{
//		final LineEvent.Type evt = event.getType();
//		
//		if (evt.equals(LineEvent.Type.START))
//		{
//			synchronized (this.lock)
//			{
//				this.started = true;
//				this.lock.notifyAll();
//			}
//		}
//		else if (evt.equals(LineEvent.Type.STOP))
//		{
//			synchronized (this.lock)
//			{
//				this.stopped = true;
//				this.lock.notifyAll();
//			}
//		}
//	}
}
