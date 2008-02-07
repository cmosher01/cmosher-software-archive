/*
 * Created on Jan 27, 2008
 */
package video;

import chipset.Timable;
import chipset.TimingGenerator;

public class VideoStaticGenerator implements Timable
{
	private final Object lock = new Object();
	private final VideoDisplayDevice tv;

	public VideoStaticGenerator(final VideoDisplayDevice tv)
	{
		this.tv = tv;
	}

	public void tick()
	{
		for (int i = 0; i < TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE; ++i)
		{
			this.tv.putAsDisconnectedVideoIn();
		}
	}
}
