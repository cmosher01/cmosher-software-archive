/*
 * Created on July 20, 2005
 */
package com.surveysampling.util;

import java.io.IOException;
import java.io.Writer;


/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class XMLFilterWriter extends Writer
{
    private final Writer out;

    /**
     * @param out
     * 
     */
    public XMLFilterWriter(final Writer out)
    {
        this.out = out;
    }

    /**
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException
    {
        final String s = new String(cbuf,off,len);

        this.out.write(XMLUtil.filterForXML(s));
    }

    /**
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException
    {
        this.out.flush();
    }

    /**
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException
    {
        this.out.close();
    }
}
