/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.invocation;



import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;
import commonj.work.Work;



/**
 * Adapts the {@link Executable} interface to the standard {@link Work} interface.
 * Also provides its own mechanism to store, and allow retrieval of, the results
 * of the execution (or an exception). Objects of this class are thread-safe (which
 * implies that is safe to call {@link ExecutableWork#getResult() getResult} or
 * {@link ExecutableWork#getException() getException} from a different thread
 * than the one that called {@link ExecutableWork#run run}). 
 * @author mosherc
 */
class ExecutableWork implements Work
{
    private final Executable executable;

    private Optional<ExecutionResult> result;
    private Optional<ExecutionException> exception;

    /**
     * Initializes this object to wrap the given {@link Executable}.
     * This object will hold a reference to the given {@link Executable}
     * for the life of this object.
     */
    public ExecutableWork(final Executable executable)
    {
        this.executable = executable;
    }

    /**
     * <p>
     * Calls the {@link Executable#execute() execute} method of this object's
     * wrapped {@link Executable}. Any result, or exception, that occurs is
     * stored internally, and can be retrieved (only after this method is
     * finished running) by calling {@link ExecutableWork#getResult() getResult}
     * and {@link ExecutableWork#getException() getException}.
     * </p>
     * <p>
     * This method never throws exceptions... not even unchecked ones.
     * </p>
     */
    @Override
    public synchronized void run()
    {
        try
        {
            this.result = execute();
            this.exception = new Nothing<ExecutionException>();
        }
        catch (final ExecutionException e)
        {
            this.result = new Nothing<ExecutionResult>();
            this.exception = new Optional<ExecutionException>(e);
        }
    }

    /**
     * Calls the {@link Executable#execute() execute} method of this object's
     * wrapped {@link Executable}. It returns the result, if any. It catches
     * any exception (even unchecked ones) and wraps it in an
     * {@link ExecutionException} (unless it already is an {@link ExecutionException}),
     * and throws it.
     * @return the result of {@link Executable#execute() execute}
     * @throws ExecutionException wraps any exception thrown by {@link Executable#execute() execute}
     */
    private Optional<ExecutionResult> execute() throws ExecutionException
    {
        try
        {
            return this.executable.execute();
        }
        catch (final ExecutionException e)
        {
            // filter out any ExecutionExceptions proper (see catch Throwable)
            throw e;
        }
        catch (final Throwable e)
        {
            // we don't want any exceptions (not even unchecked exceptions)
            // to get thrown out of our run method, so catch everything
            // here (and wrap it in an ExecutionException, unless it already
            // is an ExecutionException)
            throw new ExecutionException(e);
        }
    }

    /**
     * Returns <code>false</code>. Our work items are never (long-running) daemons.
     * @return <code>false</code>
     */
    @Override
    public boolean isDaemon()
    {
        return false;
    }

    /**
     * Does nothing.
     */
    @Override
    public void release()
    {
        // TODO don't think we need to do anything in ExecutableWork.release
    }

    /**
     * Gets the result, if any, returned by the execution of the object's wrapped
     * {@link Executable}. This method can be called only after the
     * {@link ExecutableWork#run() run} method is finished.
     * @return the result, if any
     * @throws IllegalStateException if this method is called before the {@link ExecutableWork#run() run} method is finished
     */
    public synchronized Optional<ExecutionResult> getResult()
    {
        if (this.result == null)
        {
            throw new IllegalStateException("Work has not yet completed, so the results are not available yet.");
        }
        return this.result;
    }

    /**
     * Gets the exception, if any, thrown by the execution of the object's wrapped
     * {@link Executable}. This method can be called only after the
     * {@link ExecutableWork#run() run} method is finished.
     * @return the exception, if any
     * @throws IllegalStateException if this method is called before the {@link ExecutableWork#run() run} method is finished
     */
    public synchronized Optional<ExecutionException> getException()
    {
        if (this.result == null)
        {
            throw new IllegalStateException("Work has not yet completed, so any exception is not available yet.");
        }
        return this.exception;
    }
}
