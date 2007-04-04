/*
 * Created on Nov 11, 2006
 */
package nu.mine.mosher.gedcom.servlet;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.StringTokenizer;
import javax.servlet.http.HttpServletRequest;

public class HTTPHeaderParser
{
    private Collection<String> setValues = new HashSet<String>();

    /**
     * @param request
     * @param name
     */
    public HTTPHeaderParser(final HttpServletRequest request, final String name)
    {
        final Enumeration<String> headers = request.getHeaders(name);
        while (headers.hasMoreElements())
        {
            final String values = headers.nextElement();
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
