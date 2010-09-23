/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.servlet.mock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.ipc.va.cti.CtiEvent;
import com.ipc.va.cti.logicalDevice.events.LogicalDeviceListener;

/**
 * @author mordarsd
 *
 */
public class MockCtiEventDispatcher
{

    private Method[] methods;
    
    private Object invocationTarget;
    
    public MockCtiEventDispatcher(Object invocationTarget)
    {
        this.invocationTarget = invocationTarget;
        this.methods = invocationTarget.getClass().getDeclaredMethods();
    }
    
    
    /* (non-Javadoc)
     * @see com.ipc.uda.service.servlet.mock.MockCtiListener#invoke(java.util.Map)
     */
    public void fireEvent(String methodName, Map args)
    {
        Method method = getMethod(methodName);
        if (method != null)
        {
            //System.out.println("LogicalDeviceMockCtiEventDispatcher::fireEvent - method: " + method);
            CtiEvent event = CtiEventFactory.create(args);
            try
            {
                method.invoke(this.invocationTarget, event);
            }
            catch (Throwable e)
            {
                throw new IllegalStateException(e);
            }
        }
        
    }
    
    private Method getMethod(String name)
    {
        for(Method method : methods)
        {
            if (method.getName().equalsIgnoreCase(name))
            {
                return method;
            }
        }
        return null;
    }
    
   
}
