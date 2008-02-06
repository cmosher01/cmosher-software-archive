/*
 * Created on Jan 27, 2008
 */
package video;

import chipset.Timable;
import chipset.TimingGenerator;

public class VideoStaticGenerator implements Timable
{
	private final Object lock = new Object();
	private VideoDisplayDevice tv;

	public void tick()
	{
		for (int i = 0; i < TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE; ++i)
		{
			VideoDisplayDevice tvlocal;
			synchronized (lock)
			{
				tvlocal = this.tv;
			}
			if (tvlocal != null)
				tvlocal.putAsDisconnectedVideoIn();
		}
	}

	public void setDisplay(VideoDisplayDevice display)
	{
		synchronized (lock)
		{
			this.tv = display;
		}
	}
}
