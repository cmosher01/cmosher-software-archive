/*
 * Created on May 14, 2004
 */
package com.surveysampling.bulkemailer.util;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Provides static functions related to HTTP.
 * @author Chris Mosher
 */
public final class HttpUtil
{
    private HttpUtil() throws UnsupportedOperationException
    {
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * Parses an HTTP query string. The string is assumed to be of the form:
     * </p>
     * <code><pre>    [param1 = [value1] [ & param2 = [value2] [...] ] ]</pre></code>
     * <p>
     * Does not handle any escaped characters (like <code>%20</code>).
     * </p>
     * <p>
     * For multiple occurrences of the same param, only the
     * last occurrence will be returned.
     * </p>
     * 
     * @param query the query string
     * @param mapParamToValue map (of String to String) to add
     * the results to
     */
    public static void parseQueryStringSimple(String query, Map<String,String> mapParamToValue)
    {
        StringTokenizer st = new StringTokenizer(query, "&");
        while (st.hasMoreTokens())
        {
            StringTokenizer st2 = new StringTokenizer(st.nextToken(), "=");

            String param;
            if (st2.hasMoreTokens())
            {
                param = st2.nextToken();
            }
            else
            {
                param = "";
            }

            String value;
            if (st2.hasMoreTokens())
            {
                value = st2.nextToken();
            }
            else
            {
                value = "";
            }

            mapParamToValue.put(param, value);
        }
    }
}
