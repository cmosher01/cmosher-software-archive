/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util.smc;

import com.ipc.uda.service.util.smc.monitor.StateChangeMonitor;

import statemap.State;

/**
 * @author mosherc
 *
 */
public class DefaultActions
{
    private StateChangeMonitor monitor;

    public void setStateChangeMonitor(final StateChangeMonitor monitor)
    {
        this.monitor = monitor;
    }

    /**
     * Workaround for smc.sf.net bug 2918863: "clearState not called for actionless transitions."
     * For an action list that would otherwise be empty, use this instead:
     * <code>
     * <pre>
     * { nothing(); }
     * </pre>
     * </code>
     */
    public void nothing()
    {
        // literally do nothing
    }

    /**
     * @param endState
     */
    public void undefinedChange(final State endState)
    {
        if (this.monitor == null)
        {
            return;
        }
        this.monitor.undefinedChange(endState);
    }
}
