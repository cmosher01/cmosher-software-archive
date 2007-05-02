/*
 * Created on July 19, 2005
 */
package com.surveysampling.build.servlet.util;

import java.io.PrintWriter;

/**
 * Contains static methods to write the "menu"
 * and "current path" portions of each web page.
 * 
 * @author Chris Mosher
 */
public class Menu
{
    /**
     * Prints the menu (in an XHTML <code>div</code> element)
     * to the given response writer.
     * @param path
     * @param out
     */
    public static void printMenu(final String path, final PrintWriter out)
    {
        out.println("<div class=\"menu\">");

        out.print("<a href=\"build?svn.base.path="+path+"\">build</a>");
        out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
        out.print("<a href=\"build?test=true&amp;svn.base.path="+path+"\">test</a>");
        out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
        out.print("<a href=\"askmakejavadoc?svn.base.path="+path+"\">generate javadoc</a>");
        out.print("&nbsp;&nbsp;&nbsp;&nbsp;");
        out.print("<a href=\"javadoc/"+path+"/index.html\" target=\"_blank\">view javadoc</a>");

        out.println();

        out.println("</div>");
    }

    /**
     * Prints the current path and change link
     * (in an XHTML <code>div</code> element)
     * to the given response writer.
     * @param path
     * @param out
     */
    public static void printPath(final String path, final PrintWriter out)
    {
        out.println("<div class=\"path\">");
        out.print("current build path: <b>");
        out.print(path);
        out.println("</b>&nbsp;&nbsp;&nbsp;&nbsp;<a href=\"paths?svn.base.path="+path+"\">change path</a>");
        out.println("</div>");
    }
}
