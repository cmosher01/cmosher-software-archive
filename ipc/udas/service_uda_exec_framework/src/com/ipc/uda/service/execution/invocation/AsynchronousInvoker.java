/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.invocation;



import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;

import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkListener;
import commonj.work.WorkManager;



/**
 * An {@link Invoker} that invokes its {@link Executable} asynchronously, by
 * scheduling it with a given {@link WorkManager}.
 *
 * @author mosherc
 */
public class AsynchronousInvoker implements Invoker
{
    private final WorkManager workManager;

    /**
     * Initializes this {@link AsynchronousInvoker} to use the
     * given {@link WorkManager}.
     * @param workManager the work manager to use to schedule the work
     */
    public AsynchronousInvoker(final WorkManager workManager)
    {
        this.workManager = workManager;
    }

    /**
     * Gets called by the work manager upon completion of the scheduled
     * work (the {@link ExecutableWork}).
     * @author mosherc
     */
    private static class AsynchronousWorkListener implements WorkListener
    {
        /**
         * Logs any exception thrown by the {@link Executable}.
         */
        @Override
        public void workCompleted(WorkEvent event)
        {
            final ExecutableWork work = getWork(event);

            // Ignore work.getResults(), because commands (by definition) do not return results.
            if (Log.logger().isDebugEnabled() && work.getResult().exists())
            {
                Log.logger().debug("Anomaly: command tried to return a result; ignoring it.");
            }

            final Optional<ExecutionException> exception = work.getException();
            if (exception.exists())
            {
                Log.logger().logEvent("UDAS","CommandException",null,exception.get());
                // TODO: send command error message to client?
            }
        }

        /**
         * Extracts the ExecutableWork from the given WorkEvent.
         * @param event the event to extract from
         * @return the ExecutableWork that was in event
         */
        private static ExecutableWork getWork(final WorkEvent event)
        {
            try
            {
                return (ExecutableWork)event.getWorkItem().getResult();
            }
            catch (final WorkException shouldNeverHappen)
            {
                // assume that this will never happen, because the ExecutableWork.run
                // method never throws an exception
                throw new IllegalStateException(shouldNeverHappen);
            }
        }

        @Override
        public void workAccepted(WorkEvent event) { /* nothing to do */ }

        @Override
        public void workRejected(WorkEvent event) { /* nothing to do */ }

        @Override
        public void workStarted(WorkEvent event) { /* nothing to do */ }
    }





    /**
     * Invokes the {@link Executable#execute() execute} method of the given {@link Executable}
     * asynchronously (this is, it schedules it with this object's work manager).
     * Since the invocation is asynchronous, this method does not return the results
     * of calling the {@link Executable#execute() execute} method. Similarly, this method will
     * not throw any exception that the {@link Executable#execute() execute} method may throw.
     * @param executable the {@link Executable} to invoke {@link Executable#execute() execute} on
     * @return an empty {@link Optional Optional&lt;ExecutionResult&gt;}
     * @throws IllegalStateException if any exception occurs while trying to schedule the work
     */
    @SuppressWarnings("synthetic-access")
    @Override
    public Optional<ExecutionResult> invoke(final Executable executable)
    {
        final ExecutableWork work = new ExecutableWork(executable);

        try
        {
            this.workManager.schedule(work,new AsynchronousWorkListener());
        }
        catch (final Throwable e)
        {
            // scheduling work should never cause exceptions
            throw new IllegalStateException(e);
        }

        return new Nothing<ExecutionResult>();
    }

}
