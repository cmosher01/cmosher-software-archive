package com.surveysampling.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LineDumper
{
    private final BufferedReader in;
    private final PrintWriter out;
    private final Logger log;
    private final Level logginglevel;
    private final Thread thread = new Thread(new Runnable()
    {
        public void run()
        {
            doRun();
        }
    },
    getClass().getName());



    public LineDumper(Reader in)
    {
        this(in,new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));
    }

    public LineDumper(Reader in, Writer out)
    {
        if (in instanceof BufferedReader)
        {
            this.in = (BufferedReader)in;
        }
        else
        {
            this.in = new BufferedReader(in);
        }

        if (out instanceof PrintWriter)
        {
            this.out = (PrintWriter)out;
        }
        else if (out instanceof BufferedWriter)
        {
            this.out = new PrintWriter((BufferedWriter)out);
        }
        else
        {
            this.out = new PrintWriter(new BufferedWriter(out));
        }

        this.log = null;
        this.logginglevel = null;

        thread.start();
    }

    public LineDumper(Reader in, Logger log, Level logginglevel)
    {
        if (in instanceof BufferedReader)
        {
            this.in = (BufferedReader)in;
        }
        else
        {
            this.in = new BufferedReader(in);
        }

        this.out = null;

        this.log = log;
        this.logginglevel = logginglevel;

        thread.start();
    }

    protected void doRun()
    {
        try
        {
            for (String s = in.readLine(); s != null; s = in.readLine())
            {
                if (out != null)
                {
                    out.println(s);
                    out.flush();
                }
                if (log != null)
                {
                    log.log(logginglevel,s);
                }
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public void waitFor()
    {
        try
        {
            thread.join();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }
}
