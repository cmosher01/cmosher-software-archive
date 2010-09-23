/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.servlet;



import weblogic.servlet.http.AbstractAsyncServlet;
import weblogic.servlet.http.RequestResponseKey;



/**
 * Servlet implementation class EventController
 */
public class EventController extends AbstractAsyncServlet
{
    @Override
    public void init()
    {
        /*
         * Check for timed out requests every 5 seconds, and set the
         * timeout to 15 seconds, so unfulfilled requests will timeout in between
         * 15 and 20 seconds.
         */
        AbstractAsyncServlet.setScavangeInterval(5 * 1000);
        setTimeout(15 * 1000);
    }

    @Override
    protected boolean doRequest(final RequestResponseKey rrk)
    {
        try
        {
            return ServletHelper.doRequest(rrk);
        }
        catch (final Throwable e)
        {
            ServletHelper.handleException(e,rrk.getResponse());
            return false;
        }
    }

    @Override
    protected void doResponse(final RequestResponseKey rrk, final Object ignored)
    {
        try
        {
            ServletHelper.doResponse(rrk);
        }
        catch (final Throwable e)
        {
            ServletHelper.handleException(e,rrk.getResponse());
        }
    }

    @Override
    protected void doTimeout(final RequestResponseKey rrk)
    {
        try
        {
            ServletHelper.doTimeout(rrk);
        }
        catch (final Throwable e)
        {
            ServletHelper.handleException(e,rrk.getResponse());
        }
    }

}
