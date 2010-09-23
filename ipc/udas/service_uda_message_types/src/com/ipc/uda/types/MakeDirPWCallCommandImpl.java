/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

public class MakeDirPWCallCommandImpl extends MakeDirPWCallCommand implements ExecutableWithContext
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
         * TODO implement MakeDirPWCallCommandImpl:
         * look in the user's button sheet for a button having its AOR
         * equal to this.resourceAor. If there is more than one button,
         * just use any one. Once you have the button, send "press" command
         * to it. If there is no button, then send error message.
         */
        
        return new Nothing<ExecutionResult>();
    }
}
