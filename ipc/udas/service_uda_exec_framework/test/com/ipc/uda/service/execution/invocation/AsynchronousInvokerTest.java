/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.execution.invocation;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionException;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

import commonj.work.Work;
import commonj.work.WorkEvent;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;

/**
 * @author mosherc
 *
 */
public class AsynchronousInvokerTest
{
    public static class SimpleWorkManager implements WorkManager
    {
        @Override
        public WorkItem schedule(final Work work, final WorkListener listener)
        {
            work.run();

            final WorkItem workItem = new WorkItem()
            {
                @Override
                public Work getResult()
                {
                    return work;
                }

                @Override
                public int getStatus() { /* not used */ return 0; }

                @Override
                public int compareTo(Object o) { /* not used */ return 0; }
            };

            listener.workCompleted(new WorkEvent()
            {
                @Override
                public WorkItem getWorkItem()
                {
                    return workItem;
                }

                @Override
                public WorkException getException() { /* not used */ return null; }

                @Override
                public int getType() { /* not used */ return 0; }
            });

            return workItem;
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean waitForAll(Collection unused, long unused2) { /* not used */ return false; }
        @SuppressWarnings("unchecked")
        @Override
        public Collection waitForAny(Collection unused, long unused2) { /* not used */ return unused; }
        @Override
        public WorkItem schedule(Work unused) { /* not used */ return null; }
    }



    public static class ExecutableThatReturnsNothing implements Executable
    {
        public boolean executed;
        @Override
        public Optional<ExecutionResult> execute()
        {
            this.executed = true;
            return new Nothing<ExecutionResult>();
        }
    }



    @Test
    public void nominalInvoke() throws InvocationTargetException
    {
        final Invoker invoker = new AsynchronousInvoker(new SimpleWorkManager());
        final ExecutableThatReturnsNothing executable = new ExecutableThatReturnsNothing();
        final Optional<ExecutionResult> optionalResult = invoker.invoke(executable);
        assertFalse(optionalResult.exists());
        assertTrue(executable.executed);
    }



    public static class ExecutableThatAlwaysThrowsCheckedException implements Executable
    {
        public boolean executed;
        @Override
        public Optional<ExecutionResult> execute() throws ExecutionException
        {
            this.executed = true;
            throw new ExecutionException();
        }
    }

    @Test
    public void checkedExceptionDuringInvocation() throws InvocationTargetException
    {
        final Invoker invoker = new AsynchronousInvoker(new SimpleWorkManager());
        final ExecutableThatAlwaysThrowsCheckedException executable = new ExecutableThatAlwaysThrowsCheckedException();
        invoker.invoke(executable);
        assertTrue(executable.executed);
    }



    public static class ExecutableThatAlwaysThrowsUncheckedException implements Executable
    {
        public boolean executed;
        @Override
        public Optional<ExecutionResult> execute()
        {
            this.executed = true;
            throw new IllegalStateException();
        }
    }

    @Test
    public void uncheckedExceptionDuringInvocation() throws InvocationTargetException
    {
        final Invoker invoker = new AsynchronousInvoker(new SimpleWorkManager());
        final ExecutableThatAlwaysThrowsUncheckedException executable = new ExecutableThatAlwaysThrowsUncheckedException();
        invoker.invoke(executable);
        assertTrue(executable.executed);
    }
}
