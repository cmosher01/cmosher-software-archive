/* Copyright (C) 2010 IPC Systems, Inc. All rights reserved. */

package com.ipc.uda.service.util.smc.monitor.listener;

import com.ipc.uda.service.util.logging.Log;
import com.ipc.uda.service.util.smc.monitor.StateChangeListener;

/**
 * @author mosherc
 *
 */
public class StateChangeLogger implements StateChangeListener
{
    private final Object id;

    public StateChangeLogger(final Object id)
    {
        this.id = id;
    }

    @Override
    public void mapChanged(final String map, final boolean entered)
    {
        if (entered)
        {
            Log.logger().debug("UDAS FSM "+this.id+": Entering sub-FSM: "+map);
        }
        else
        {
            Log.logger().debug("UDAS FSM "+this.id+": Exiting sub-FSM: "+map);
        }
    }

    @Override
    public void stateChanged(final String statePrev, final String event, final String stateNext)
    {
        stateChanged(statePrev,event,stateNext,false);
    }

    @Override
    public void undefinedChange(final String statePrev, final String event, final String stateNext)
    {
        stateChanged(statePrev,event,stateNext,true);
    }

    private void stateChanged(final String statePrev, final String event, final String stateNext, final boolean unexpected)
    {
        final StringBuilder sb = new StringBuilder(32);

        if (unexpected)
        {
            sb.append("UNEXPECTED EVENT ");
        }

        if (statePrev == null)
        {
            sb.append("()");
        }
        else
        {
            sb.append("["+statePrev+"]");
        }

        if (event != null)
        {
            sb.append("-->"+event);
        }

        if (stateNext == null)
        {
            sb.append("-->(.)");
        }
        else
        {
            sb.append("-->["+stateNext+"]");
        }

        Log.logger().debug("UDAS FSM "+this.id+": event received: "+sb.toString());
    }
}
