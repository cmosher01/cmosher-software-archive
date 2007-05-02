/*
 * Created on July 25, 2005
 */
package com.surveysampling.util;

/**
 * Contains static methods dealing with XML.
 * 
 * @author Chris Mosher
 */
public final class XMLUtil
{
    /**
     * @param s
     * @return <code>s</code>string filtered for use in an XML document
     */
    public static String filterForXML(final String s)
    {
        return s
            .replaceAll("&","&amp;")
            .replaceAll("<","&lt;")
            .replaceAll(">","&gt;")
            .replaceAll("\"","&quot;");
    }

    private XMLUtil()
    {
        assert false;
    }
}
