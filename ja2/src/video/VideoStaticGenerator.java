/*
 * Created on Jan 27, 2008
 */
package video;

import gui.UI;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import chipset.Throttle;
import chipset.TimingGeneratorAbstract;

public class VideoStaticGenerator extends TimingGeneratorAbstract
{
	private final VideoDisplayDevice tv;

	/**
	 * @param tv
	 * @param ui 
	 * @param throttle 
	 */
	public VideoStaticGenerator(final VideoDisplayDevice tv, final Throttle throttle)
	{
		super(throttle);
		this.tv = tv;
	}

	@Override
	protected void tick()
	{
		for (int i = 0; i < TimingGeneratorAbstract.CRYSTAL_CYCLES_PER_CPU_CYCLE; ++i)
			this.tv.putAsDisconnectedVideoIn();
	}
}
