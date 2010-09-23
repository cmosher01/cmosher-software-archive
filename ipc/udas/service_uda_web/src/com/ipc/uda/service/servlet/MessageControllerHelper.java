/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.lang.reflect.InvocationTargetException;

import com.ipc.uda.service.context.ExecutableWithContext;
import com.ipc.uda.service.context.UserContext;
import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionHandler;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.execution.registry.ExecutionHandlerRegistry;
import com.ipc.uda.service.util.Optional;
import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.types.MessageSerializer.InvalidXML;

/**
 * Provides helper methods for the {@link MessageController} servlet.
 * This class has no dependence on servlet classes (<code>javax.servlet...</code> packages).
 * 
 * @author mosherc
 */
final class MessageControllerHelper extends ControllerHelper
{
    MessageControllerHelper()
    {
        // package level access
    }

    /**
     * Handles a query from the client. Reads from the given {@link BufferedReader} <code>xml</code>,
     * deserializes it, executes the query, and serializes the query results
     * to the {@link BufferedWriter} <code>out</code>.
     * @param user 
     * @param xml contains the XML describing the query
     * @param out the writer that the query results are written to
     * @throws InvalidXML if the XML is invalid (or cannot be interpreted)
     * @throws InvocationTargetException if any exceptions happen during the execution of the query
     */
    public void query(final UserContext user, final BufferedReader xml, final BufferedWriter out) throws InvalidXML, InvocationTargetException
    {
        final ExecutableWithContext exe = deserialize(xml);
        exe.setUserContext(user);

        final Optional<ExecutionResult> result = executeQuery(exe);
        if (result.exists())
        {
            serialize(result.get(),out);
        }
        else
        {
            Log.logger().info("query did not return any results ("+exe.getClass().getName()+")");
        }
    }

    /**
     * Handles a command from the client. Reads from the given {@link BufferedReader} <code>xml</code>,
     * deserializes it, and executes the command. Commands are "fire and forget," and so do
     * not return any results.
     * @param user 
     * @param xml contains the XML describing the command
     * @throws InvalidXML if the XML is invalid (or cannot be interpreted)
     * @throws InvocationTargetException if any exceptions happen during the execution of the command
     */
    public void command(final UserContext user, final BufferedReader xml) throws InvalidXML, InvocationTargetException
    {
        final ExecutableWithContext exe = deserialize(xml);
        exe.setUserContext(user);

        executeCommand(exe);
    }



    /**
     * Executes the given {@link Executable} query. The results of the
     * query are returned wrapped in an {@link ExecutionResult}.
     * @param exe the query to execute
     * @return the results of the query
     * @throws InvocationTargetException if any exceptions happen during the execution of the query
     */
    private Optional<ExecutionResult> executeQuery(final Executable exe) throws InvocationTargetException
    {
        final ExecutionHandler handler = ExecutionHandlerRegistry.getInstance().createQueryExecutionHandler();
        return handler.execute(exe);
    }

    /**
     * Executes the given {@link Executable} command.
     * @param exe the query to execute
     * @throws InvocationTargetException if any exceptions happen during the execution of the command
     */
    private void executeCommand(final Executable exe) throws InvocationTargetException
    {
        final ExecutionHandler handler = ExecutionHandlerRegistry.getInstance().createCommandExecutionHandler();
        handler.execute(exe);
    }
}
