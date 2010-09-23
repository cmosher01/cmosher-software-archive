/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.registry;

import javax.management.Notification;
import javax.management.NotificationListener;

import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.util.Classes;
import com.ipc.uda.service.util.logging.Log;



/**
 * Listens to notifications (from an MBean) to adjust the given
 * {@link ExecutionHandlerRegistry}'s configuration values.
 * @author mordarsd
 * @author mosherc
 */
class ExecutionHandlerRegistryListener implements NotificationListener
{
    public enum ActionType
    {
        SET_COMMAND_HANDLER_NAME, SET_QUERY_HANDLER_NAME
    }



    private final ExecutionHandlerRegistry registry;

    /**
     * Initializes this object to update the given {@link ExecutionHandlerRegistry}
     * (when notified).
     * @param registry the registry to update
     */
    ExecutionHandlerRegistryListener(final ExecutionHandlerRegistry registry)
    {
        // package-level access constructor because it is instantiated
        // (only) by RegisterExecutionHandlerMBeansTask, and junit tests
        this.registry = registry;
        if (this.registry == null)
        {
            throw new IllegalArgumentException("registry cannot be null");
        }
    }

    /**
     * Notifies this object to update its registry with a new {@link ExecutionHandler} class.
     * The type field of the notification is the type of handler to update (command or query),
     * and the message field of the notification is the full class name of the handler class.
     */
    @Override
    public void handleNotification(final Notification notification, final Object unused)
    {
        try
        {
            trySetNewHandler(notification);
        }
        catch (final Throwable ignore)
        {
            Log.logger().debug("error setting new ExecutionHandler",ignore);
        }
    }

    /**
     * (Package-level access so unit tests can call it)
     * @param notification
     * @throws ClassNotFoundException
     * @throws LinkageError
     * @throws ExceptionInInitializerError
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    void trySetNewHandler(final Notification notification) throws ClassNotFoundException, LinkageError, ExceptionInInitializerError, IllegalArgumentException, NullPointerException
    {
        final ActionType handlerType = ActionType.valueOf(notification.getType());
        final String className = notification.getMessage();

        final Class<? extends ExecutionHandler> newHandler = Classes.<ExecutionHandler>getClassByName(className,ExecutionHandler.class);
        switch (handlerType)
        {
            case SET_COMMAND_HANDLER_NAME:
            {
                this.registry.setCommandExecutionHandlerClass(newHandler);
            }
            break;

            case SET_QUERY_HANDLER_NAME:
            {
                this.registry.setQueryExecutionHandlerClass(newHandler);
            }
            break;

            default:
            {
                throw new IllegalStateException();
            }
        }
    }
}
