package com.surveysampling.build.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.tmatesoft.svn.core.SVNException;

import com.surveysampling.build.servlet.util.BuildUtils;
import com.surveysampling.build.servlet.util.HeaderFooter;
import com.surveysampling.build.servlet.util.HtmlReportLogger;
import com.surveysampling.build.servlet.util.Menu;
import com.surveysampling.build.servlet.util.ServletLogger;
import com.surveysampling.util.LineCopier;
import com.surveysampling.util.ServletLogWriter;




/**
 * A servlet that runs an Ant build.
 * 
 * @author Chris Mosher
 */
public class AntServlet extends HttpServlet
{
    /**
     * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response)
    {
        final PrintWriter webPage = BuildUtils.getOutput(request,response);

        HeaderFooter.printHeader("Log file from Ant build",webPage);
        webPage.println("<body>");

        final String path = BuildUtils.getPath(request);

        Menu.printMenu(path,webPage);
        Menu.printPath(path,webPage);



        printEndOfPageLink(webPage);
        printTableHeader(webPage);

        runAntBuild(request,webPage);

        printTableFooter(webPage);
        printEndOfPageTarget(webPage);



        webPage.println("</body>");
        HeaderFooter.printFooter(webPage);
    }



    private void runAntBuild(final HttpServletRequest request, final PrintWriter webPage)
    {
        final Project project = new Project();
        Throwable error = null;

        try
        {
            setUpAndBuildProject(project,request,webPage);
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            error = e;
        }

        project.fireBuildFinished(error);
    }



    private static void printEndOfPageLink(final PrintWriter webPage)
    {
        webPage.println("<div class=\"jump\">");
        webPage.println("<a href=\"#endOfPage\">jump&nbsp;to&nbsp;end&nbsp;of&nbsp;page</a>");
        webPage.println("</div>");
    }

    private static void printEndOfPageTarget(final PrintWriter webPage)
    {
        webPage.println("<div class=\"jump\">");
        webPage.println("<a name=\"endOfPage\">&nbsp;</a>");
        webPage.println("</div>");
    }

    private static void printTableHeader(final PrintWriter webPage)
    {
        webPage.println("<div class=\"buildlog\">");
        webPage.println("<table>");
        webPage.println("<tbody>");
    }

    private static void printTableFooter(final PrintWriter webPage)
    {
        webPage.println("</tbody>");
        webPage.println("</table>");
        webPage.println("</div>");
    }



    private void setUpAndBuildProject(final Project project, final HttpServletRequest request, final PrintWriter webPage) throws IOException, BuildException, SVNException
    {
        final File dirBase = getBaseDirectory();
        try
        {
            setUpBuildEnvironment(dirBase);
            populateProjectProperties(request,project);
            final String target = BuildUtils.getTarget(request);
            addProjectListeners(project,webPage);
            project.fireBuildStarted();
            buildProject(dirBase,project,target);
        }
        finally
        {
            if (!BuildUtils.isKeep(request))
            {
                removeDirectory(dirBase);
            }
        }
    }



    private File getBaseDirectory()
    {
        final File dirTemp = BuildUtils.getTempDir(getServletContext());

        final Format fmtDateTime = new SimpleDateFormat("yyyyMMdd_HHmmss");
        File dirBase = new File(dirTemp,fmtDateTime.format(new Date()));
        while (!dirBase.mkdir())
        {
            sleepRandom();
            dirBase = new File(dirTemp,fmtDateTime.format(new Date()));
        }

        return dirBase;
    }

    private static void sleepRandom()
    {
        final int msRandom = new Random().nextInt(2000)+1;
        try
        {
            Thread.sleep(msRandom);
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }

    private void setUpBuildEnvironment(final File dirBase) throws IOException
    {
        exportBuildScripts(dirBase);
        updateBuildLibrary();
    }

    private void exportBuildScripts(final File dirBase) throws IOException
    {
        final String url =
            BuildUtils.getReposBase()+"/"+
            BuildUtils.getReposName()+
            "/build";
        final File dirDest = new File(dirBase,"build");

        execLogOutput("svn export --username build --password build --non-interactive --force "+url+" "+dirDest.getAbsolutePath());
    }

    private void updateBuildLibrary() throws IOException
    {
        final String url =
            BuildUtils.getReposBase()+"/"+
            BuildUtils.getVendorReposName()+
            "/buildlib";
        final File dirDest = new File(BuildUtils.getTempDir(getServletContext()),"buildlib");
        final String command = dirDest.exists() ? "update" : "checkout";

        execLogOutput("svn "+command+" --username build --password build --non-interactive "+url+" "+dirDest.getAbsolutePath());
    }

    private void execLogOutput(final String command) throws IOException
    {
        log(command);

        final Process proc = Runtime.getRuntime().exec(command);

        final LineCopier procOut = new LineCopier(new InputStreamReader(proc.getInputStream()),new ServletLogWriter(this),true);
        final LineCopier procErr = new LineCopier(new InputStreamReader(proc.getErrorStream()),new ServletLogWriter(this),true);



        try
        {
            proc.waitFor();
        }
        catch (final InterruptedException e)
        {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }

        procOut.join();
        procErr.join();
    }

    private static void buildProject(final File baseDir, final Project project, final String target)
        throws BuildException
    {
        project.init();

        final File buildFile = new File(baseDir,"build\\build.xml");

        project.setUserProperty("ant.file",buildFile.getAbsolutePath());
        project.setUserProperty("ant.version",Main.getAntVersion());

        project.setBaseDir(baseDir);

        ProjectHelper.configureProject(project,buildFile);

        project.executeTarget(target);
    }

    private void addProjectListeners(final Project project, final PrintWriter webPage)
    {
        project.addBuildListener(new ServletLogger(this));
        project.addBuildListener(new HtmlReportLogger(webPage));
    }

    /**
     * Collect all available information and store it into Ant's project properties
     * @param project
     * @param request
     * @throws SVNException 
     */
    private void populateProjectProperties(final HttpServletRequest request, final Project project) throws SVNException
    {
        // hard-wired properties
        {
            project.setUserProperty("svn.repos.base",BuildUtils.getReposBase());
            project.setUserProperty("svn.repos.name",BuildUtils.getReposName());
            project.setUserProperty("svn.repos.vendor.name",BuildUtils.getVendorReposName());
            project.setUserProperty("svn.repos.rev",Long.toString(BuildUtils.getLatestRevisionNumber()));
            project.setUserProperty("svn.base.path.filtered",filterFileName(BuildUtils.getPath(request)));
            project.setUserProperty("path.default",BuildUtils.getPathDefault());
        }

        // Servlet (parameters)
        {
            for (final Enumeration en = getInitParameterNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                project.setUserProperty(key,getInitParameter(key));
            }
        }

        // ServletContext (parameters and attributes)
        {
            final ServletContext ctx = getServletContext();
            for (final Enumeration en = ctx.getInitParameterNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                project.setUserProperty(key,ctx.getInitParameter(key));
            }
            for (final Enumeration en = ctx.getAttributeNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                final Object atr = ctx.getAttribute(key);
                if (atr != null)
                {
                    project.setUserProperty(key,atr.toString());
                }
            }
        }

        // HttpSession (attributes)
        {
            final HttpSession session = request.getSession(false);
            if (session != null)
            {
                project.setUserProperty("session.id","" + session.getId().hashCode());
                for (final Enumeration en = session.getAttributeNames(); en.hasMoreElements();)
                {
                    final String key = (String)en.nextElement();
                    final Object atr = session.getAttribute(key);
                    if (atr != null)
                    {
                        project.setUserProperty(key,atr.toString());
                    }
                }
            }
        }

        // ServletRequest (parameters)
        {
            for (final Enumeration en = request.getParameterNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                project.setProperty(key,request.getParameter(key));
            }
        }

        // HttpServletRequest (headers and attributes)
        {
            for (final Enumeration en = request.getHeaderNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                project.setProperty(key,request.getHeader(key));
            }
            for (final Enumeration en = request.getAttributeNames(); en.hasMoreElements();)
            {
                final String key = (String)en.nextElement();
                final Object atr = request.getAttribute(key);
                if (atr != null)
                {
                    project.setUserProperty(key,atr.toString());
                }
            }
        }
    }

    private static String filterFileName(final String s)
    {
        // replace all non-word characters with an underscore
        return s.replaceAll("\\W","_");
    }

    private static void removeDirectory(final File dir)
    {
        final String[] children = dir.list();
        if (children != null)
        {
            for (final String child : children)
            {
                removeDirectory(new File(dir,child));
            }
        }
        dir.delete();
    }
}

