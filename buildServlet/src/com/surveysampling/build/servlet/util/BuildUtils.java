/*
 * Created on July 19, 2005
 */
package com.surveysampling.build.servlet.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tmatesoft.svn.core.SVNException;

import com.surveysampling.util.HTTPHeaderParser;

/**
 * TODO
 * 
 * @author Chris Mosher
 */
public final class BuildUtils
{
    private BuildUtils()
    {
        assert false;
    }



    private static final String DEFAULT_REPOS_BASE = "svn://127.0.0.1/";

    private static final String reposBase = initReposBase();
    private static final String reposName;
    private static final String vendorReposName;
    private static final String defaultPath;
    private static final String defaultTarget;
    static
    {
        try
        {
            final Properties props = new Properties();
            props.load(BuildUtils.class.getResourceAsStream("BuildUtils.properties"));
            reposName = props.getProperty("reposName");
            vendorReposName = props.getProperty("vendorReposName");
            defaultPath = props.getProperty("defaultPath");
            defaultTarget = props.getProperty("defaultTarget");
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static String initReposBase()
    {
        final String loadedReposBase = lookupReposBase();
        final String slashedReposBase = loadedReposBase.replace('\\','/');
        if (slashedReposBase.endsWith("/"))
        {
            return slashedReposBase.substring(0,slashedReposBase.length()-1);
        }
        return slashedReposBase;
    }

    /**
     * Looks up the environment variable "reposBase" and returns
     * its value, or a default of "svn://127.0.0.1/" if some error
     * occurs during the look-up process.
     * This value can be specified as follows:
     * <pre>
     * <Environment name="reposBase" value="svn://buildServerBox/" type="java.lang.String" />
     * </pre>
     * under the Context element in conf/context.xml file, in Tomcat 5.5.
     * @return repository base URL
     */
    private static String lookupReposBase()
    {
        try
        {
            final Context jndi = new InitialContext();
            return (String)jndi.lookup("java:comp/env/reposBase");
        }
        catch (final Throwable e)
        {
            e.printStackTrace();
            System.err.println("using default subversion URL base: "+DEFAULT_REPOS_BASE);
            return DEFAULT_REPOS_BASE;
        }
    }

    /**
     * Character encoding to use for the response we
     * send back to the browser.
     */
    public static final String RESPONSE_CHAR_ENCODING = "UTF-8";
    private static final boolean AUTO_FLUSH = true;

    /**
     * @param request 
     * @param response
     * @return response writer
     * @throws RuntimeException
     */
    public static PrintWriter getOutput(final HttpServletRequest request, final HttpServletResponse response) throws RuntimeException
    {
        final boolean isCompliant = checkBrowserCompliance(request);

        if (isCompliant)
        {
            response.setContentType("application/xhtml+xml; charset=UTF-8");
        }
        else
        {
            response.setContentType("text/html; charset=UTF-8");
        }

        try
        {
            final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),
                RESPONSE_CHAR_ENCODING)),AUTO_FLUSH);

            if (isCompliant)
            {
                out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            }

            return out;
        }
        catch (final IOException e)
        {
            // Hmmm... no way to respond to the user?
            // I guess we really can't do too much, then.
            // So just call it a programming error.
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * @param response
     * @return PrintWriter
     * @throws RuntimeException
     */
    public static PrintWriter getOutputAsHTML(final HttpServletResponse response) throws RuntimeException
    {
        response.setContentType("text/html; charset=UTF-8");

        try
        {
            return new PrintWriter(new BufferedWriter(new OutputStreamWriter(response.getOutputStream(),
                RESPONSE_CHAR_ENCODING)),AUTO_FLUSH);
        }
        catch (final IOException e)
        {
            // Hmmm... no way to respond to the user?
            // I guess we really can't do too much, then.
            // So just call it a programming error.
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static boolean checkBrowserCompliance(final HttpServletRequest request)
    {
        final HTTPHeaderParser headerParser = new HTTPHeaderParser(request,"accept");
        return headerParser.contains("application/xhtml+xml");
    }

    /**
     * @param request
     * @return path with repository to base directory of applications
     */
    public static String getPath(final HttpServletRequest request)
    {
        String path = request.getParameter("svn.base.path");
        if (path == null || path.length() == 0)
        {
            path = BuildUtils.defaultPath;
        }
        return path;
    }

    /**
     * Gets the default path.
     * @return the default path
     */
    public static String getPathDefault()
    {
        return BuildUtils.defaultPath;
    }

    /**
     * @param request
     * @return target within ant build.xml script to build
     */
    public static String getTarget(final HttpServletRequest request)
    {
        String target = request.getParameter("target");
        if (target == null || target.length() == 0)
        {
            target = BuildUtils.defaultTarget;
        }
        return target;
    }

    /**
     * @param request
     * @return <code>true</code> if test (as opposed to build) is requested
     */
    public static boolean isTest(HttpServletRequest request)
    {
        final String test = request.getParameter("test");
        if (test == null || test.length() == 0)
        {
            return false;
        }

        return test.equalsIgnoreCase("true");
    }

    /**
     * @param request
     * @return <code>true</code> if test (as opposed to build) is requested
     */
    public static boolean isKeep(HttpServletRequest request)
    {
        final String test = request.getParameter("keep");
        if (test == null || test.length() == 0)
        {
            return false;
        }

        return test.equalsIgnoreCase("true");
    }

    /**
     * An absolute, canonical, file representing the web application's
     * temporary directory, for the given servlet context.
     * @param servletContext
     * @return <code>File</code> representing web app's temporary directory
     */
    public static File getTempDir(final ServletContext servletContext)
    {
        try
        {
            final File dirTemp = (File)servletContext.getAttribute("javax.servlet.context.tempdir");
            final File dir = dirTemp.getAbsoluteFile().getCanonicalFile();

            if (!dir.isDirectory())
            {
                throw new IOException("Not a directory.");
            }

            return dir;
        }
        catch (final IOException e)
        {
            throw new RuntimeException("Unable to find web application's temporary directory.",e);
        }
    }

    /**
     * @return Returns the URL of the directory that contains the Subversion repositories.
     */
    public static String getReposBase()
    {
        return BuildUtils.reposBase;
    }

    /**
     * @return Returns the SVN_REPOS.
     */
    public static String getReposName()
    {
        return BuildUtils.reposName;
    }

    /**
     * @return Returns the SVN_REPOS_VENDOR.
     */
    public static String getVendorReposName()
    {
        return BuildUtils.vendorReposName;
    }

    /**
     * @return returns the latest revision number of the repository
     * @throws SVNException
     */
    public static long getLatestRevisionNumber() throws SVNException
    {
        final SvnWrapper svn = new SvnWrapper(BuildUtils.getReposBase()+"/"+BuildUtils.getReposName());
        return svn.getLatestRevision();
    }
}
