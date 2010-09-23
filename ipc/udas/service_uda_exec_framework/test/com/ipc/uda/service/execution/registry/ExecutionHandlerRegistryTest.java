/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.execution.registry;

import static org.junit.Assert.*;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Nothing;
import com.ipc.uda.service.util.Optional;

/**
 * @author mosherc
 */
public class ExecutionHandlerRegistryTest
{
    public static class NullExecutionHandler implements ExecutionHandler
    {
        @Override
        public Optional<ExecutionResult> execute(final Executable unused)
        {
            return new Nothing<ExecutionResult>();
        }
    }

    @Test
    public void setCommandExecutionHandlerClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.setCommandExecutionHandlerClass(NullExecutionHandler.class);
        final ExecutionHandler exec = reg.createCommandExecutionHandler();
        assertTrue(exec instanceof NullExecutionHandler);
    }

    @Test
    public void setQueryExecutionHandlerClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.setQueryExecutionHandlerClass(NullExecutionHandler.class);
        final ExecutionHandler exec = reg.createQueryExecutionHandler();
        assertTrue(exec instanceof NullExecutionHandler);
    }

    @Test(expected=IllegalStateException.class)
    public void createCommandWithoutSet()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.createCommandExecutionHandler();
    }

    @Test(expected=IllegalStateException.class)
    public void createQueryWithoutSet()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.createQueryExecutionHandler();
    }

    @Test
    public void getInstance()
    {
        final ExecutionHandlerRegistry reg = ExecutionHandlerRegistry.getInstance();
        assertNotNull(reg);
    }



    @SuppressWarnings("unchecked") // testing to make sure unchecked casts will still get detected
    @Test(expected=IllegalStateException.class)
    public void setCommandExecutionHandlerWithWrongTypeOfClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final Class<ExecutionHandler> cls = (Class)String.class;
        reg.setCommandExecutionHandlerClass(cls);
    }

    @SuppressWarnings("unchecked") // testing to make sure unchecked casts will still get detected
    @Test(expected=IllegalStateException.class)
    public void setQueryExecutionHandlerWithWrongTypeOfClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final Class<ExecutionHandler> cls = (Class)String.class;
        reg.setQueryExecutionHandlerClass(cls);
    }



    public static class CannotInstantiate extends IllegalStateException { /* nothing */}

    public static class ExecutionHandlerThatCannotBeInstantiated implements ExecutionHandler
    {
        public ExecutionHandlerThatCannotBeInstantiated()
        {
            throw new CannotInstantiate();
        }
        @Override
        public Optional<ExecutionResult> execute(final Executable unused)
        {
            return null;
        }
    }

    @Test(expected=IllegalStateException.class)
    public void setCommandExecutionHandlerToBadClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.setCommandExecutionHandlerClass(ExecutionHandlerThatCannotBeInstantiated.class);
    }

    @Test(expected=IllegalStateException.class)
    public void setQueryExecutionHandlerToBadClass()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        reg.setQueryExecutionHandlerClass(ExecutionHandlerThatCannotBeInstantiated.class);
    }
}
