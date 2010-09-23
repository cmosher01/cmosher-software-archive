/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.servlet;



import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Handles the server side of the UDAS/UDAC message (or "command") channel.
 * This servlet will handle queries (HTTP GET requests) and commands (HTTP
 * POST requests).
 */
public class MessageController extends HttpServlet
{
    /**
     * Handles all processing of a GET request (a query).
     * @param request the HTTP request channel
     * @param response the HTTP response channel
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
    {
        try
        {
            ServletHelper.doGet(request,response);
        }
        catch (final Throwable e)
        {
            ServletHelper.handleException(e,response);
        }
    }

   /**
    * Handles all processing of a POST request (a command).
    * @param request the HTTP request channel
    * @param response the HTTP response channel
    */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
    {
        try
        {
            ServletHelper.doPost(request);
        }
        catch (final Throwable e)
        {
            ServletHelper.handleException(e,response);
        }
    }
}
