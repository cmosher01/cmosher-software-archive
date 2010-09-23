/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ipc.uda.service.locator.ServiceLocatorProvider;

/**
 * @author mordarsd
 *
 */
public final class MockCtiEventContoller extends HttpServlet
{

    private static final Map<String, MockCtiEventDispatcher> DISPATCHER_MAP = new HashMap<String, MockCtiEventDispatcher>(3);
    
    static
    {
        DISPATCHER_MAP.put("callcontrol", new MockCtiEventDispatcher(ServiceLocatorProvider.getUdaServiceLocator().getCallControlListener()));
        DISPATCHER_MAP.put("logicaldevice", new MockCtiEventDispatcher(ServiceLocatorProvider.getUdaServiceLocator().getLogicalDeviceListener()));
        DISPATCHER_MAP.put("physicaldevice", new MockCtiEventDispatcher(ServiceLocatorProvider.getUdaServiceLocator().getPhysicalDeviceListener()));
        DISPATCHER_MAP.put("linestatus", new MockCtiEventDispatcher(ServiceLocatorProvider.getUdaServiceLocator().getLineStatusListener()));
    }
    
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        final String path = req.getServletPath();
        
        final String type = path.substring(path.lastIndexOf("/") + 1);
        final String method = req.getParameter("method");
        
        MockCtiEventDispatcher dispatcher = null;
        
        try
        {
            dispatcher = DISPATCHER_MAP.get(type);
            
//            resp.setContentType("text/html");
//            resp.getWriter().printf(
//                    "Firing event - MockCtiEventDispatcher: %s<br>method: %s<br>args: %s<br><br>",
//                   dispatcher.getClass().getName(), method, req.getParameterMap());
            System.out.printf(
                    "Firing event - MockCtiEventDispatcher: %s\nmethod: %s\nargs: %s\n\n",
                   dispatcher.getClass().getName(), method, req.getParameterMap());
            
            
            dispatcher.fireEvent(method, req.getParameterMap());
        }
        catch(Throwable t)
        {
            throw new ServletException(t.getMessage());
        }
        finally
        {
            //resp.getWriter().flush();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        doGet(req, resp);
    }
    

    
}
