/*
 * Created on Feb 9, 2008
 */
package chipset;

import emu.Apple2;

public class PowerUpReset
{
	private int pendingTicks;
	private final Apple2 apple;

	public PowerUpReset(final Apple2 apple)
	{
		this.apple = apple;
	}

	public void tick()
	{
		if (this.pendingTicks > 0)
		{
			--this.pendingTicks;
			if (this.pendingTicks == 0)
			{
				this.apple.reset();
			}
		}
	}

	public void powerOn()
	{

		// TODO if rev > 0
		this.pendingTicks = (int)(TimingGenerator.AVG_CPU_HZ*.3); // U.A.II, p. 7-15
	}
}
