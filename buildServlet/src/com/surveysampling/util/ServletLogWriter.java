/*
 * Created on July 20, 2005
 */
package com.surveysampling.util;

import java.io.Writer;

import javax.servlet.GenericServlet;

/**
 * A <code>Writer</code> that writes to a given
 * <code>Servlet</code>'s log.
 * 
 * @author Chris Mosher
 */
public class ServletLogWriter extends Writer
{
    private final GenericServlet servlet;

    /**
     * Initializes this <code>ServletLogWriter</code> to
     * write to the given servlet's log.
     * @param servlet
     */
    public ServletLogWriter(final GenericServlet servlet)
    {
        this.servlet = servlet;
    }

    /**
     * Writes the given <code>char[]</code> to
     * the servlet's log.
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len)
    {
        this.servlet.log(new String(cbuf,off,len));
    }

    /**
     * Does nothing.
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush()
    {
        // do nothing
    }

    /**
     * Does nothing.
     * @see java.io.Writer#close()
     */
    @Override
    public void close()
    {
        // do nothing
    }
}
