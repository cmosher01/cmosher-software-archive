package com.surveysampling.build.servlet.util;

import javax.servlet.GenericServlet;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;



/**
 * An Ant <code>BuildListener</code> that writes
 * build-started and build-finished messages to
 * the given servlet's log.
 * 
 * @author Chris Mosher
 */
public class ServletLogger implements BuildListener
{
    private final GenericServlet servlet;



    /**
     * @param servlet
     */
    public ServletLogger(final GenericServlet servlet)
    {
        this.servlet = servlet;
    }



    public void buildStarted(final BuildEvent event)
    {
        this.servlet.log("BUILD STARTED");
    }



    public void buildFinished(final BuildEvent event)
    {
        final Throwable error = event.getException();

        if (error == null)
        {
            this.servlet.log("BUILD SUCCESSFUL");
        }
        else
        {
            this.servlet.log("BUILD FAILED",error);
        }
    }



    public void targetStarted(BuildEvent event)
    {
    }

    public void targetFinished(BuildEvent event)
    {
    }

    public void taskStarted(BuildEvent event)
    {
    }

    public void taskFinished(BuildEvent event)
    {
    }

    public void messageLogged(BuildEvent event)
    {
    }
}
