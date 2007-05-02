package com.surveysampling.build.servlet.util;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;

import com.surveysampling.util.XMLUtil;
import com.surveysampling.util.XMLFilterWriter;




/**
 * An Ant logger that formats messages suitable for
 * displaying in a table in a web page.
 * 
 * @author Chris Mosher
 */
public class HtmlReportLogger implements BuildListener
{
    private static final int MAX_PRIORITY_DISPLAYED = Project.MSG_VERBOSE;

    private final PrintWriter writer;

    private boolean oddRow;

    private static final SimpleDateFormat fmtTime = new SimpleDateFormat("yyyy/MM/dd'&nbsp;'HH:mm:ss");

    private Throwable throwableLastDisplayed = new Throwable();


    /**
     * Initializes this logger to write to the given
     * <code>PrintWriter</code>.
     * @param writer
     */
    public HtmlReportLogger(final PrintWriter writer)
    {
        this.writer = writer;
    }



    public void buildStarted(final BuildEvent event)
    {
        setEventMessage("BUILD STARTED",event);
        writeEvent(event);
    }

    public void buildFinished(final BuildEvent event)
    {
        setEventMessage("BUILD FINISHED",event);
        writeEvent(event);
    }

    public void targetStarted(final BuildEvent event)
    {
        setEventMessage("TARGET STARTED",event);
        writeEvent(event);
    }

    public void targetFinished(final BuildEvent event)
    {
        setEventMessage("TARGET FINISHED",event);
        writeEvent(event);
    }

    public void taskStarted(final BuildEvent event)
    {
        setEventMessage("TASK STARTED",event);
        writeEvent(event);
    }

    public void taskFinished(final BuildEvent event)
    {
        setEventMessage("TASK FINISHED",event);
        writeEvent(event);
    }

    public void messageLogged(final BuildEvent event)
    {
        writeEvent(event);
    }



    protected void writeEvent(final BuildEvent event)
    {
        // don't think this ever happens, but just in case:
        if (event == null)
        {
            return;
        }

        // make sure events with exceptions have level set to "error"
        final Throwable error = event.getException();
        if (error != null)
        {
            event.setMessage(event.getMessage(),Project.MSG_ERR);
        }

        // see if we should display the message or not,
        // depending on the "priority" or verbosity level
        // Note that higher priorities mean more verbosity
        if (event.getPriority() > MAX_PRIORITY_DISPLAYED)
        {
            return;
        }

        final String nameOfTask = getTaskName(event);
        final String nameOfTarget = getTargetName(event);
        final String sPriority = getPriorityClass(event.getPriority());
        final String messageEvent = getMessage(event);

        final boolean onAnOddRow = getAndIncRow();



        this.writer.print("<tr class=\"");
        this.writer.print(onAnOddRow ? "oddRow" : "evenRow");
        this.writer.print("\">");



        this.writer.print("<td>");
        if (nameOfTarget.length() > 0)
        {
            this.writer.print(nameOfTarget);
        }
        else
        {
            this.writer.print("&nbsp;");
        }
        this.writer.print("</td>");



        this.writer.print("<td>");
        if (nameOfTask.length() > 0)
        {
            this.writer.print(nameOfTask);
        }
        else
        {
            this.writer.print("&nbsp;");
        }
        this.writer.print("</td>");



        this.writer.print("<td>");
        this.writer.print(fmtTime.format(new Date()));
        this.writer.print("</td>");



        this.writer.print("<td class=\"");
        this.writer.print(sPriority);
        this.writer.print("\">");

        if (messageEvent.startsWith("<span>"))
        {
            this.writer.print(messageEvent);
        }
        else
        {
            this.writer.print(XMLUtil.filterForXML(messageEvent));
        }

        if (error != null)
        {
            if (error != this.throwableLastDisplayed)
            {
                this.throwableLastDisplayed = error;
                this.writer.println();
                this.writer.println("<pre>");
                final PrintWriter xmlFilter = new PrintWriter(new BufferedWriter(new XMLFilterWriter(this.writer)));
                error.printStackTrace(xmlFilter);
                xmlFilter.flush();
                this.writer.println("</pre>");
            }
        }

        this.writer.print("</td>");



        this.writer.println("</tr>");
    }

    protected String getMessage(final BuildEvent event)
    {
        final String messageEvent = event.getMessage();
        if (messageEvent == null)
        {
            return "";
        }

        return messageEvent;
    }



    protected String getTargetName(final BuildEvent event)
    {
        final Target target = event.getTarget();
        if (target == null)
        {
            return "";
        }

        final String name = target.getName();
        if (name == null)
        {
            return "";
        }

        return name;
    }



    protected String getTaskName(final BuildEvent event)
    {
        final Task task = event.getTask();
        if (task == null)
        {
            return "";
        }

        final String name = task.getTaskName();
        if (name == null)
        {
            return "";
        }

        return name;
    }



    protected void setEventMessage(final String msg, final BuildEvent event)
    {
        event.setMessage(msg,event.getPriority());
    }



    protected synchronized boolean getAndIncRow()
    {
        final boolean odd = this.oddRow;
        this.oddRow = !odd;
        return odd;
    }



    protected String getPriorityClass(final int priority)
    {
        switch (priority)
        {
            case Project.MSG_DEBUG:
                return "MSG_DEBUG";
            case Project.MSG_VERBOSE:
                return "MSG_VERBOSE";
            case Project.MSG_INFO:
                return "MSG_INFO";
            case Project.MSG_WARN:
                return "MSG_WARN";
            case Project.MSG_ERR:
                return "MSG_ERR";
        }
        return "";
    }
}
