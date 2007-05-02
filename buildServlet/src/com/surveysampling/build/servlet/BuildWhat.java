/*
 * Created on July 19, 2005
 */
package com.surveysampling.build.servlet;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tmatesoft.svn.core.SVNException;

import com.surveysampling.build.servlet.util.BuildUtils;
import com.surveysampling.build.servlet.util.HeaderFooter;
import com.surveysampling.build.servlet.util.Menu;
import com.surveysampling.build.servlet.util.SvnProjectFinder;

/**
 * Servlet to show the main home page, where the
 * user chooses which project to build or test.
 * 
 * @author Chris Mosher
 */
public class BuildWhat extends HttpServlet
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

    private void tryGet(final HttpServletRequest request, final HttpServletResponse response) throws SVNException
    {
        final PrintWriter webPage = BuildUtils.getOutput(request,response);

        final boolean bTest = BuildUtils.isTest(request);
        HeaderFooter.printHeader((bTest ? "Test" : "Build") + " an Application",webPage);
        webPage.println("<body>");

        final String path = BuildUtils.getPath(request);

        Menu.printMenu(path,webPage);
        Menu.printPath(path,webPage);





        writeProjectList(request,bTest,path,webPage);





        webPage.println("</body>");
        HeaderFooter.printFooter(webPage);
    }

    private void writeProjectList(final HttpServletRequest request, final boolean bTest, final String path, final PrintWriter webPage) throws SVNException
    {
        webPage.println("<div class=\"buildWhat\">");
        webPage.println("<form method=\"POST\" action=\"ant\">");

        webPage.println("<table>");
        webPage.println("<tbody>");

        final Collection<String> rProject = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        getProjectList(path,rProject);

        for (final String sProject : rProject)
        {
            final String sProjectName = filterProjectName(sProject);
            writeProject(sProjectName,webPage);
        }

        webPage.println("</tbody>");
        webPage.println("</table>");

        if (bTest)
        {
            webPage.println("<input type=\"hidden\" name=\"target\" value=\"test\" />");
        }
        webPage.println("<input type=\"hidden\" name=\"svn.base.path\" value=\""+path+"\" />");
        if (BuildUtils.isKeep(request))
        {
            webPage.println("<input type=\"hidden\" name=\"keep\" value=\"true\" />");
        }
        webPage.print("<input type=\"submit\" value=\"");
        webPage.print(bTest ? "Run JUnit tests" : "Build");
        webPage.println("\" />");

        webPage.println("</form>");
        webPage.println("</div>");
    }

    private String filterProjectName(final String project)
    {
        String ret = project;
        if (ret.endsWith("/"))
        {
            ret = ret.substring(0,ret.length()-1);
        }

        final int iLastSlash = ret.lastIndexOf('/');
        if (iLastSlash >= 0)
        {
            ret = ret.substring(iLastSlash+1);
        }

        return ret;
    }

    private static void writeProject(final String sProject, final PrintWriter webPage)
    {
        webPage.print("<tr><td>");

        webPage.print("<input type=\"radio\" name=\"build.app\" value=\""+sProject+"\" />");
        webPage.print(sProject);

        webPage.println("</td></tr>");
    }

    private void getProjectList(String path, final Collection<String> rProject) throws SVNException
    {
        final SvnProjectFinder finder = new SvnProjectFinder(BuildUtils.getReposBase()+"/"+BuildUtils.getReposName());
        finder.getProjects(path,rProject);
    }
}
