/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.types;



import com.ipc.uda.service.callproc.IcmP2PCall;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;



/**
 * @author mordarsd
 * @author mosherc
 */
public class MakeIcmP2PCallCommandImpl extends MakeIcmP2PCallCommand implements
        ExecutableWithContext
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
        if (this.ctx.getCallContext().getLeft().hasLineCall())
        {
            // TODO send "Release call before selecting feature" error to UDAC
            return new Nothing<ExecutionResult>();
        }

        final IcmP2PCall icm = createIntercomCall();

        /*
         * Now do the usual command handling, which is just to send
         * our event to the call's FSM
         */
        icm.udacIcmOutgoing(this.userAor);

        return new Nothing<ExecutionResult>();
    }

    private IcmP2PCall createIntercomCall()
    {
        /*
         * We are making an outgoing ICM call, so we create the call
         * object (specifically, an ICM) here. We need to create ICMs,
         * as opposed to line calls, because ICMs don't exist as
         * buttons in the button sheet.
         */
        final IcmP2PCall icm = new IcmP2PCall(this.ctx);
        icm.startFSM();

        /*
         * Then we need to add it to our user context, for later
         * lookup by the AOR of the far end.
         */
        this.ctx.getCallContext().getIcmApp().getMapAorToIntercom().put(this.userAor,icm);

        return icm;
    }
}
