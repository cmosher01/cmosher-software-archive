/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import weblogic.servlet.http.RequestResponseKey;

import com.ipc.security.common.SecurityAAUtil;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.context.UserContextManager;
import com.ipc.uda.service.context.UserContextManagerCleaner;
import com.ipc.uda.service.context.UserID;
import com.ipc.uda.service.util.UdaPrincipal;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.MessageSerializer.InvalidXML;



/**
 * @author mosherc
 *
 */
final class ServletHelper
{
    private static final UserContextManager managerUserContext = UserContextManager.getInstance();

    @SuppressWarnings("unused")
    private static final UserContextManagerCleaner managerUserContextCleaner = new UserContextManagerCleaner();

    private static final MessageControllerHelper helperMessage = new MessageControllerHelper();
    private static final EventControllerHelper helperEvent = new EventControllerHelper();



    private ServletHelper()
    {
        throw new IllegalStateException();
    }





    /**
     * Handles all processing of a GET request (a query), except that this method just throws an exception
     * (if something goes wrong); it doesn't do exception handling.
     * @param request the HTTP request channel
     * @param response the HTTP response channel
     * @throws InvocationTargetException 
     * @throws InvalidXML 
     */
    public static void doGet(final HttpServletRequest request, final HttpServletResponse response) throws InvalidXML, InvocationTargetException
    {
        final UserContext user = getUserContext(request);
        if (Log.logger().isDebugEnabled())
        {
            Log.logger().debug("Received query from user "+user.getUser().getName());
        }
        final BufferedReader xml = RequestUtils.getXmlFromRequestParameter(request);
        final BufferedWriter out = ResponseUtils.getResponseWriter(response);
        ServletHelper.helperMessage.query(user,xml,out);
    }

    /**
     * Handles all processing of a POST request (a command), except that this method just throws an exception
     * (if something goes wrong); it doesn't do exception handling.
     * @param request the HTTP request channel
     * @param response the HTTP response channel
     * @throws InvocationTargetException 
     * @throws InvalidXML 
     */
    public static void doPost(final HttpServletRequest request) throws InvalidXML, InvocationTargetException
    {
        final UserContext user = getUserContext(request);
        if (Log.logger().isDebugEnabled())
        {
            Log.logger().debug("Received command from user "+user.getUser().getName());
        }
        final BufferedReader xml = RequestUtils.getXmlFromRequestBody(request);
        ServletHelper.helperMessage.command(user,xml);
    }



    /**
     * Handles the request portion of the long poll (the event channel).
     * @param rrk the RequestResponseKey
     * @return <code>true</code> if doResponse will be called later (asynchronously),
     * <code>false</code> if doResponse has already been called synchronously
     */
    public static boolean doRequest(final RequestResponseKey rrk)
    {
        final UserContext user = getUserContext(rrk.getRequest());
        user.getTimer().connect(); // keep session active
        return user.getEventQueue().register(rrk);
    }

    /**
     * Handles the response portion of the long poll (the event channel).
     * @param rrk the RequestResponseKey as passed into the corresponding
     * doRequest call.
     */
    public static void doResponse(final RequestResponseKey rrk)
    {
        doResponseOrTimeout(rrk,false);
    }

    /**
     * Handles the response portion of the long poll (the event channel), in
     * the case where no event messages arrive within the timeout period.
     * @param rrk the RequestResponseKey as passed into the corresponding
     * doRequest call.
     */
    public static void doTimeout(final RequestResponseKey rrk)
    {
        doResponseOrTimeout(rrk,true);
    }

    private static void doResponseOrTimeout(final RequestResponseKey rrk, final boolean timeout)
    {
        final UserContext user = getUserContext(rrk.getRequest());
        user.getTimer().disconnect(); // remove session if times out
        final BufferedWriter out = ResponseUtils.getResponseWriter(rrk.getResponse());
        ServletHelper.helperEvent.longpoll(user,timeout,out);
    }



    /**
     * Gets the use principal from the given request. If the user has not been
     * authenticated, then throw an exception.
     * @param request
     * @return the user principal (never null)
     */
    private static UserContext getUserContext(final HttpServletRequest request)
    {
        final String auth = RequestUtils.getAuthorizationHeader(request);
        final UdaPrincipal user = new UdaPrincipal(SecurityAAUtil.getLoginName(auth));
        
        final String deviceID = RequestUtils.getDeviceIDHeader(request);
        final UserID userID = new UserID(user, deviceID);
        
        UserContext userCtx = ServletHelper.managerUserContext.getOrCreateContext(userID);
        userCtx.setUsingMockObjects(RequestUtils.isUsingMockObjects(request));
        return userCtx;
    }






 




    /**
     * Handles logging (to the DMS logging facility) and reporting (to the given HTTP response channel)
     * of the given exception.
     * @param exception the exception that will be handled
     * @param response the HTTP response stream to send the error to
     */
    public static void handleException(final Throwable exception, final HttpServletResponse response)
    {
        // if anything goes wrong, log it, and send generic error to client
        Log.logger().logEvent("UDAS","BadRequest",null,exception);
        try
        {
            if (response != null)
            {
                sendErrorResponse(exception,response);
            }
        }
        catch (final Throwable ignore)
        {
            // if we failed to send an error message to the user,
            // that's OK, just do nothing
            Log.logger().info("Error while trying to send error message to client; ignoring it.",ignore);
        }
    }





    private static void sendErrorResponse(final Throwable exception, final HttpServletResponse response)
        throws IOException
    {
        final StringWriter st = new StringWriter(1024);
        exception.printStackTrace(new PrintWriter(st));

        response.sendError(HttpServletResponse.SC_BAD_REQUEST,st.toString());
    }
}
