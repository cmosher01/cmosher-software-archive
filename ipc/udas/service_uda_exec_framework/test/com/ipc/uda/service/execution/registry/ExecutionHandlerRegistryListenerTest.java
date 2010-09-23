/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.execution.registry;

import static org.junit.Assert.*;

import javax.management.Notification;

import org.junit.Test;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.execution.registry.ExecutionHandlerRegistryListener.ActionType;
import com.ipc.uda.service.util.Optional;

/**
 * @author mosherc
 *
 */
public class ExecutionHandlerRegistryListenerTest
{
    public static class NullHandler implements ExecutionHandler
    {
        @Override
        public Optional<ExecutionResult> execute(final Executable unused)
        {
            return null;
        }
    }

    @Test
    public void nominalSetCommand()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final ExecutionHandlerRegistryListener listener = new ExecutionHandlerRegistryListener(reg);
        final String action = ActionType.SET_COMMAND_HANDLER_NAME.name();
        final Notification notification = new Notification(action,this,0,NullHandler.class.getName());
        listener.handleNotification(notification,null);
        final ExecutionHandler handler = reg.createCommandExecutionHandler();
        assertTrue(handler instanceof NullHandler);
    }

    @Test
    public void nominalSetQuery()
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final ExecutionHandlerRegistryListener listener = new ExecutionHandlerRegistryListener(reg);
        final String action = ActionType.SET_QUERY_HANDLER_NAME.name();
        final Notification notification = new Notification(action,this,0,NullHandler.class.getName());
        listener.handleNotification(notification,null);
        final ExecutionHandler handler = reg.createQueryExecutionHandler();
        assertTrue(handler instanceof NullHandler);
    }

    @Test(expected=IllegalArgumentException.class)
    public void setBadAction() throws ExceptionInInitializerError, IllegalArgumentException, NullPointerException, ClassNotFoundException, LinkageError
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final ExecutionHandlerRegistryListener listener = new ExecutionHandlerRegistryListener(reg);
        final String action = "Invalid.action.name";
        final Notification notification = new Notification(action,this,0,NullHandler.class.getName());
        listener.trySetNewHandler(notification);
    }

    @Test(expected=ClassNotFoundException.class)
    public void setBadClassName() throws ExceptionInInitializerError, IllegalArgumentException, NullPointerException, ClassNotFoundException, LinkageError
    {
        final ExecutionHandlerRegistry reg = new ExecutionHandlerRegistry();
        final ExecutionHandlerRegistryListener listener = new ExecutionHandlerRegistryListener(reg);
        final String action = ActionType.SET_QUERY_HANDLER_NAME.name();
        final Notification notification = new Notification(action,this,0,"bad.class.name");
        listener.trySetNewHandler(notification);
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructNull() throws IllegalArgumentException
    {
        new ExecutionHandlerRegistryListener(null);
    }
}
