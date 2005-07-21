/*
 * Created on July 18, 2005
 */
package nu.mine.mosher.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Reads the entire stream from a <code>Reader</code> and
 * dumps it to a <code>Writer</code>, in a new thread.
 *
 * @author Chris Mosher
 */
public class ReaderDumper
{
	private final BufferedReader in;
	private final BufferedWriter out;

	private final Thread thread;



    /**
     * @param in
     * @param out
     */
    public ReaderDumper(final Reader in, final Writer out)
    {
    	this(in,out,Executors.defaultThreadFactory());
    }

    /**
     * @param in
     * @param out 
	 * @param factoryThread 
     */
    public ReaderDumper(final Reader in, final Writer out, final ThreadFactory factoryThread)
    {
    	this.in = new BufferedReader(in);
    	this.out = new BufferedWriter(out);

    	this.thread = factoryThread.newThread(new Runnable()
		{
			public void run()
			{
				try
				{
					doRun();
				}
				catch (final Throwable e)
				{
					e.printStackTrace();
				}
			}
		});
    	this.thread.start();
    }

    /**
     * @throws IOException
     */
	protected void doRun() throws IOException
    {
    	for (String s = this.in.readLine(); s != null; s = this.in.readLine())
    	{
    		this.out.write(s);
    		this.out.newLine();
    	}
    }

	public void join()
	{
		try
		{
			this.thread.join();
		}
		catch (final InterruptedException e)
		{
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
}
