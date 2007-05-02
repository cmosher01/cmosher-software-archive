/*
 * Created on July 21, 2005
 */
package com.surveysampling.build.servlet;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.surveysampling.build.servlet.util.BuildUtils;
import com.surveysampling.build.servlet.util.HeaderFooter;
import com.surveysampling.build.servlet.util.Menu;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class AskMakeJavadocServlet extends HttpServlet
{
    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException
    {
        try
        {
            tryGet(request,response);
        }
        catch (final Throwable e)
        {
            throw new ServletException(e);
        }
    }

    protected void tryGet(final HttpServletRequest request, final HttpServletResponse response)
    {
        final PrintWriter webPage = BuildUtils.getOutput(request,response);

        HeaderFooter.printHeader("Generate Javadoc",webPage);
        webPage.println("<body>");

        final String path = BuildUtils.getPath(request);

        Menu.printMenu(path,webPage);
        Menu.printPath(path,webPage);





        writeJavadocForm(path,webPage);





        webPage.println("</body>");
        HeaderFooter.printFooter(webPage);
    }

    protected void writeJavadocForm(final String path, final PrintWriter webPage)
    {
        webPage.println("<div class=\"buildWhat\">");
        webPage.println("<form method=\"POST\" action=\"ant\">");

        webPage.println("<p>");
        webPage.println("You can generate the API (Javadoc) documentation<br />");
        webPage.println("for the base directory shown above:");
        webPage.println("</p>");

        final File dirJavadoc = new File(BuildUtils.getTempDir(getServletContext()),"javadoc");
        webPage.println("<input type=\"hidden\" name=\"javadoc.base.dir\" value=\""+dirJavadoc.getAbsolutePath()+"\" />");
        webPage.println("<input type=\"hidden\" name=\"target\" value=\"javadoc\" />");
        webPage.println("<input type=\"hidden\" name=\"svn.base.path\" value=\""+path+"\" />");
        webPage.println("<input type=\"submit\" value=\"Generate Javadoc\" />");

        webPage.println("</form>");
        webPage.println("</div>");
    }
}
