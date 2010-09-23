/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.execution.invocation;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;

/**
 * @author mosherc
 *
 */
public class SynchronousInvokerTest
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
    public void nominalInvoke() throws InvocationTargetException
    {
        final Invoker invoker = new SynchronousInvoker();
        final Executable executable = new ExecutableThatReturnsResult();
        final Optional<ExecutionResult> optionalResult = invoker.invoke(executable);
        assertTrue(optionalResult.exists());
        final ExecutionResult result = optionalResult.get();
        assertNotNull(result);
    }



    public static class ExecutableThatAlwaysThrowsCheckedException implements Executable
    {
        @Override
        public Optional<ExecutionResult> execute() throws ExecutionException
        {
            throw new ExecutionException();
        }
    }

    @Test(expected=InvocationTargetException.class)
    public void checkedExceptionDuringInvocation() throws InvocationTargetException
    {
        final Invoker invoker = new SynchronousInvoker();
        final Executable executable = new ExecutableThatAlwaysThrowsCheckedException();
        invoker.invoke(executable);
    }



    public static class ExecutableThatAlwaysThrowsUncheckedException implements Executable
    {
        @Override
        public Optional<ExecutionResult> execute()
        {
            throw new IllegalStateException();
        }
    }

    @Test(expected=InvocationTargetException.class)
    public void uncheckedExceptionDuringInvocation() throws InvocationTargetException
    {
        final Invoker invoker = new SynchronousInvoker();
        final Executable executable = new ExecutableThatAlwaysThrowsUncheckedException();
        invoker.invoke(executable);
    }
}
