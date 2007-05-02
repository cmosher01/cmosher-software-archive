/*
 * Created on Aug 10, 2005
 */
package com.surveysampling.util;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class HTTPHeaderParser
{
    private Collection<String> setValues = new HashSet<String>();

    /**
     * @param request
     * @param name
     */
    public HTTPHeaderParser(final HttpServletRequest request, final String name)
    {
        final Enumeration headers = request.getHeaders(name);
        while (headers.hasMoreElements())
        {
            final String values = (String)headers.nextElement();
            parseValues(values);
        }
    }

    private void parseValues(final String values)
    {
        final StringTokenizer st = new StringTokenizer(values,",");
        while (st.hasMoreTokens())
        {
            final String value = st.nextToken().trim();
            this.setValues.add(value);
        }
    }

    /**
     * Looks up a string to see if it is present in
     * any of the headers' values.
     * @param lookup string to look up
     * @return <code>true</code> if the header(s) value(s) contain the given string
     */
    public boolean contains(final String lookup)
    {
        return this.setValues.contains(lookup.trim());
    }
}
