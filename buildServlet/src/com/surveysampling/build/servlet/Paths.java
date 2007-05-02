/*
 * Created on Jul 20, 2005
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
import com.surveysampling.build.servlet.util.SvnBranchFinder;



/**
 * TODO
 * 
 * @author Chris Mosher
 */
public class Paths extends HttpServlet
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

        HeaderFooter.printHeader("Build (or Test) an Application",webPage);
        webPage.println("<body>");

        final String path = BuildUtils.getPath(request);

        Menu.printMenu(path,webPage);
        Menu.printPath(path,webPage);





        writePathList(webPage);





        webPage.println("</body>");

        HeaderFooter.printFooter(webPage);
    }

    private void writePathList(final PrintWriter webPage) throws SVNException
    {
        webPage.println("<div class=\"choosepath\">");
        webPage.println("<table>");
        webPage.println("<caption>Please&nbsp;choose&nbsp;a&nbsp;base&nbsp;directory&nbsp;to&nbsp;build:</caption>");
        webPage.println("<tbody>");

        final Collection<String> rBranch = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        getBranchList(rBranch);

        for (final String sBranch : rBranch)
        {
            final String sPath = formatBranch(sBranch);
            writeBranch(sPath,webPage);
        }

        webPage.println("</tbody>");
        webPage.println("</table>");

        webPage.println("</div>");
    }

    private String formatBranch(final String branch)
    {
        String ret = branch;
        if (ret.startsWith("/"))
        {
            ret = ret.substring(1);
        }
        if (ret.endsWith("/"))
        {
            ret = ret.substring(0,ret.length()-1);
        }
        return ret;
    }

    private static void writeBranch(final String path, final PrintWriter webPage)
    {
        webPage.print("<tr><td>");
        webPage.println("<a href=\"build?svn.base.path="+path+"\">");
        webPage.println(path);
        webPage.println("</a>");
        webPage.println("</td></tr>");
    }

    private static void getBranchList(final Collection<String> rBranch) throws SVNException
    {
        final SvnBranchFinder finder = new SvnBranchFinder(BuildUtils.getReposBase()+"/"+BuildUtils.getReposName());
        finder.getBranches(rBranch);
    }
}
