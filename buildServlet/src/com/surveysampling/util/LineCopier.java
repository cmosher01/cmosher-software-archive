/*
 * Created on July 21, 2005
 */

package com.surveysampling.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Copies lines from a <code>Reader</code> to a <code>Writer</code>,
 * in a new thread.
 * 
 * @author Chris Mosher
 */
public class LineCopier
{
    private final BufferedReader in;
    private final BufferedWriter out;
    private final boolean autoFlush;
    private final Thread thread = new Thread(new Runnable()
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
    },
    getClass().getName());



    /**
     * Creates a new <code>LineCopier</code> and starts
     * its internal thread.
     * @param in <code>Reader</code> to read lines from
     * @param out <code>Writer</code> to write lines to
     * @param autoFlush if <code>true</code>, flushes <code>out</code>
     * after every new-line
     */
    public LineCopier(final Reader in, final Writer out, final boolean autoFlush)
    {
        this.in = new BufferedReader(in);
        this.out = new BufferedWriter(out);
        this.autoFlush = autoFlush;

        this.thread.start();
    }

    protected void doRun() throws IOException
    {
        for (String sLine = this.in.readLine(); sLine != null; sLine = this.in.readLine())
        {
            this.out.write(sLine);
            this.out.newLine();
            if (this.autoFlush)
            {
                this.out.flush();
            }
        }
    }

    /**
     * Waits for this <code>LineCopier</code>'s internal
     * thread to end. 
     */
    public void join()
    {
        try
        {
            this.thread.join();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
