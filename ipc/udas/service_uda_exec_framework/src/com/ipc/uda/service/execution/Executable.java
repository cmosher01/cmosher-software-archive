/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution;



import com.ipc.uda.service.util.Optional;
//import com.ipc.uda.types.UserContext;



/**
 * Something that can be executed, such as a command or a query.
 * @author mordarsd
 * @author mosherc
 */
public interface Executable
{
    /**
     * Executes this command or query.
     * @return an optional result
     * @throws ExecutionException if anything goes wrong
     */
    Optional<ExecutionResult> execute() throws ExecutionException;
}
