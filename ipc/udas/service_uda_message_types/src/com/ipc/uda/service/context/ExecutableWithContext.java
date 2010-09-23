package com.ipc.uda.service.context;

import com.ipc.uda.service.execution.Executable;

/**
 * This is a convenience interface that just combines
 * {@link Executable} and {@link HasContext}.
 * @author mosherc
 *
 */
public interface ExecutableWithContext extends Executable, HasContext
{
    // no extra methods needed
}
