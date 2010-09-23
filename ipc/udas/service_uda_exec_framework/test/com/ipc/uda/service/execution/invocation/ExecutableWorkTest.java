/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.execution.invocation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;

/**
 * @author mosherc
 *
 */
public class ExecutableWorkTest
{
    public static class ExecutableThatReturnsResult implements Executable
    {
        @Override
        public Optional<ExecutionResult> execute()
        {
            return new Optional<ExecutionResult>(new ExecutionResult(){/* */});
        }
    }



    @Test
    public void isDaemon()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatReturnsResult());
        assertFalse(work.isDaemon());
    }

    @Test
    public void nominalRun()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatReturnsResult());
        work.run();
        final Optional<ExecutionResult> optionalResult = work.getResult();
        assertTrue(optionalResult.exists());
        final ExecutionResult result = optionalResult.get();
        assertNotNull(result);
    }

    @Test(expected=IllegalStateException.class)
    public void getResultWithoutRunning()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatReturnsResult());
        work.getResult();
    }

    @Test(expected=IllegalStateException.class)
    public void getExceptionWithoutRunning()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatReturnsResult());
        work.getException();
    }



    public static class ExecutableThatAlwaysThrowsCheckedException implements Executable
    {
        @Override
        public Optional<ExecutionResult> execute() throws ExecutionException
        {
            throw new ExecutionException();
        }
    }

    @Test
    public void checkedExceptionDuringExecution()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatAlwaysThrowsCheckedException());
        work.run();

        final Optional<ExecutionResult> optionalResult = work.getResult();
        assertFalse(optionalResult.exists());

        final Optional<ExecutionException> optionalException = work.getException();
        assertTrue(optionalException.exists());

        final ExecutionException exception = optionalException.get();
        assertNotNull(exception);
    }

    public static class ExecutableThatAlwaysThrowsUncheckedException implements Executable
    {
        @Override
        public Optional<ExecutionResult> execute()
        {
            throw new IllegalStateException();
        }
    }

    @Test
    public void uncheckedExceptionDuringExecution()
    {
        final ExecutableWork work = new ExecutableWork(new ExecutableThatAlwaysThrowsUncheckedException());
        work.run();

        final Optional<ExecutionResult> optionalResult = work.getResult();
        assertFalse(optionalResult.exists());

        final Optional<ExecutionException> optionalException = work.getException();
        assertTrue(optionalException.exists());

        final ExecutionException exception = optionalException.get();
        assertNotNull(exception);
    }
}
