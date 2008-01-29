package keyboard;
/*
 * Created on Jan 29, 2008
 */
public class HyperMode
{
	private boolean hyper;

	public synchronized boolean isHyper()
	{
		return this.hyper;
	}

	public synchronized void setHyper(boolean isHyper)
	{
			this.hyper = isHyper;
	}

	public synchronized void toggleHyper()
	{
		this.hyper = !this.hyper;
	}

}
