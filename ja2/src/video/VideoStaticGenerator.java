/*
 * Created on Jan 27, 2008
 */
package video;

import chipset.Timable;
import chipset.TimingGenerator;

public class VideoStaticGenerator implements Timable
{
	private final VideoDisplayDevice tv;

	/**
	 * @param tv
	 * @param ui 
	 * @param throttle 
	 */
	public VideoStaticGenerator(final VideoDisplayDevice tv)
	{
		this.tv = tv;
	}

	public void tick()
	{
		for (int i = 0; i < TimingGenerator.CRYSTAL_CYCLES_PER_CPU_CYCLE; ++i)
			this.tv.putAsDisconnectedVideoIn();
	}
}
