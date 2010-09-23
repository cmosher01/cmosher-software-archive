/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.util.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.ipc.util.logging.Logger;

/**
 * Substitute for DMS's logger, to be used while theirs is under development.
 * @author mosherc
 */
public class SubstituteLogger implements Logger
{
    /**
     * Lock type (object to lock on).
     */
    private static class Lock { public Lock() { /*object to lock on*/ } }

    /**
     * Synch lock for writing error message.
     */
    private static final Lock LOCK = new Lock();

    /**
     * Formatter for timestamps
     */
    private static final SimpleDateFormat FMT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");



    SubstituteLogger()
    {
        // package access only
    }




    @Override
    public void account(String userName, String objectName, String action, boolean result,
            String resultDetail, Object newData, Object oldData)
    {
        log(userName+": "+objectName+": "+action+": "+oldData+" --> "+newData+": "+resultDetail,LogLevel.INFO);
    }

    @Override
    public void audit(String userName, String event, boolean result, String resultDetail)
    {
        log(userName+": "+event+": "+resultDetail,LogLevel.INFO);
    }

    @Override
    public void debug(String message)
    {
        log(message,LogLevel.DEBUG);
    }

    @Override
    public void debug(String message, Throwable throwable)
    {
        log(message,LogLevel.DEBUG,throwable);
    }

    @Override
    public LogLevel getLogLevel()
    {
        return LogLevel.DEBUG;
    }

    @Override
    public long getMessageDumpSize()
    {
        return 0;
    }    

    @Override
    public void info(String message)
    {
        log(message,LogLevel.INFO);
    }

    @Override
    public void info(String message, Throwable throwable)
    {
        log(message,LogLevel.INFO,throwable);
    }

    @Override
    public boolean isDebugEnabled()
    {
        return true;
    }

    @Override
    public boolean isInfoEnabled()
    {
        return true;
    }

    @Override
    public boolean isMessageDumpEnabled()
    {
        return false;
    }

    @Override
    public void logEvent(String componentName, String eventName)
    {
        logEvent(componentName,eventName,null);
    }

    @Override
    public void logEvent(String componentName, String eventName, Object[] params)
    {
        logEvent(componentName,eventName,params,null);
    }

    @Override
    public void logEvent(String componentName, String eventName, Object[] params,
            Throwable throwable)
    {
        final StringBuilder sb = new StringBuilder();
        if (eventName != null)
        {
            sb.append(eventName);
        }
        if (params != null && params.length > 0)
        {
            for (final Object param : params)
            {
                if (param != null)
                {
                    sb.append(" ");
                    sb.append(param.toString());
                }
            }
        }
        log(sb.toString(),LogLevel.INFO,throwable);
    }

    @Override
    public void setLogLevel(LogLevel logLevel)
    {
        // ignored (we always log at debug level)
    }

    @Override
    public void setMessageDumpEnabled(boolean enabled)
    {
        // ignored (we never have message dump)
    }

    @Override
    public void setMessageDumpSize(int size)
    {
        // ignored (we never have message dump)
    }

    private void log(final String message, LogLevel level)
    {
        log(message,level,null);
    }

    @Override
    public void logMessage(LogLevel level, String compName, String message, Calendar date,
            String deviceId, Throwable t)
    {
        log(FMT.format(date)+" "+compName+" device: "+deviceId+"; "+message,level,t);
    }

    // all logging goes through here
    private static void log(final String message, final LogLevel level, final Throwable exception)
    {
        final StringWriter sb = new StringWriter(1024);
        final PrintWriter err = new PrintWriter(sb);

        SubstituteLogger.buildErrorMessage(message,level,exception,err);

        err.flush();

        synchronized (SubstituteLogger.LOCK)
        {
            System.err.println(sb.toString());
        }

        if (err.checkError())
        {
            System.err.println(
                    "Error while creating error message. The original error message is: "+message);
        }

        err.close();
    }

    private static void buildErrorMessage(
            final String message,
            final LogLevel level,
            final Throwable exception,
            final PrintWriter err)
    {
        err.println("-----UDAS-["+level+"]-message-begin-----------------------------------");

        final String threadName = Thread.currentThread().getName();
        final long threadID = Thread.currentThread().getId();
        err.println("thread: "+Long.toHexString(threadID)+" "+(threadName==null?"":threadName));

        final String time = FMT.format(new Date(System.currentTimeMillis()));
        err.println(time+" "+message);

        if (exception != null)
        {
            exception.printStackTrace(err);
        }

        err.println("-----UDAS-["+level+"]-message-end-------------------------------------");
    }




    @Override
    public void dumpMemoryBuffer()
    {
        // TODO Auto-generated method stub
        
    }
}
