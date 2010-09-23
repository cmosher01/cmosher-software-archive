/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */
package com.ipc.uda.service.execution.invocation;



import java.lang.reflect.InvocationTargetException;

import com.ipc.uda.service.execution.Executable;
import com.ipc.uda.service.execution.ExecutionResult;
import com.ipc.uda.service.util.Optional;



/**
 * Classes that implement this interface will invoke an {@link Executable} in
 * some manner.
 * 
 * @author mosherc
 */
public interface Invoker
{
    /**
     * Invokes the {@link Executable#execute() execute} method of the given {@link Executable}
     * in some manner. This method may or may not return the results
     * of calling the {@link Executable#execute() execute} method, depending on the manner of
     * invocation (such as synchronous versus asynchronous). Similarly, this method may or
     * may not throw an exception that the {@link Executable#execute() execute} method throws.
     * If so, then this method will throw an {@link InvocationTargetException} whose cause
     * is the exeception thrown by the {@link Executable#execute() execute} method.
     * Any further constraints would be defined by classes that implement {@link Invoker}.
     * @param executable the {@link Executable} to invoke {@link Executable#execute() execute} on
     * @return possibly the result returned by {@link Executable#execute() execute}
     * @throws InvocationTargetException wraps any exception thrown by {@link Executable#execute() execute}
     */
    Optional<ExecutionResult> invoke(Executable executable) throws InvocationTargetException;
}
