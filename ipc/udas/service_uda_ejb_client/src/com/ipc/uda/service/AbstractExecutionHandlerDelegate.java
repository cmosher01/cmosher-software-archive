/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service;



import java.lang.reflect.InvocationTargetException;

import com.ipc.uda.service.ejb.session.ExecutionHandlerLocalHome;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;



/**
 * @author mordarsd
 * @author mosherc
 * 
 */
abstract class AbstractExecutionHandlerDelegate implements ExecutionHandler
{
    protected abstract String getJndiName();

    @Override
    public Optional<ExecutionResult> execute(final Executable executable) throws InvocationTargetException
    {
        return getExecutionHandler().execute(executable);
    }

    private ExecutionHandler getExecutionHandler()
    {
        try
        {
            final ExecutionHandlerLocalHome home = (ExecutionHandlerLocalHome)EJBHomeLocator.getInstance().getLocalHome(getJndiName());
            return home.create();
        }
        catch (final Throwable e)
        {
            throw new IllegalStateException(e);
        }
    }
}
