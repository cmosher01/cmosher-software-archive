/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util.smc.monitor;

import statemap.State;



/**
 * @author mosherc
 */
public interface StateChangeMonitor
{
    void undefinedChange(State endState);
    void dispose();
}
