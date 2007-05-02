/*
 * Created on Jul 19, 2005
 */
package com.surveysampling.build.servlet.util;

import java.io.PrintWriter;

/**
 * Contains static methods to write the
 * header and footer of a web page.
 * 
 * @author Chris Mosher
 */
public class HeaderFooter
{
    /**
     * @param out
     * @param title
     */
    public static void printHeader(final String title, final PrintWriter out)
    {
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">"); 
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        out.println("<head>");
        out.println("<title>");
        out.println(title);
        out.println("</title>");
        out.println("<link rel=\"stylesheet\" href=\"default.css\" />");
        out.println("</head>");
    }

    /**
     * @param out
     */
    public static void printFooter(final PrintWriter out)
    {
        out.println("</html>");
    }
}
