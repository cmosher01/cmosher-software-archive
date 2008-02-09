/*
 * Created on Dec 1, 2007
 */
package cards.stdio;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import util.Util;
import keyboard.KeypressQueue;

public class StandardInProducer
{
	private static final int CR = '\r';
	private static final int LF = '\n';

	private final KeypressQueue keys;
	private final Thread th;

	public StandardInProducer(final KeypressQueue keys)
	{
		this.keys = keys;
		this.th = new Thread(new Runnable()
		{
			@SuppressWarnings("synthetic-access")
			public void run()
			{
				readInput();
			}
		});
		this.th.setName("User-StandardInProducer");
		this.th.setDaemon(true);
		this.th.start();
	}

	private void readInput()
	{
		try
		{
			tryReadInput();
		}
		catch (final Throwable t)
		{
			t.printStackTrace();
		}
	}

	private static enum state_t { START, GOT_CR, GOT_LF, GOT_EOF }

	private void tryReadInput() throws IOException
	{
		final InputStream in = new FileInputStream(FileDescriptor.in);
		while (in.available() == 0)
		{
			try
			{
				Thread.sleep(250);
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}

		/*
		 * Continuously read characters from standard in
		 * and put them onto the queue.
		 * Stop when we hit EOF (placing EOF onto the queue).
		 * Translate LF to CR.
		 * If we hit a CR immediately after a LF, drop it.
		 * If we hit a LF immediately after a CR, drop it.
		 */
		state_t state = state_t.START;

		while (state != state_t.GOT_EOF)
		{
			int c = in.read();
			if (c == Util.EOF)
			{
				state = state_t.GOT_EOF;
				this.keys.putEOF();
			}
			else
			{
				if (state == state_t.START)
				{
					if (c == CR)
					{
						state = state_t.GOT_CR;
					}
					else if (c == LF)
					{
						state = state_t.GOT_LF;
						c = CR;
					}
					this.keys.put(c);
				}
				else if (state == state_t.GOT_CR)
				{
					if (c != LF)
					{
						this.keys.put(c);
					}
					state = state_t.START;
				}
				else if (state == state_t.GOT_LF)
				{
					if (c != CR)
					{
						if (c == LF)
						{
							c = CR;
						}
						this.keys.put(c);
					}
					state = state_t.START;
				}
			}
		}
	}
}
