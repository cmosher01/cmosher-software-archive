/**
 * 
 */
package com.ipc.uda.types;

import com.ipc.uda.service.callproc.ConferenceCall;
import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * @author parkerj
 * @author sharmar
 * @author mosherc
 */
public class ConferenceCommandImpl extends ConferenceCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {
        /*
         * Making conference call on the current handset.
         * This just turns on conference mode. Note that conference mode
         * cannot really be turned off in UDA. It will only leave
         * conference mode when all parties leave the conference, or
         * when the conference is released.
         */

        // TODO switch (this.handset)

        final ConferenceCall confCall = this.ctx.getCallContext().getLeft();
        confCall.turnOn();

        return new Nothing<ExecutionResult>();
    }

    @Override
    public void setUserContext(UserContext ctx)
    {
        this.ctx = ctx;
    }
}
