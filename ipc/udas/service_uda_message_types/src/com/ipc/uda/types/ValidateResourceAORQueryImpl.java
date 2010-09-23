/* Copyright (c) 2009 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

public class ValidateResourceAORQueryImpl extends ValidateResourceAORQuery implements ExecutableWithContext
{

    private UserContext ctx;

    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }

    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        return new Nothing<ExecutionResult>();
    }
}
