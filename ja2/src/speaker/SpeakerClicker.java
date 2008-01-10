package speaker;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
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
	private static final double SAMPLES_PER_TICK = 44100.0/1020484.0;


	private volatile int t;

	private volatile SourceDataLine line;
	private volatile byte[] pcm = new byte[44100];

	private AtomicInteger click = new AtomicInteger();
	private int prevClick;
	private boolean pos;



	public SpeakerClicker()
	{
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
		final AudioFormat fmt = new AudioFormat(44100,8,1,true,false);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class,fmt);
		if (!AudioSystem.isLineSupported(info))
		{
			throw new LineUnavailableException("audio line not supported");
		}

		this.line = (SourceDataLine)AudioSystem.getLine(info);

		this.line.open(fmt,44100);
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

	void feed()
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
		while (true)
		{
			int cl;
			synchronized (this.click)
			{
				cl = this.click.get();
				while (cl == 0)
				{
					try
					{
						this.click.wait();
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
					cl = this.click.get();
				}
			}
			final int deltaTicks = cl - this.prevClick;
			this.prevClick = cl;
			synchronized (this.click)
			{
				this.click.set(0);
			}

			final int deltaSamples = (int)(Math.rint(Math.round(SAMPLES_PER_TICK*deltaTicks))+.0001);
			if (deltaSamples < this.pcm.length-164)
			{
				this.pcm[deltaSamples-1] = this.pos ? (byte)120 : (byte)-120;
				this.pcm[deltaSamples] = this.pos ? (byte)120 : (byte)-120;

				this.line.write(this.pcm,0,deltaSamples+1);

				this.pcm[deltaSamples-1] = 0;
				this.pcm[deltaSamples] = 0;
	
				this.pos = !this.pos;
			}
		}
	}

	public void click()
	{
		synchronized (this.click)
		{
			this.click.set(this.t);
			this.click.notifyAll();
		}
	}

	public void tick()
	{
		++this.t;
	}
}
