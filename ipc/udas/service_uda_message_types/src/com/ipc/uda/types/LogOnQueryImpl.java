/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.types.util.LifecycleUtil;
import com.ipc.uda.types.util.UserStateChangeException;

/**
 * @author mordarsd
 * @author Jagannadham Dulipala
 * @author mosherc
 */
public class LogOnQueryImpl extends LogOnQuery implements ExecutableWithContext
{
    private UserContext ctx;

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * Handles all processing of the "log on" query.
     * @return {@link LogOnQueryResult}
     * @throws ExecutionException if anything goes wrong
     */
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        
        try
        {
            // TODO: Since DeviceID is now in the HTTP Header, we may no longer need to store it in the request body - DEM 5/19/10
            return LifecycleUtil.logOn(this.ctx);
        }
        catch (UserStateChangeException e)
        {
            throw new ExecutionException(
                    "Unexpected Exception caught while attempting to perform User LOGON; reason: " +
                    e.getMessage(), e);
        }
    }
    
}
