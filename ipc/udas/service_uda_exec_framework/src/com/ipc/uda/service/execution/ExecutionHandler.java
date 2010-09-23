/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution;

import java.lang.reflect.InvocationTargetException;

import com.ipc.uda.service.util.Optional;



/**
 * A handler that executes {@link Executable}s.
 * @author mordarsd
 */
public interface ExecutionHandler
{
    /**
     * Executes the given {@link Executable}. The execution may be immediate or
     * deferred.
     * @param executable the {@link Executable} to execute.
     * @return result (never null)
     * @throws InvocationTargetException if the Executable's execute method
     * throws an exception, then it's wrapped in this {@link InvocationTargetException}
     */
    Optional<ExecutionResult> execute(Executable executable) throws InvocationTargetException;
}
