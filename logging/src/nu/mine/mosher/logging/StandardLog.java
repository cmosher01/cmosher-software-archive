/*
 * Created on May 20, 2004
 */
package nu.mine.mosher.logging;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A substitute PrintStream that takes items
 * printed to it and sends them to a Logger.
 */
public abstract class StandardLog extends PrintStream
{
    protected static final String CR = "\r";
    protected static final String LF = "\n";

    protected final Logger log;
    protected final StringBuffer buf = new StringBuffer(1024);



    protected static class StandardOutput extends StandardLog
    {
        public StandardOutput(Logger log)
        {
            super(log);
        }
        protected void log(String s)
        {
            this.log.log(Level.INFO,s);
        }
    }
    protected static class StandardError extends StandardLog
    {
        public StandardError(Logger log)
        {
            super(log);
        }
        protected void log(String s)
        {
            this.log.log(Level.WARNING,s);
        }
    }



    /**
     * Reassigns the standard error stream so
     * that anything sent to it gets logged as
     * warning messages to the given Logger.
     * @param log the Logger to send stderr to
     */
    public static void setErr(Logger log)
    {
        System.setErr(new StandardError(log));
    }

    /**
     * Reassigns the standard output stream so
     * that anything sent to it gets logged as
     * information messages to the given Logger.
     * @param log the Logger to send stdout to
     */
    public static void setOut(Logger log)
    {
        System.setOut(new StandardOutput(log));
    }



    protected StandardLog(Logger log)
    {
        super(new OutputStream()
        {
            /*
             * PrintStream needs an OutputStream,
             * but we will never use it, because
             * we send everything to the Java
             * logging APi instead.
             */
            public void write(int b)
            {
                throw new UnsupportedOperationException();
            }
        }
        );

        this.log = log;
    }



    protected void setError()
    {
    }

    public boolean checkError()
    {
        return false;
    }

    public void close()
    {
    }

    public synchronized void flush()
    {
        if (buf.charAt(buf.length()-1) != LF.charAt(0))
        {
            buf.append(LF);
        }
        checkNewline();
    }



    // these methods never append a newline

    public synchronized void print(boolean x)
    {
        buf.append(x);
    }

    public synchronized void print(int x)
    {
        buf.append(x);
    }

    public synchronized void print(long x)
    {
        buf.append(x);
    }

    public synchronized void print(float x)
    {
        buf.append(x);
    }

    public synchronized void print(double x)
    {
        buf.append(x);
    }



    // these methods may append a newline

    public synchronized void print(char x)
    {
        buf.append(x);
        checkNewline();
    }

    public synchronized void print(char[] x)
    {
        buf.append(x);
        checkNewline();
    }

    public synchronized void print(Object x)
    {
        buf.append(x);
        checkNewline();
    }

    public synchronized void print(String x)
    {
        buf.append(x);
        checkNewline();
    }



    // these methods always append a newline

    public synchronized void println()
    {
        newline();
    }

    public synchronized void println(boolean x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(int x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(long x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(float x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(double x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(char x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(char[] x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(Object x)
    {
        buf.append(x);
        newline();
    }

    public synchronized void println(String x)
    {
        buf.append(x);
        newline();
    }



    // these should be deprecated; they don't translate
    // bytes to chars properly.

    public synchronized void write(byte[] x)
    {
        write(x,0,x.length);
    }

    public synchronized void write(byte[] x, int off, int len)
    {
        char[] rc = new char[len];
        for (int i = 0; i < x.length; ++i)
        {
            rc[i] = (char)x[i];
        }
        buf.append(rc);
    }

    public synchronized void write(int i)
    {
        print((char)i);
    }





    /**
     * Appends a newline character to
     * the buffer, and calls
     *
     */
    protected void newline()
    {
        buf.append(LF);
        checkNewline();
    }

    /**
     * Checks the beginning of the buffer to
     * see if it contains lines (terminated
     * by CR, LF, or CR/LF) to be written,
     * and if so writes them to the log.
     */
    protected void checkNewline()
    {
        while (logOneLine())
        {
        }
    }

    /**
     * Checks the beginning of the buffer to
     * see if it contains one line (terminated
     * by CR, LF, or CR/LF) to be written,
     * and if so writes it to the log.
     */
    protected boolean logOneLine()
    {
        int cr = buf.indexOf(CR);
        int lf = buf.indexOf(LF);
        if (cr >= 0 && lf >= 0)
        {
            int first = Math.min(cr, lf);
            if (first == cr)
            {
                int skip = 1;
                if (lf == cr+1)
                {
                    ++skip;
                }
                logLine(cr,skip);
            }
            else if (first == lf)
            {
                logLine(lf,1);
            }
        }
        else if (cr >= 0)
        {
            int skip = 1;
            if (lf == cr+1)
            {
                ++skip;
            }
            logLine(cr,skip);
        }
        else if (lf >= 0)
        {
            logLine(lf,1);
        }
        return (cr >= 0 || lf >= 0);
    }

    /**
     * Writes a line to the log, taken from
     * the beginning of the buffer up to
     * position i (exclusive), then deletes
     * the line (including the termination
     * sequence of length skip) from the buffer.
     * @param i end of line
     * @param skip length of terminator
     */
    protected void logLine(int i, int skip)
    {
        log(buf.substring(0,i));
        buf.delete(0,i+skip);
    }

    /**
     * Writes the given string to the log
     * @param s
     */
    protected abstract void log(String s);
}
