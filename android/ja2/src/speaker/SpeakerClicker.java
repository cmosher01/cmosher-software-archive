// TODO ANDROID: fix speaker sound generation
package speaker;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
//import javax.sound.sampled.AudioFormat;
//import javax.sound.sampled.AudioSystem;
//import javax.sound.sampled.DataLine;
//import javax.sound.sampled.LineUnavailableException;
//import javax.sound.sampled.SourceDataLine;
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

	private volatile AudioTrack line;
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
			@Override
			public void run()
			{
					feed();
			}
		});
		th.setDaemon(true);
		th.setName("User-SpeakerClicker");
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

	void feed()
	{
		final int szMin = AudioTrack.getMinBufferSize(SAMPLES_PER_SECOND, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT);
		this.line = new AudioTrack(AudioManager.STREAM_MUSIC,SAMPLES_PER_SECOND,AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT, szMin, AudioTrack.MODE_STREAM);
		this.line.play();
		
		this.running = true;
		try
		{
			Thread.sleep(100);
		}
		catch (InterruptedException e1)
		{
			Thread.currentThread().interrupt();
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
						this.click.wait(this.running ? 500: 0);
					}
					catch (InterruptedException e)
					{
						Thread.currentThread().interrupt();
					}
					cl = this.click.get();
					if (cl == 0 && this.running)
					{
//						this.line.stop();
						this.running = false;
					}
					else if (cl != 0 && !this.running)
					{
//						this.line.start();
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
