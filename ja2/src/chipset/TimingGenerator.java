package chipset;

import java.util.concurrent.atomic.AtomicBoolean;
import paddle.PaddlesInterface;
import chipset.cpu.CPU6502;
import speaker.SpeakerClicker;
import util.Util;
import video.Video;



/*
 * Created on Aug 1, 2007
 */
public class TimingGenerator
{
	public static final int CRYSTAL_HZ = Util.divideRoundUp(315000000,22);
	public static final int CRYSTAL_CYCLES_PER_CPU_CYCLE = 14;
	public static final int EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE = 2;

	public static final int HORIZ_CYCLES = 65;
	public static final int AVG_CPU_HZ = (int)Math.rint(Math.round(((double)315000000*HORIZ_CYCLES)/(22*(CRYSTAL_CYCLES_PER_CPU_CYCLE*HORIZ_CYCLES+EXTRA_CRYSTAL_CYCLES_PER_CPU_LONG_CYCLE))));
	public static final int CPU_HZ = Util.divideRoundUp(CRYSTAL_HZ,CRYSTAL_CYCLES_PER_CPU_CYCLE);

	private final CPU6502 cpu;
	private final Video video;
	private final PaddlesInterface paddles;

	private final AtomicBoolean shutdown = new AtomicBoolean();
	private final Thread thread = new Thread(new Runnable()
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

	private final SpeakerClicker speaker;
	private final Throttle throttle;



	public TimingGenerator(final CPU6502 cpu, final Video video, final PaddlesInterface paddles, final SpeakerClicker speaker, final Throttle throttle)
	{
		this.cpu = cpu;
		this.video = video;
		this.paddles = paddles;
		this.speaker = speaker;
		this.throttle = throttle;
	}

	public void run()
	{
		this.thread.setName("User-TimingGenerator");
		this.thread.start();
	}

	private void threadProcedure()
	{
		while (!isShuttingDown())
		{
			this.cpu.tick();
			this.video.tick();
			this.paddles.tick();
			this.speaker.tick();
			this.throttle.tick();
		}
	}

	private boolean isShuttingDown()
	{
		synchronized (this.shutdown)
		{
			return this.shutdown.get();
		}
	}

	public void shutdown()
	{
		synchronized (this.shutdown)
		{
			this.shutdown.set(true);
		}

		try
		{
			this.thread.join();
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
		}
	}
}
