/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;

import com.ipc.uda.service.callproc.IcmP2PCall;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * @author mordarsd
 * @author mosherc
 */
public class AnswerIcmCallCommandImpl extends AnswerIcmCallCommand implements ExecutableWithContext
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
        if (this.ctx.getCallContext().getLeft().hasLineCall())
        {
            // TODO send "Release call before selecting feature" error to UDAC
            return new Nothing<ExecutionResult>();
        }

        if (!this.ctx.getCallContext().getIcmApp().getMapAorToIntercom().containsKey(this.userAor))
        {
            throw new ExecutionException(
            		"Cannot find IcmP2PCall Call for UserAOR: " + this.userAor);
        }

        final IcmP2PCall icm = this.ctx.getCallContext().getIcmApp().getMapAorToIntercom().get(this.userAor);
        icm.udacIcmAnswer();

        return new Nothing<ExecutionResult>();
    }
}
