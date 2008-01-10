package speaker;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import chipset.TimingGenerator;

/*
 * Created on Jan 8, 2008
 */
public class SpeakerClicker
{
	private static final int SAMPLES_PER_SECOND = 44100;
	private static final double SAMPLES_PER_TICK = (double)SAMPLES_PER_SECOND/(double)TimingGenerator.AVG_CPU_HZ;
	private static final byte AMPLITUDE = 126;


	private volatile int t;

	private volatile SourceDataLine line;
	private volatile byte[] pcm = new byte[SAMPLES_PER_SECOND];

	private AtomicInteger click = new AtomicInteger();
	private int prevClick;
	private boolean pos;
	private AtomicBoolean started = new AtomicBoolean();
	private boolean running;



	public SpeakerClicker()
	{
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					feed();
				}
				catch (LineUnavailableException e)
				{
					e.printStackTrace();
					synchronized (SpeakerClicker.this.started)
					{
						SpeakerClicker.this.started.set(true);
						SpeakerClicker.this.started.notifyAll();
					}
				}
			}
		});
		th.setDaemon(true);
		th.setName("Ja2-SpeakerClicker");
		th.start();
		synchronized (this.started)
		{
			while (!this.started.get())
			{
				try
				{
					this.started.wait();
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
				}
			}
		}
	}

	void feed() throws LineUnavailableException
	{
		final AudioFormat fmt = new AudioFormat(SAMPLES_PER_SECOND,Byte.SIZE,1,true,false);
		final DataLine.Info info = new DataLine.Info(SourceDataLine.class,fmt);
		if (!AudioSystem.isLineSupported(info))
		{
			throw new LineUnavailableException("audio line not supported");
		}

		this.line = (SourceDataLine)AudioSystem.getLine(info);

		this.line.open(fmt,SAMPLES_PER_SECOND);
		this.line.start();
		this.running = true;
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e1)
		{
			e1.printStackTrace();
		}

		synchronized (this.started)
		{
			this.started.set(true);
			this.started.notifyAll();
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
						this.click.wait(this.running ? 1000: 0);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
					cl = this.click.get();
					if (cl == 0 && this.running)
					{
						this.line.stop();
						this.running = false;
					}
					else if (cl != 0 && !this.running)
					{
						this.line.start();
						this.running = true;
					}
				}
			}
			final int deltaTicks = cl - this.prevClick;
			this.prevClick = cl;
			synchronized (this.click)
			{
				this.click.set(0);
			}

			final int deltaSamples = (int)(Math.rint(Math.round(SAMPLES_PER_TICK*deltaTicks))+.0001);

			if (0 < deltaSamples && deltaSamples < this.pcm.length)
			{
				this.pcm[deltaSamples-1] = this.pos ? AMPLITUDE : -AMPLITUDE;
				this.pcm[deltaSamples] = this.pos ? AMPLITUDE : -AMPLITUDE;

				this.line.write(this.pcm,0,deltaSamples);

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
		if (this.t > 0x70000000)
		{
			this.t = 0;
		}
	}
}
