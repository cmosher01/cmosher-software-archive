/*
 * Created on Dec 2, 2007
 */
package paddle;

import java.util.concurrent.atomic.AtomicBoolean;

public class PaddleButtonStates implements PaddleBtnInterface
{
	private static final int PADDLE_COUNT = 4;
	private AtomicBoolean[] button = new AtomicBoolean[PADDLE_COUNT];

	public PaddleButtonStates()
	{
		for (int i = 0; i < this.button.length; ++i)
		{
			this.button[i] = new AtomicBoolean();
		}
	}

	void setButton(final int btn, final boolean pressed)
	{
		this.button[btn].set(pressed);
	}

	public boolean isDown(final int paddle)
	{
		if (paddle < 0 || PADDLE_COUNT <= paddle)
		{
			return false;
		}
		return this.button[paddle].get();
	}
}
