/**
 * 
 */
package com.ipc.uda.types;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;

/**
 *@author sharmar
 *
 */
public class DialDigitsCommandImpl extends DialDigitsCommand implements ExecutableWithContext
{
	private UserContext ctx;
	 
    @Override
    public Optional<ExecutionResult> execute() throws ExecutionException
    {   
        if(!this.ctx.getCallContext().getLeft().exists())
        {
            hunt();
        }

        waitForActiveCall();

        synchronized (this.ctx.getCallContext().getLeft())
        {
            if (this.ctx.getCallContext().getLeft().exists())
            {
                this.ctx.getCallContext().getLeft().dialDigits(this.digits);
            }
            else
            {
                Log.logger().info("Hunted for line, but line never went busy.");
            }
        }
        
        return new Nothing<ExecutionResult>();
    }

    private void waitForActiveCall() throws ExecutionException
    {
        final long endTime = System.currentTimeMillis()+500;

        while (!this.ctx.getCallContext().getLeft().exists() && System.currentTimeMillis() <= endTime)
        {
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                throw new ExecutionException(e);
            }
        }
    }

    private void hunt()
    {
        // TODO if user's CDI setting for autoHuntEnabled is false, return
        this.ctx.getCallContext().getButtonSheet().hunt();
    }
    @Override
    public void setUserContext(final UserContext ctx)
    {
        this.ctx = ctx;
    }
}
