/*
 * Created on Aug 4, 2005
 */
package com.surveysampling.build.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.surveysampling.build.servlet.util.BuildUtils;

/**
 * Just a simple static page server, pulling
 * pages from the "javadoc" directory in the
 * web application's temporary directory.
 * 
 * @author Chris Mosher
 */
public class ViewJavadoc extends HttpServlet
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

    protected void tryGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException
    {
        final PrintWriter webPage = BuildUtils.getOutputAsHTML(response);

        final String path = request.getPathInfo();

        final File dirTemp = BuildUtils.getTempDir(getServletContext());
        final File startPage = new File(dirTemp,"javadoc"+path);

        printFile(startPage,webPage);

        webPage.flush();
    }

    private static void printFile(final File startPage, final PrintWriter webPage) throws IOException
    {
        final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(startPage),"UTF-8"));
        for (String sLine = in.readLine(); sLine != null; sLine = in.readLine())
        {
            webPage.println(sLine);
        }
        in.close();
    }
}
