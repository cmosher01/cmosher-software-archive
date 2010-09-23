/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;


import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * @author mordarsd
 *
 */
public class StartCallProcessingCommandImpl extends StartCallProcessingCommand implements ExecutableWithContext
{
    private UserContext ctx;
    
    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }    
    
    @Override
    public Optional<ExecutionResult> execute()
    {
        this.ctx.getCallContext().startFSMs();
        this.ctx.initializeCtiCallbackHandler();

        return new Nothing<ExecutionResult>();
    }
}
