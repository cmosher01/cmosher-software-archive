/*
 * Created on Dec 1, 2007
 */
package stdio;

import java.io.InputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class StandardInProducer
{
	private static final int CR = '\r';
	private static final int LF = '\n';
	private static final int EOF = -1;

	private final BlockingQueue<Integer> q;
	private final InputStream in;
	private final Thread th;

	public StandardInProducer(final BlockingQueue<Integer> q)
	{
		this.q = q;
		this.in = new FileInputStream(FileDescriptor.in);
		this.th = new Thread(new Runnable()
		{
			public void run()
			{
				readInput();
			}
		});
		this.th.setDaemon(true);
		this.th.start();
	}

	void readInput()
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

	private static enum state_t { START, GOT_CR, GOT_LF, GOT_EOF };

	void tryReadInput() throws IOException, InterruptedException
	{
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
			int c = this.in.read();
			if (c == EOF)
			{
				state = state_t.GOT_EOF;
				put(c);
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
					put(c);
				}
				else if (state == state_t.GOT_CR)
				{
					if (c != LF)
					{
						put(c);
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
						put(c);
					}
					state = state_t.START;
				}
			}
		}
	}

	void put(final int c) throws InterruptedException
	{
		this.q.put(c | 0x80);
	}
}
