/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util.smc;

import com.ipc.uda.service.util.logging.Log;

/**
 * @author mosherc
 */
public final class Actions
{
    private Actions()
    {
        throw new IllegalStateException();
    }

    public static void handleActionException(final Throwable e)
    {
        Log.logger().logEvent("UDAS","ActionException",(Object[])null,e); // TODO: is this exception handling OK?
    }
}



