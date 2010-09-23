/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;



import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;



public class ReleaseCallCommandImpl extends ReleaseCallCommand implements ExecutableWithContext
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
        /*
         * Release the current call on the current handset,
         * if there is such a call.
         */

        // TODO switch (this.handset)
        this.ctx.getCallContext().getLeft().release();

        return new Nothing<ExecutionResult>();
    }
}
