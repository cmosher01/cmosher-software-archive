/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.ejb.session;



import java.lang.reflect.InvocationTargetException;

import javax.ejb.Local;
import javax.ejb.LocalHome;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import weblogic.jws.Transactional;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.execution.invocation.Invoker;
import com.ipc.uda.service.execution.invocation.SynchronousInvoker;
import com.ipc.uda.service.util.Optional;



/**
 * Handles the execution of a given {@link Executable} query.
 */
@Stateless
@Local( { ExecutionHandlerLocal.class })
@LocalHome(ExecutionHandlerLocalHome.class)
public class QueryExecutionHandlerBean implements ExecutionHandler
{
    private final Invoker invoker = new SynchronousInvoker();

    /**
     * Executes the given {@link Executable} query.
     * @param executable the {@link Executable} query to execute
     * @return the results of the query
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Optional<ExecutionResult> execute(final Executable executable) throws InvocationTargetException
    {
        return this.invoker.invoke(executable);
    }

}
