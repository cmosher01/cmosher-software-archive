package keyboard;
/*
 * Created on Jan 29, 2008
 */
public class KeyboardBufferMode
{
	private boolean buffered;

	public synchronized boolean isBuffered()
	{
		return this.buffered;
	}

	public synchronized void setBuffered(final boolean buffered)
	{
		this.buffered = buffered;
	}

	public synchronized void toggleBuffered()
	{
		this.buffered = !this.buffered;
	}

}
