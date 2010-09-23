/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.invocation;



import java.lang.reflect.InvocationTargetException;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;



/**
 * An {@link Invoker} that invokes its {@link Executable} synchronously.
 * @author mosherc
 */
public class SynchronousInvoker implements Invoker
{
    /**
     * Invokes the {@link Executable#execute() execute} method of the given {@link Executable}
     * synchronously (immediately). This method returns the results
     * of calling the {@link Executable#execute() execute} method. This method will
     * throw any exception that the {@link Executable#execute() execute} method throws
     * (wrapped in an {@link InvocationTargetException}).
     * @param executable the {@link Executable} to invoke {@link Executable#execute() execute} on
     * @return the result returned by {@link Executable#execute() execute}
     * @throws InvocationTargetException wraps any exception thrown by {@link Executable#execute() execute}
     */
    @Override
    public Optional<ExecutionResult> invoke(final Executable executable) throws InvocationTargetException
    {
        final ExecutableWork work = new ExecutableWork(executable);

        work.run();

        final Optional<ExecutionException> exception = work.getException();
        if (exception.exists())
        {
            throw new InvocationTargetException(exception.get());
        }

        return work.getResult();
    }
}
